package com.finanzas.automatica.data.sync

import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.BudgetEntity
import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.local.entity.ClassificationRuleEntity
import com.finanzas.automatica.data.local.entity.InvoiceEntity
import com.finanzas.automatica.data.local.entity.InvoiceItemEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.data.local.entity.SavingsGoalEntity
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

/**
 * Traduccion entre las entidades de Room y las filas de Postgres.
 *
 * Tres diferencias que hay que salvar en cada tabla:
 *
 *  1. **Identidad**: Room usa un `id` Long autoincremental que solo significa
 *     algo en este telefono; Postgres usa el `syncId` (UUID). Las llaves
 *     foraneas se traducen en los dos sentidos con los mapas que recibe cada
 *     funcion.
 *  2. **Nombres**: camelCase en Kotlin, snake_case en Postgres.
 *  3. **Fechas**: epoch millis en Room, `timestamptz` ISO-8601 en Postgres.
 *
 * Todo aqui es funcion pura sin dependencias de Android, para poder probarlo
 * con tests unitarios normales.
 */
object SyncMappers {

    // --- Fechas ---------------------------------------------------------------

    fun toIso(millis: Long): String = Instant.ofEpochMilli(millis).toString()

    /**
     * Postgres devuelve `2026-08-18T17:04:05.123456+00:00`; `Instant.parse` solo
     * acepta el sufijo `Z`, asi que hace falta `OffsetDateTime`. Si el valor
     * llega ilegible se usa [porDefecto] en vez de lanzar: una fila con una
     * fecha rara no debe tumbar toda la sincronizacion.
     */
    fun fromIso(valor: String?, porDefecto: Long = 0L): Long {
        if (valor.isNullOrBlank()) return porDefecto
        return try {
            OffsetDateTime.parse(valor).toInstant().toEpochMilli()
        } catch (_: DateTimeParseException) {
            try {
                Instant.parse(valor).toEpochMilli()
            } catch (_: DateTimeParseException) {
                porDefecto
            }
        }
    }

    // --- Acceso tolerante a campos -------------------------------------------

    fun texto(o: JsonObject, clave: String): String? =
        (o[clave] as? JsonPrimitive)?.takeIf { it !is JsonNull }?.content

    fun entero(o: JsonObject, clave: String, porDefecto: Long = 0L): Long =
        texto(o, clave)?.toLongOrNull() ?: porDefecto

    fun booleano(o: JsonObject, clave: String, porDefecto: Boolean = false): Boolean =
        texto(o, clave)?.toBooleanStrictOrNull() ?: porDefecto

    private fun JsonObject.borrado(): Boolean = booleano(this, "deleted")

    fun estaBorrado(o: JsonObject): Boolean = o.borrado()

    // --- Categorias -----------------------------------------------------------

    fun categoryToJson(e: CategoryEntity, uuidDe: (Long) -> String?): JsonObject = buildJsonObject {
        put("id", e.syncId)
        put("name", e.name)
        put("type", e.type)
        put("icon_name", e.iconName)
        put("is_custom", e.isCustom)
        putNullable("parent_category_id", e.parentCategoryId?.let(uuidDe))
        put("sort_order", e.sortOrder)
        put("created_at", toIso(e.createdAt))
        // CategoryEntity no tiene updatedAt: se usa createdAt como reloj logico.
        // Es correcto aqui porque una categoria practicamente no se edita; si
        // algun dia se pueden renombrar, hay que agregarle la columna.
        put("updated_at", toIso(e.createdAt))
        put("deleted", false)
    }

    fun categoryFromJson(o: JsonObject, idLocal: Long, idPadreLocal: Long?): CategoryEntity =
        CategoryEntity(
            id = idLocal,
            name = texto(o, "name").orEmpty(),
            type = texto(o, "type") ?: "EXPENSE",
            iconName = texto(o, "icon_name") ?: "category",
            isCustom = booleano(o, "is_custom"),
            parentCategoryId = idPadreLocal,
            sortOrder = entero(o, "sort_order").toInt(),
            createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
            syncId = texto(o, "id").orEmpty()
        )

    // --- Agenda ---------------------------------------------------------------

    fun agendaToJson(e: AgendaEntryEntity, uuidDe: (Long) -> String?): JsonObject = buildJsonObject {
        put("id", e.syncId)
        put("account_identifier", e.accountIdentifier)
        put("display_name", e.displayName)
        putNullable("default_category_id", e.defaultCategoryId?.let(uuidDe))
        // El color es un Int con signo en Android (0xFF8D6E63 desborda Int); en
        // Postgres es bigint sin signo. La conversion se hace en los dos lados.
        put("color", e.color.toLong() and 0xFFFFFFFFL)
        put("origin", e.origin)
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.updatedAt))
        put("deleted", false)
    }

    fun agendaFromJson(o: JsonObject, idLocal: Long, categoriaLocal: Long?): AgendaEntryEntity =
        AgendaEntryEntity(
            id = idLocal,
            accountIdentifier = texto(o, "account_identifier").orEmpty(),
            displayName = texto(o, "display_name").orEmpty(),
            defaultCategoryId = categoriaLocal,
            color = entero(o, "color", 0xFF8D6E63L).toInt(),
            origin = texto(o, "origin") ?: "MANUAL",
            createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
            updatedAt = fromIso(texto(o, "updated_at"), System.currentTimeMillis()),
            syncId = texto(o, "id").orEmpty()
        )

    // --- Movimientos ----------------------------------------------------------

    fun movementToJson(
        e: MovementEntity,
        uuidCategoria: (Long) -> String?,
        uuidAgenda: (Long) -> String?,
        subirTextoCrudo: Boolean
    ): JsonObject = buildJsonObject {
        put("id", e.syncId)
        put("type", e.type)
        put("amount", e.amount)
        put("payment_method", e.paymentMethod)
        put("counterparty_raw", e.counterpartyRaw)
        putNullable("counterparty_id", e.counterpartyId?.let(uuidAgenda))
        putNullable("category_id", e.categoryId?.let(uuidCategoria))
        put("date", toIso(e.date))
        put("source", e.source)
        put("confirmation_state", e.confirmationState)
        put("bank_entity", e.bankEntity)
        put("raw_text", if (subirTextoCrudo) e.rawText else "")
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.updatedAt))
        put("deleted", false)
    }

    fun movementFromJson(
        o: JsonObject,
        idLocal: Long,
        categoriaLocal: Long?,
        agendaLocal: Long?
    ): MovementEntity = MovementEntity(
        id = idLocal,
        type = texto(o, "type") ?: "EXPENSE",
        amount = entero(o, "amount"),
        paymentMethod = texto(o, "payment_method") ?: "OTHER",
        counterpartyRaw = texto(o, "counterparty_raw").orEmpty(),
        counterpartyId = agendaLocal,
        categoryId = categoriaLocal,
        date = fromIso(texto(o, "date"), System.currentTimeMillis()),
        source = texto(o, "source") ?: "MANUAL",
        confirmationState = texto(o, "confirmation_state") ?: "PENDING",
        bankEntity = texto(o, "bank_entity") ?: "UNKNOWN",
        rawText = texto(o, "raw_text").orEmpty(),
        createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
        updatedAt = fromIso(texto(o, "updated_at"), System.currentTimeMillis()),
        syncId = texto(o, "id").orEmpty()
    )

    // --- Presupuestos ---------------------------------------------------------

    fun budgetToJson(e: BudgetEntity, uuidCategoria: (Long) -> String?): JsonObject = buildJsonObject {
        put("id", e.syncId)
        putNullable("category_id", uuidCategoria(e.categoryId))
        put("monthly_limit", e.monthlyLimit)
        put("month", e.month)
        put("year", e.year)
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.createdAt))
        put("deleted", false)
    }

    fun budgetFromJson(o: JsonObject, idLocal: Long, categoriaLocal: Long): BudgetEntity =
        BudgetEntity(
            id = idLocal,
            categoryId = categoriaLocal,
            monthlyLimit = entero(o, "monthly_limit"),
            month = entero(o, "month", 1L).toInt(),
            year = entero(o, "year", 1970L).toInt(),
            createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
            syncId = texto(o, "id").orEmpty()
        )

    // --- Metas ----------------------------------------------------------------

    fun goalToJson(e: SavingsGoalEntity): JsonObject = buildJsonObject {
        put("id", e.syncId)
        put("name", e.name)
        put("target_amount", e.targetAmount)
        put("current_amount", e.currentAmount)
        put("target_date", toIso(e.targetDate))
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.updatedAt))
        put("deleted", false)
    }

    fun goalFromJson(o: JsonObject, idLocal: Long): SavingsGoalEntity = SavingsGoalEntity(
        id = idLocal,
        name = texto(o, "name").orEmpty(),
        targetAmount = entero(o, "target_amount"),
        currentAmount = entero(o, "current_amount"),
        targetDate = fromIso(texto(o, "target_date"), System.currentTimeMillis()),
        createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
        updatedAt = fromIso(texto(o, "updated_at"), System.currentTimeMillis()),
        syncId = texto(o, "id").orEmpty()
    )

    // --- Reglas de clasificacion ---------------------------------------------

    fun ruleToJson(e: ClassificationRuleEntity, uuidCategoria: (Long) -> String?): JsonObject = buildJsonObject {
        put("id", e.syncId)
        put("pattern", e.pattern)
        put("bank_entity", e.bankEntity)
        putNullable("category_id", uuidCategoria(e.categoryId))
        put("priority", e.priority)
        put("is_active", e.isActive)
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.createdAt))
        put("deleted", false)
    }

    fun ruleFromJson(o: JsonObject, idLocal: Long, categoriaLocal: Long): ClassificationRuleEntity =
        ClassificationRuleEntity(
            id = idLocal,
            pattern = texto(o, "pattern").orEmpty(),
            bankEntity = texto(o, "bank_entity") ?: "UNKNOWN",
            categoryId = categoriaLocal,
            priority = entero(o, "priority").toInt(),
            isActive = booleano(o, "is_active", true),
            createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
            syncId = texto(o, "id").orEmpty()
        )

    // --- Facturas -------------------------------------------------------------

    fun invoiceToJson(e: InvoiceEntity, uuidMovimiento: (Long) -> String?): JsonObject = buildJsonObject {
        put("id", e.syncId)
        put("merchant_name", e.merchantName)
        put("date", toIso(e.date))
        put("total_amount", e.totalAmount)
        // Solo la URI, nunca la imagen: subir fotos de comprobantes implica
        // Storage, cuota y muchos mas datos personales. Ver backend/README.md.
        putNullable("image_uri", e.imageUri)
        putNullable("movement_id", e.movementId?.let(uuidMovimiento))
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.createdAt))
        put("deleted", false)
    }

    fun invoiceFromJson(o: JsonObject, idLocal: Long, movimientoLocal: Long?): InvoiceEntity =
        InvoiceEntity(
            id = idLocal,
            merchantName = texto(o, "merchant_name").orEmpty(),
            date = fromIso(texto(o, "date"), System.currentTimeMillis()),
            totalAmount = entero(o, "total_amount"),
            imageUri = texto(o, "image_uri"),
            movementId = movimientoLocal,
            createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
            syncId = texto(o, "id").orEmpty()
        )

    fun invoiceItemToJson(
        e: InvoiceItemEntity,
        uuidFactura: (Long) -> String?,
        uuidCategoria: (Long) -> String?,
        uuidAgenda: (Long) -> String?
    ): JsonObject = buildJsonObject {
        put("id", e.syncId)
        putNullable("invoice_id", uuidFactura(e.invoiceId))
        put("product_name", e.productName)
        put("quantity", e.quantity)
        put("unit_price", e.unitPrice)
        put("total_price", e.totalPrice)
        putNullable("category_id", e.categoryId?.let(uuidCategoria))
        put("is_debt", e.isDebt)
        putNullable("debtor_contact_id", e.debtorContactId?.let(uuidAgenda))
        putNullable("debtor_name", e.debtorName)
        put("debt_status", e.debtStatus)
        putNullable("notes", e.notes)
        put("created_at", toIso(e.createdAt))
        put("updated_at", toIso(e.createdAt))
        put("deleted", false)
    }

    fun invoiceItemFromJson(
        o: JsonObject,
        idLocal: Long,
        facturaLocal: Long,
        categoriaLocal: Long?,
        deudorLocal: Long?
    ): InvoiceItemEntity = InvoiceItemEntity(
        id = idLocal,
        invoiceId = facturaLocal,
        productName = texto(o, "product_name").orEmpty(),
        quantity = entero(o, "quantity", 1L).toInt(),
        unitPrice = entero(o, "unit_price"),
        totalPrice = entero(o, "total_price"),
        categoryId = categoriaLocal,
        isDebt = booleano(o, "is_debt"),
        debtorContactId = deudorLocal,
        debtorName = texto(o, "debtor_name"),
        debtStatus = texto(o, "debt_status") ?: "PENDING",
        notes = texto(o, "notes"),
        createdAt = fromIso(texto(o, "created_at"), System.currentTimeMillis()),
        syncId = texto(o, "id").orEmpty()
    )

}

private fun kotlinx.serialization.json.JsonObjectBuilder.putNullable(clave: String, valor: String?) {
    if (valor == null) put(clave, JsonNull) else put(clave, valor)
}
