package com.finanzas.automatica.data.sync

import com.finanzas.automatica.data.local.FinanzasDatabase
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import java.time.OffsetDateTime

/** Resultado visible de una sincronizacion, para poder decirle al usuario que paso. */
data class SyncResult(
    val bajados: Int,
    val subidos: Int,
    val borradosPropagados: Int
)

/**
 * Motor de sincronizacion entre Room y Supabase.
 *
 * **Orden: primero bajar, despues subir.** No es indiferente. Un telefono que
 * entra por primera vez a una cuenta que ya tiene datos siembra sus propias 33
 * categorias por defecto con UUID distintos a los de la nube; si subiera
 * primero, chocaria contra los indices unicos del servidor (categoria por
 * nombre, contacto por identificador, presupuesto por periodo) y la
 * transaccion entera fallaria. Bajando primero, [aplicarCambios] reconcilia
 * esas filas por su llave natural y **adopta el UUID de la nube**, con lo que
 * la subida posterior ya no tiene nada con que chocar.
 *
 * Conflictos: gana la escritura mas reciente (`updatedAt`). Es la estrategia
 * correcta para este caso porque los dos lados casi nunca editan lo mismo — el
 * telefono captura, la web corrige — y el costo de equivocarse es perder una
 * recategorizacion, no un movimiento.
 */
class SyncEngine(
    private val db: FinanzasDatabase,
    private val client: SupabaseClient,
    private val store: SyncStore
) {

    private val sync get() = db.syncDao()
    private val lapidas get() = db.syncDeletionDao()

    // --- Sesion ---------------------------------------------------------------

    suspend fun signIn(email: String, password: String) {
        store.save(client.signIn(email.trim(), password))
    }

    suspend fun signUp(email: String, password: String) {
        store.save(client.signUp(email.trim(), password))
    }

    fun signOut() = store.clear()

    /**
     * Devuelve un access token utilizable, renovandolo si esta por caducar.
     * Los access token de Supabase duran una hora; sin esto, la sincronizacion
     * fallaria con 401 en cualquier sesion de mas de ese tiempo.
     */
    private suspend fun tokenValido(): String {
        if (!store.isSignedIn) throw SupabaseException("No hay sesión. Entra con tu cuenta de Kivo.")
        if (store.needsRefresh) store.save(client.refresh(store.refreshToken))
        return store.accessToken
    }

    // --- Sincronizacion completa ---------------------------------------------

    suspend fun sync(): SyncResult {
        val token = tokenValido()

        val bajados = bajarCambios(token)
        val (subidos, borrados) = subirCambios(token)

        store.lastSyncAt = System.currentTimeMillis()
        return SyncResult(bajados = bajados, subidos = subidos, borradosPropagados = borrados)
    }

    // --- Bajada ---------------------------------------------------------------

    private suspend fun bajarCambios(token: String): Int {
        val respuesta = client.rpc(
            "kivo_pull_changes",
            buildJsonObject {
                // "-infinity" en vez de vacio: es el valor de timestamptz que
                // Postgres entiende como "desde siempre", para el primer pull.
                put("since", store.pullCursor.ifBlank { "-infinity" })
            },
            token
        ).jsonObject

        val tablas = respuesta["tablas"]?.jsonObject ?: JsonObject(emptyMap())
        val aplicados = aplicarCambios(tablas)

        SyncMappers.texto(respuesta, "server_time")?.let { store.pullCursor = conSolape(it) }
        return aplicados
    }

    /**
     * Retrocede el cursor unos segundos antes de guardarlo.
     *
     * Una transaccion que empezo antes que el pull pero confirmo despues puede
     * quedar justo fuera del corte y no volver a aparecer nunca, porque su
     * `synced_at` ya es anterior al cursor. Como los upserts son idempotentes,
     * volver a bajar unas filas repetidas no cuesta nada; perderlas si.
     */
    private fun conSolape(serverTime: String): String = try {
        OffsetDateTime.parse(serverTime).minusSeconds(SOLAPE_SEGUNDOS).toString()
    } catch (_: Exception) {
        serverTime
    }

    private suspend fun aplicarCambios(tablas: JsonObject): Int {
        var aplicados = 0

        // Mapas UUID → id local que se van llenando en orden de dependencia.
        val categorias = mutableMapOf<String, Long>()
        val agenda = mutableMapOf<String, Long>()
        val movimientos = mutableMapOf<String, Long>()
        val facturas = mutableMapOf<String, Long>()

        // Un pull incremental puede traer un movimiento cuya categoria no viene
        // en esta tanda (no cambio). Por eso, si no esta en el mapa, se busca en
        // la base local antes de darla por inexistente.
        suspend fun idCategoria(uuid: String?): Long? = uuid?.let {
            categorias[it] ?: sync.categoryBySyncId(it)?.id
        }
        suspend fun idAgenda(uuid: String?): Long? = uuid?.let {
            agenda[it] ?: sync.agendaBySyncId(it)?.id
        }
        suspend fun idMovimiento(uuid: String?): Long? = uuid?.let {
            movimientos[it] ?: sync.movementBySyncId(it)?.id
        }
        suspend fun idFactura(uuid: String?): Long? = uuid?.let {
            facturas[it] ?: sync.invoiceBySyncId(it)?.id
        }

        // 1. Categorias -------------------------------------------------------
        filas(tablas, "categories").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteCategory(uuid); aplicados++; return@forEach
            }

            val nombre = SyncMappers.texto(fila, "name").orEmpty()
            val tipo = SyncMappers.texto(fila, "type") ?: "EXPENSE"
            val existente = sync.categoryBySyncId(uuid)
                ?: sync.categoryByNaturalKey(nombre, tipo)

            val padre = idCategoria(SyncMappers.texto(fila, "parent_category_id"))
            val remota = SyncMappers.fromIso(SyncMappers.texto(fila, "updated_at"))

            if (existente == null) {
                val id = sync.insertCategory(SyncMappers.categoryFromJson(fila, 0L, padre))
                categorias[uuid] = id
                aplicados++
            } else {
                categorias[uuid] = existente.id
                // Aunque el reloj remoto no gane, si el syncId local es otro hay
                // que adoptar el de la nube: es lo que hace que la subida
                // posterior actualice esa fila en vez de crear un duplicado (el
                // caso de las 33 categorias que cada telefono siembra por su
                // cuenta con UUID distintos).
                if (remota > existente.createdAt || existente.syncId != uuid) {
                    sync.updateCategory(SyncMappers.categoryFromJson(fila, existente.id, padre))
                    aplicados++
                }
            }
        }

        // 2. Agenda -----------------------------------------------------------
        filas(tablas, "agenda_entries").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteAgenda(uuid); aplicados++; return@forEach
            }

            val identificador = SyncMappers.texto(fila, "account_identifier").orEmpty()
            val existente = sync.agendaBySyncId(uuid) ?: sync.agendaByNaturalKey(identificador)
            val categoria = idCategoria(SyncMappers.texto(fila, "default_category_id"))
            val entidad = SyncMappers.agendaFromJson(fila, existente?.id ?: 0L, categoria)

            if (existente == null) {
                agenda[uuid] = sync.insertAgenda(entidad)
                aplicados++
            } else {
                agenda[uuid] = existente.id
                if (entidad.updatedAt > existente.updatedAt || existente.syncId != uuid) {
                    sync.updateAgenda(entidad)
                    aplicados++
                }
            }
        }

        // 3. Movimientos ------------------------------------------------------
        filas(tablas, "movements").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteMovement(uuid); aplicados++; return@forEach
            }

            val existente = sync.movementBySyncId(uuid)
            val entidad = SyncMappers.movementFromJson(
                fila,
                idLocal = existente?.id ?: 0L,
                categoriaLocal = idCategoria(SyncMappers.texto(fila, "category_id")),
                agendaLocal = idAgenda(SyncMappers.texto(fila, "counterparty_id"))
            )

            if (existente == null) {
                movimientos[uuid] = sync.insertMovement(entidad)
                aplicados++
            } else {
                movimientos[uuid] = existente.id
                if (entidad.updatedAt > existente.updatedAt) {
                    sync.updateMovement(entidad)
                    aplicados++
                }
            }
        }

        // 4. Presupuestos -----------------------------------------------------
        filas(tablas, "budgets").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteBudget(uuid); aplicados++; return@forEach
            }

            // Un presupuesto sin categoria local no se puede guardar: la columna
            // es NOT NULL y con llave foranea. Se ignora y volvera a bajar en la
            // proxima sincronizacion, cuando la categoria ya exista.
            val categoria = idCategoria(SyncMappers.texto(fila, "category_id")) ?: return@forEach
            val mes = SyncMappers.entero(fila, "month", 1L).toInt()
            val anio = SyncMappers.entero(fila, "year", 1970L).toInt()

            val existente = sync.budgetBySyncId(uuid) ?: sync.budgetByNaturalKey(categoria, mes, anio)
            val entidad = SyncMappers.budgetFromJson(fila, existente?.id ?: 0L, categoria)

            if (existente == null) {
                sync.insertBudget(entidad); aplicados++
            } else if (entidad.createdAt > existente.createdAt || existente.syncId != uuid) {
                sync.updateBudget(entidad); aplicados++
            }
        }

        // 5. Metas ------------------------------------------------------------
        filas(tablas, "savings_goals").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteGoal(uuid); aplicados++; return@forEach
            }

            val existente = sync.goalBySyncId(uuid)
            val entidad = SyncMappers.goalFromJson(fila, existente?.id ?: 0L)

            if (existente == null) {
                sync.insertGoal(entidad); aplicados++
            } else if (entidad.updatedAt > existente.updatedAt) {
                sync.updateGoal(entidad); aplicados++
            }
        }

        // 6. Reglas de clasificacion ------------------------------------------
        filas(tablas, "classification_rules").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteRule(uuid); aplicados++; return@forEach
            }

            val categoria = idCategoria(SyncMappers.texto(fila, "category_id")) ?: return@forEach
            val existente = sync.ruleBySyncId(uuid)
            val entidad = SyncMappers.ruleFromJson(fila, existente?.id ?: 0L, categoria)

            if (existente == null) {
                sync.insertRule(entidad); aplicados++
            } else if (entidad.createdAt > existente.createdAt) {
                sync.updateRule(entidad); aplicados++
            }
        }

        // 7. Facturas ---------------------------------------------------------
        filas(tablas, "invoices").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteInvoice(uuid); aplicados++; return@forEach
            }

            val existente = sync.invoiceBySyncId(uuid)
            val entidad = SyncMappers.invoiceFromJson(
                fila,
                idLocal = existente?.id ?: 0L,
                movimientoLocal = idMovimiento(SyncMappers.texto(fila, "movement_id"))
            )

            if (existente == null) {
                facturas[uuid] = sync.insertInvoice(entidad)
                aplicados++
            } else {
                facturas[uuid] = existente.id
                if (entidad.createdAt > existente.createdAt) {
                    sync.updateInvoice(entidad); aplicados++
                }
            }
        }

        // 8. Productos de factura ---------------------------------------------
        filas(tablas, "invoice_items").forEach { fila ->
            val uuid = SyncMappers.texto(fila, "id") ?: return@forEach
            if (SyncMappers.estaBorrado(fila)) {
                sync.deleteInvoiceItem(uuid); aplicados++; return@forEach
            }

            val factura = idFactura(SyncMappers.texto(fila, "invoice_id")) ?: return@forEach
            val existente = sync.invoiceItemBySyncId(uuid)
            val entidad = SyncMappers.invoiceItemFromJson(
                fila,
                idLocal = existente?.id ?: 0L,
                facturaLocal = factura,
                categoriaLocal = idCategoria(SyncMappers.texto(fila, "category_id")),
                deudorLocal = idAgenda(SyncMappers.texto(fila, "debtor_contact_id"))
            )

            if (existente == null) {
                sync.insertInvoiceItem(entidad); aplicados++
            } else if (entidad.createdAt > existente.createdAt) {
                sync.updateInvoiceItem(entidad); aplicados++
            }
        }

        return aplicados
    }

    private fun filas(tablas: JsonObject, nombre: String): List<JsonObject> =
        (tablas[nombre] as? JsonArray)?.mapNotNull { it as? JsonObject }.orEmpty()

    // --- Subida ---------------------------------------------------------------

    private suspend fun subirCambios(token: String): Pair<Int, Int> {
        val categorias = sync.allCategories()
        val agenda = sync.allAgendaEntries()
        val movimientos = sync.allMovements()
        val presupuestos = sync.allBudgets()
        val metas = sync.allSavingsGoals()
        val reglas = sync.allRules()
        val facturas = sync.allInvoices()
        val productos = sync.allInvoiceItems()

        val uuidCategoria = categorias.associate { it.id to it.syncId }
        val uuidAgenda = agenda.associate { it.id to it.syncId }
        val uuidMovimiento = movimientos.associate { it.id to it.syncId }
        val uuidFactura = facturas.associate { it.id to it.syncId }

        val subirCrudo = store.uploadRawText

        val tablas = buildJsonObject {
            put("categories", arreglo(categorias.map { SyncMappers.categoryToJson(it, uuidCategoria::get) }))
            put("agenda_entries", arreglo(agenda.map { SyncMappers.agendaToJson(it, uuidCategoria::get) }))
            put("movements", arreglo(movimientos.map {
                SyncMappers.movementToJson(it, uuidCategoria::get, uuidAgenda::get, subirCrudo)
            }))
            put("budgets", arreglo(presupuestos.map { SyncMappers.budgetToJson(it, uuidCategoria::get) }))
            put("savings_goals", arreglo(metas.map { SyncMappers.goalToJson(it) }))
            put("classification_rules", arreglo(reglas.map { SyncMappers.ruleToJson(it, uuidCategoria::get) }))
            put("invoices", arreglo(facturas.map { SyncMappers.invoiceToJson(it, uuidMovimiento::get) }))
            put("invoice_items", arreglo(productos.map {
                SyncMappers.invoiceItemToJson(it, uuidFactura::get, uuidCategoria::get, uuidAgenda::get)
            }))
        }

        val pendientesDeBorrar = lapidas.getAll()
        val borrados = buildJsonObject {
            pendientesDeBorrar.groupBy { it.tableName }.forEach { (tabla, filas) ->
                put(tabla, arreglo(filas.map { kotlinx.serialization.json.JsonPrimitive(it.syncId) }))
            }
        }

        client.rpc(
            "kivo_push_changes",
            buildJsonObject {
                put("payload", buildJsonObject {
                    put("tablas", tablas)
                    put("borrados", borrados)
                })
            },
            token
        )

        // Las lapidas se borran solo despues de que el servidor confirmo. Si la
        // peticion falla, la excepcion sube y quedan para el proximo intento.
        if (pendientesDeBorrar.isNotEmpty()) {
            lapidas.deleteBySyncIds(pendientesDeBorrar.map { it.syncId })
        }

        val subidos = categorias.size + agenda.size + movimientos.size + presupuestos.size +
            metas.size + reglas.size + facturas.size + productos.size

        return subidos to pendientesDeBorrar.size
    }

    private fun arreglo(elementos: List<JsonElement>): JsonArray = buildJsonArray { elementos.forEach { add(it) } }

    private companion object {
        const val TAG = "SyncEngine"
        const val SOLAPE_SEGUNDOS = 10L
    }
}
