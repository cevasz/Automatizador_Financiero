package com.finanzas.automatica.sync

import com.finanzas.automatica.data.local.entity.AgendaEntryEntity
import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.data.sync.SyncMappers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * La traduccion Room ↔ Postgres es el punto donde la sincronizacion puede
 * corromper datos en silencio: un monto mal escalado, una fecha con la zona
 * horaria equivocada o una llave foranea apuntando al id local en vez de al
 * UUID no fallan en ninguna parte — simplemente dejan datos mal en la nube.
 * Estos tests fijan ese contrato.
 */
class SyncMappersTest {

    private fun movimiento(
        id: Long = 7L,
        categoryId: Long? = null,
        counterpartyId: Long? = null,
        rawText: String = "Recibiste $50.000 de JUAN PEREZ"
    ) = MovementEntity(
        id = id,
        type = "INCOME",
        amount = 50_000_00L,
        paymentMethod = "NEQUI",
        counterpartyRaw = "JUAN PEREZ",
        counterpartyId = counterpartyId,
        categoryId = categoryId,
        date = 1_700_000_000_000L,
        source = "NOTIFICATION",
        confirmationState = "PENDING",
        bankEntity = "NEQUI",
        rawText = rawText,
        createdAt = 1_700_000_000_000L,
        updatedAt = 1_700_000_500_000L,
        syncId = "11111111-1111-4111-8111-111111111111"
    )

    // --- Fechas ---------------------------------------------------------------

    @Test
    fun `las fechas van y vuelven sin perder el instante`() {
        val millis = 1_755_000_123_000L
        val iso = SyncMappers.toIso(millis)
        assertEquals(millis, SyncMappers.fromIso(iso))
    }

    @Test
    fun `acepta el formato con desplazamiento que devuelve Postgres`() {
        // Postgres responde "+00:00", no "Z"; Instant.parse por si solo no lo acepta
        // y la fecha se habria quedado en el valor por defecto.
        assertEquals(
            SyncMappers.fromIso("2026-08-18T12:00:00Z"),
            SyncMappers.fromIso("2026-08-18T07:00:00-05:00")
        )
    }

    @Test
    fun `una fecha ilegible usa el valor por defecto en vez de lanzar`() {
        // Una sola fila con una fecha rara no puede tumbar toda la sincronizacion.
        assertEquals(42L, SyncMappers.fromIso("no es una fecha", porDefecto = 42L))
        assertEquals(42L, SyncMappers.fromIso(null, porDefecto = 42L))
    }

    // --- Movimientos ----------------------------------------------------------

    @Test
    fun `el monto viaja en centavos, sin reescalar`() {
        val json = SyncMappers.movementToJson(movimiento(), { null }, { null }, subirTextoCrudo = true)
        assertEquals("5000000", SyncMappers.texto(json, "amount"))
    }

    @Test
    fun `las llaves foraneas se traducen a UUID, no al id local`() {
        val json = SyncMappers.movementToJson(
            movimiento(categoryId = 3L, counterpartyId = 9L),
            uuidCategoria = { if (it == 3L) "cat-uuid" else null },
            uuidAgenda = { if (it == 9L) "agenda-uuid" else null },
            subirTextoCrudo = true
        )

        assertEquals("cat-uuid", SyncMappers.texto(json, "category_id"))
        assertEquals("agenda-uuid", SyncMappers.texto(json, "counterparty_id"))
    }

    @Test
    fun `una llave foranea sin equivalente sube como null, no como cadena vacia`() {
        // Postgres rechazaria "" como uuid; y un id local colado ahi apuntaria a la
        // fila de otra persona.
        val json = SyncMappers.movementToJson(
            movimiento(categoryId = 3L),
            uuidCategoria = { null },
            uuidAgenda = { null },
            subirTextoCrudo = true
        )
        assertEquals(JsonNull, json["category_id"])
        assertNull(SyncMappers.texto(json, "category_id"))
    }

    @Test
    fun `con el interruptor apagado no se sube el texto del banco`() {
        val json = SyncMappers.movementToJson(movimiento(), { null }, { null }, subirTextoCrudo = false)

        assertEquals("", SyncMappers.texto(json, "raw_text"))
        // El resto del movimiento si se sincroniza: apagar el texto crudo no
        // significa dejar de sincronizar.
        assertEquals("5000000", SyncMappers.texto(json, "amount"))
        assertEquals("JUAN PEREZ", SyncMappers.texto(json, "counterparty_raw"))
    }

    @Test
    fun `el id que viaja es el syncId, no el id de Room`() {
        val json = SyncMappers.movementToJson(movimiento(id = 7L), { null }, { null }, true)
        assertEquals("11111111-1111-4111-8111-111111111111", SyncMappers.texto(json, "id"))
    }

    @Test
    fun `una fila que baja conserva el id local y adopta el syncId remoto`() {
        val remoto = Json.parseToJsonElement(
            """
            {
              "id": "22222222-2222-4222-8222-222222222222",
              "type": "EXPENSE",
              "amount": 12345,
              "payment_method": "BANCOLOMBIA",
              "counterparty_raw": "TIENDA",
              "date": "2026-08-18T12:00:00+00:00",
              "source": "MANUAL",
              "confirmation_state": "CONFIRMED",
              "bank_entity": "BANCOLOMBIA",
              "raw_text": "",
              "created_at": "2026-08-18T12:00:00+00:00",
              "updated_at": "2026-08-18T12:30:00+00:00",
              "deleted": false
            }
            """.trimIndent()
        ).jsonObject

        val entidad = SyncMappers.movementFromJson(remoto, idLocal = 55L, categoriaLocal = 4L, agendaLocal = null)

        assertEquals(55L, entidad.id, "el id de Room no cambia: lo referencian otras tablas")
        assertEquals("22222222-2222-4222-8222-222222222222", entidad.syncId)
        assertEquals(12_345L, entidad.amount)
        assertEquals(4L, entidad.categoryId)
        assertNull(entidad.counterpartyId)
        assertFalse(SyncMappers.estaBorrado(remoto))
    }

    @Test
    fun `una lapida se reconoce como borrada`() {
        val remoto = Json.parseToJsonElement(
            """{"id":"33333333-3333-4333-8333-333333333333","deleted":true}"""
        ).jsonObject
        assertTrue(SyncMappers.estaBorrado(remoto))
    }

    // --- Agenda ---------------------------------------------------------------

    @Test
    fun `el color de la agenda sobrevive al viaje pese al Int con signo de Android`() {
        // 0xFF8D6E63 no cabe en un Int positivo: en Android es negativo. Sin la
        // conversion a sin signo, Postgres recibiria un numero negativo y al
        // volver el contacto quedaria de otro color.
        val original = 0xFF8D6E63.toInt()
        val json = SyncMappers.agendaToJson(
            AgendaEntryEntity(
                id = 1L,
                accountIdentifier = "3001234567",
                displayName = "Mamá",
                color = original,
                syncId = "44444444-4444-4444-8444-444444444444"
            ),
            uuidDe = { null }
        )

        assertEquals("4287458915", SyncMappers.texto(json, "color")) // 0xFF8D6E63 sin signo
        assertEquals(original, SyncMappers.agendaFromJson(json, idLocal = 1L, categoriaLocal = null).color)
    }
}
