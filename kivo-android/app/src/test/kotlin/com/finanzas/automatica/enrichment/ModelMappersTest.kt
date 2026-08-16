package com.finanzas.automatica.enrichment

import com.finanzas.automatica.data.local.entity.MovementEntity
import com.finanzas.automatica.domain.enrichment.toDomain
import com.finanzas.automatica.domain.enrichment.toDomainSafely
import com.finanzas.automatica.domain.model.MovementType
import org.junit.jupiter.api.Test

/**
 * Regresión del bug "la app se cerró y ya no me deja abrirla": una sola fila con un valor
 * que no corresponde a ningún enum (dato corrupto, versión anterior con otro enum, etc.)
 * hacía que `.toDomain()` lanzara dentro del `Flow` que alimenta la lista de movimientos
 * -- ese Flow se suscribe al abrir el Dashboard, sin try/catch alrededor, así que la
 * excepción tumbaba la app en CADA apertura, no solo una vez.
 */
class ModelMappersTest {

    private fun movementEntity(
        id: Long,
        type: String = "EXPENSE",
        paymentMethod: String = "NEQUI",
        source: String = "NOTIFICATION",
        confirmationState: String = "PENDING",
        bankEntity: String = "NEQUI"
    ) = MovementEntity(
        id = id,
        type = type,
        amount = 1_000_00L,
        paymentMethod = paymentMethod,
        counterpartyRaw = "Comercio $id",
        counterpartyId = null,
        categoryId = null,
        date = 1_700_000_000_000L,
        source = source,
        confirmationState = confirmationState,
        bankEntity = bankEntity,
        rawText = "texto crudo $id"
    )

    @Test
    fun `a single corrupted row does not take down the whole list`() {
        val rows = listOf(
            movementEntity(1),
            // Valor que no existe en el enum MovementType -- antes esto reventaba
            // toda la lista (y con ella la pantalla que la mostraba).
            movementEntity(2, type = "VALOR_QUE_NO_EXISTE"),
            movementEntity(3)
        )

        val domain = rows.toDomainSafely { it.toDomain() }

        assert(domain.size == 2) { "se esperaban 2 filas válidas, se obtuvieron ${domain.size}" }
        assert(domain.map { it.id } == listOf(1L, 3L)) { "ids: ${domain.map { it.id }}" }
        assert(domain.all { it.type == MovementType.EXPENSE })
    }

    @Test
    fun `corrupted values in any enum column are tolerated`() {
        val rows = listOf(
            movementEntity(1, paymentMethod = "NO_EXISTE"),
            movementEntity(2, source = "NO_EXISTE"),
            movementEntity(3, confirmationState = "NO_EXISTE"),
            movementEntity(4, bankEntity = "NO_EXISTE"),
            movementEntity(5)
        )

        val domain = rows.toDomainSafely { it.toDomain() }

        assert(domain.size == 1) { "solo la fila 5 es válida, se obtuvieron ${domain.size}" }
        assert(domain.single().id == 5L)
    }

    @Test
    fun `a fully valid list maps every row`() {
        val rows = (1L..5L).map { movementEntity(it) }
        val domain = rows.toDomainSafely { it.toDomain() }
        assert(domain.size == 5)
    }
}
