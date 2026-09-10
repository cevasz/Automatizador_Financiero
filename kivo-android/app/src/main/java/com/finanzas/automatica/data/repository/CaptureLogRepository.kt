package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.data.local.entity.CaptureLogEntity
import com.finanzas.automatica.domain.model.RawMovement
import com.finanzas.automatica.domain.parser.CaptureDiagnosis
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.presentation.ui.format.Money
import kotlinx.coroutines.flow.Flow

/**
 * Escribe y lee el registro de diagnóstico de captura.
 *
 * Nunca lanza: lo llama `NotificationCaptureService` en segundo plano y un fallo
 * guardando un diagnóstico no puede tumbar la captura del movimiento en sí, que es lo
 * que de verdad importa.
 */
class CaptureLogRepository(
    private val database: FinanzasDatabase
) {
    private val dao = database.captureLogDao()

    fun observeRecent(limit: Int = CaptureLogEntity.MAX_ENTRIES): Flow<List<CaptureLogEntity>> =
        dao.getRecentFlow(limit)

    fun observeIgnoredCount(): Flow<Int> = dao.ignoredCountFlow()

    /** Registra una notificación que sí se convirtió en movimiento. */
    suspend fun logCaptured(packageName: String, text: String, movement: RawMovement) {
        write(
            CaptureLogEntity(
                packageName = packageName,
                bankEntity = movement.bankEntity.name,
                outcome = CaptureLogEntity.OUTCOME_CAPTURED,
                reasonCode = null,
                reason = "Movimiento registrado",
                rawText = text,
                movementSummary = summarize(movement)
            )
        )
    }

    /** Registra una notificación descartada, con el motivo que dio el motor de reglas. */
    suspend fun logIgnored(packageName: String, text: String, diagnosis: CaptureDiagnosis) {
        val rejection = diagnosis.rejection ?: return
        write(
            CaptureLogEntity(
                packageName = packageName,
                bankEntity = diagnosis.bankEntity?.name,
                outcome = CaptureLogEntity.OUTCOME_IGNORED,
                reasonCode = rejection.name,
                reason = rejection.label,
                rawText = text,
                movementSummary = null
            )
        )
    }

    /** Registra una notificación aceptada que después falló al parsearse. */
    suspend fun logFailed(packageName: String, text: String, bankEntity: String?, error: String) {
        write(
            CaptureLogEntity(
                packageName = packageName,
                bankEntity = bankEntity,
                outcome = CaptureLogEntity.OUTCOME_FAILED,
                reasonCode = null,
                reason = error,
                rawText = text,
                movementSummary = null
            )
        )
    }

    suspend fun clear() {
        try {
            dao.deleteAll()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private suspend fun write(entry: CaptureLogEntity) {
        try {
            dao.insert(entry)
            dao.trimTo(CaptureLogEntity.MAX_ENTRIES)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun summarize(movement: RawMovement): String {
        val tipo = if (movement.type == MovementType.INCOME) "Ingreso" else "Egreso"
        return "$tipo ${Money.format(movement.amount)} · ${movement.counterpartyRaw}"
    }
}
