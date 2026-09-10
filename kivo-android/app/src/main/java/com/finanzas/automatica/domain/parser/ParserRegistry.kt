package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.ParseResult
import java.time.Instant

/**
 * Diagnóstico de una notificación: qué banco la reconoció (si alguno) y qué se decidió.
 *
 * @param parser el parser que la va a procesar, o `null` si nadie la acepta.
 * @param bankEntity banco al que corresponde el motivo mostrado, aunque se haya
 *   descartado — es lo que permite decir "Nequi la ignoró porque..." en vez de un
 *   genérico "no se registró".
 */
data class CaptureDiagnosis(
    val parser: BankParser?,
    val bankEntity: BankEntity?,
    val decision: CaptureDecision
) {
    val accepted: Boolean get() = decision is CaptureDecision.Accepted

    val rejection: CaptureRejection?
        get() = (decision as? CaptureDecision.Rejected)?.reason

    /** `true` si el texto de esta notificación puede guardarse en el registro local. */
    val loggable: Boolean get() = accepted || rejection?.loggable == true
}

class ParserRegistry(private val parsers: List<BankParser>) {

    fun parse(
        packageName: String,
        notificationText: String,
        occurredAt: Instant? = null
    ): ParseResult {
        val parser = select(packageName, notificationText)
            ?: return ParseResult.Failure("No parser found for package: $packageName", notificationText)

        return parser.parse(notificationText, occurredAt)
    }

    fun canParseAny(packageName: String, notificationText: String): Boolean {
        return parsers.any { it.canParse(packageName, notificationText) }
    }

    fun getSupportedPackages(): List<String> = parsers.flatMap { it.supportedPackageNames }.distinct()

    /**
     * Explica qué pasó con una notificación.
     *
     * Cuando nadie la acepta, se devuelve el motivo del parser que MÁS avanzó en la
     * evaluación (el de mayor `ordinal` en [CaptureRejection], que sigue el orden de los
     * pasos): si Nequi la descartó porque "no trae monto" y los otros cuatro bancos
     * porque "no es de un banco reconocido", el motivo útil es el de Nequi.
     */
    fun diagnose(packageName: String, notificationText: String): CaptureDiagnosis {
        val evaluations = parsers.map { it to it.evaluate(packageName, notificationText) }

        val accepted = evaluations.filter { it.second is CaptureDecision.Accepted }
        if (accepted.isNotEmpty()) {
            val chosen = select(packageName, notificationText) ?: accepted.first().first
            return CaptureDiagnosis(chosen, chosen.bankEntity, CaptureDecision.Accepted)
        }

        val furthest = evaluations
            .mapNotNull { (parser, decision) ->
                (decision as? CaptureDecision.Rejected)?.let { parser to it }
            }
            .maxByOrNull { it.second.reason.ordinal }
            ?: return CaptureDiagnosis(null, null, CaptureDecision.Rejected(CaptureRejection.OTHER_APP))

        val (parser, rejection) = furthest
        // Solo se nombra el banco si de verdad lo reconoció; con OTHER_APP el "banco" es
        // apenas el primero de la lista y decirlo confundiría al leer el diagnóstico.
        val bank = if (rejection.reason == CaptureRejection.OTHER_APP) null else parser.bankEntity
        return CaptureDiagnosis(null, bank, rejection)
    }

    /**
     * Elige el parser correcto cuando más de uno reconoce la notificación.
     *
     * Antes se tomaba el primero de la lista que dijera "sí", y como el reconocimiento
     * también acepta el nombre del banco dentro del texto, una notificación de
     * Bancolombia que dijera "Transferiste $50.000 a tu Nequi" la terminaba procesando
     * el parser de Nequi: el movimiento quedaba con la entidad equivocada y se
     * contabilizaba dos veces cuando Nequi mandaba su propia notificación.
     *
     * Orden: manda el paquete real que emitió la notificación; si no coincide con
     * ninguno (SMS, correo, OCR), gana el banco que se nombra primero en el texto, que
     * es casi siempre el que reporta.
     */
    private fun select(packageName: String, notificationText: String): BankParser? {
        val matching = parsers.filter { it.canParse(packageName, notificationText) }
        if (matching.size <= 1) return matching.firstOrNull()

        matching.firstOrNull { parser ->
            parser.supportedPackageNames.any { packageName.contains(it, ignoreCase = true) }
        }?.let { return it }

        val normalized = NotificationText.normalize(notificationText)
        return matching.minByOrNull { parser ->
            parser.bankNamePattern.find(normalized)?.range?.first ?: Int.MAX_VALUE
        }
    }

    companion object {
        fun createDefault(): ParserRegistry {
            return ParserRegistry(listOf(
                NequiParser(),
                BancolombiaParser(),
                DaviplataParser(),
                NuParser(),
                LuloParser()
            ))
        }
    }
}
