package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.MovementType

/**
 * Léxico compartido de señales transaccionales en español colombiano.
 *
 * Antes cada parser tenía su propia cadena de `contains("recibiste") || ...` con tres o
 * cuatro palabras sueltas, así que la misma transacción se clasificaba distinto según el
 * banco y bastaba con que la app cambiara la redacción ("Te llegaron", "Te consignaron",
 * "Te enviaron plata") para que el movimiento no se detectara o se guardara como egreso
 * por el default conservador. Aquí viven las FAMILIAS de verbos, no palabras sueltas, y
 * todos los parsers las comparten; cada banco solo agrega las suyas propias
 * (`extraSignals`) sin mezclar reglas de otras entidades.
 *
 * Reglas de escritura: el texto llega normalizado por [NotificationText] (minúsculas,
 * sin tildes), así que los patrones van sin tildes y SIEMPRE con límite de palabra `\b`.
 * Sin `\b`, "conoce" hacía match dentro de "si no reconoces esta compra" — una frase que
 * viene en notificaciones de compra REALES — y descartaba el egreso entero.
 *
 * El peso resuelve los solapamientos: gana el patrón más específico, no el primero de la
 * lista. "Devolucion de $50.000 en tu compra" pesa más como devolución (ingreso) que como
 * compra (egreso); "recargaste tu celular" pesa más como recarga de celular (egreso) que
 * como recarga de la cuenta (ingreso). A igual peso gana el que aparece primero.
 */
object TransactionLexicon {

    /** Peso mínimo para considerar que el texto describe una transacción sin ambigüedad. */
    const val STRONG_SIGNAL_WEIGHT = 3

    data class Signal(val pattern: Regex, val type: MovementType, val weight: Int)

    data class Detection(
        val type: MovementType,
        val weight: Int,
        val position: Int,
        val matched: String
    ) {
        val isStrong: Boolean get() = weight >= STRONG_SIGNAL_WEIGHT
    }

    fun signal(pattern: String, type: MovementType, weight: Int): Signal =
        Signal(Regex(pattern), type, weight)

    private fun income(pattern: String, weight: Int) = signal(pattern, MovementType.INCOME, weight)
    private fun expense(pattern: String, weight: Int) = signal(pattern, MovementType.EXPENSE, weight)

    /** Dinero que ENTRA a la cuenta del usuario. */
    val INCOME_SIGNALS: List<Signal> = listOf(
        // Formas impersonales ("te ...") — son las más frecuentes en billeteras.
        income("\\bte (llego|llegaron|enviaron|mandaron|consignaron|transfirieron|depositaron|abonaron|devolvieron|pagaron|giraron|regresaron|reintegraron)\\b", 4),
        income("\\bte (hicieron|hizo) (una |un )?(transferencia|envio|giro|deposito|consignacion|pago)\\b", 5),
        income("\\b(llego|llegaron) (tu |la )?(plata|dinero|transferencia|giro|nomina|pago)\\b", 4),
        income("\\brecibiste\\b", 3),
        income("\\b(recibido|recibida|recibidos|recibidas)\\b", 2),
        income("\\btransferencia recibida\\b", 5),
        income("\\b(pago|giro|deposito|envio) recibido\\b", 5),
        // Devoluciones: el texto casi siempre menciona "compra", pero es plata que vuelve.
        income("\\b(devolucion|devolvimos|devuelto|devuelta|reembolso|reintegro|reverso|reversion|reversado|reversada)\\b", 6),
        income("\\b(abono|abonos|abonamos|abonaron|abonado|abonada)\\b", 3),
        income("\\b(consignacion|consignaron|consignado|consignada)\\b", 3),
        income("\\bdeposito\\b", 3),
        income("\\b(cashback|rendimiento|rendimientos|intereses ganados|interes generado|premio|bono|nomina|subsidio)\\b", 3),
        income("\\b(ingreso|ingresaron|entrada de dinero|dinero a tu favor|plata a tu favor|saldo a favor)\\b", 2),
        // Recarga de la cuenta/billetera (la recarga de celular está en los egresos con más peso).
        income("\\b(te recargaron|recargaste tu (nequi|daviplata|cuenta|billetera|bolsillo)|recarga exitosa|recarga de tu cuenta)\\b", 4),
        income("\\brecarga\\b", 1),
        income("\\bventa (exitosa|aprobada)\\b", 3),
        income("\\bcobraste\\b", 3)
    )

    /** Dinero que SALE de la cuenta del usuario. */
    val EXPENSE_SIGNALS: List<Signal> = listOf(
        expense("\\bpagaste\\b", 3),
        expense("\\bpago (exitoso|aprobado|realizado|efectuado|confirmado|programado|automatico|de suscripcion|de factura|de servicio|de servicios|de tu factura)\\b", 4),
        expense("\\b(pagamos|se pago|hiciste un pago|realizaste un pago)\\b", 4),
        expense("\\bpago\\b", 1),
        expense("\\b(compraste|compra (aprobada|exitosa|realizada|confirmada|por|de|en)|realizaste una compra|hiciste una compra)\\b", 4),
        expense("\\bcompra\\b", 2),
        expense("\\b(enviaste|transferiste|mandaste)\\b", 4),
        expense("\\btransferencia (enviada|exitosa|realizada)\\b", 5),
        expense("\\b(envio|giro) (enviado|exitoso|realizado)\\b", 4),
        expense("\\b(retiraste|sacaste|retiro|retiros|avance|avanzaste)\\b", 4),
        expense("\\b(te cobramos|te cobraron|cobro|cobramos|cargo|cargamos|se cargo|descontamos|se descontaron|descuento de tu cuenta|debitamos|se debito|debito automatico|debitado)\\b", 4),
        expense("\\b(suscripcion|domiciliacion|cuota de manejo|comision|cuatro por mil|gmf|impuesto)\\b", 4),
        expense("\\b(consumo|consumiste|gastaste|gasto)\\b", 2),
        expense("\\bpago pse\\b", 4),
        // Recarga de celular/datos: es un gasto aunque comparta el verbo con la recarga
        // de la cuenta, por eso pesa más que la señal de ingreso "recarga".
        expense("\\brecarga(ste|ron|mos)? (tu |el |la |de )?(celular|linea|minutos|datos|movil|plan|tullave|transmilenio)\\b", 6),
        expense("\\brecarga de (celular|minutos|datos)\\b", 6),
        expense("\\bdebito\\b", 2)
    )

    private val BASE_SIGNALS: List<Signal> = INCOME_SIGNALS + EXPENSE_SIGNALS

    /**
     * Marcadores de que NO hubo movimiento de dinero, y que jamás conviven con una
     * transacción efectiva: intentos fallidos, solicitudes de plata de terceros y códigos
     * de seguridad. Registrar estos era la causa de egresos fantasma — un pago rechazado
     * nunca salió de la cuenta, y una "solicitud de dinero" es solo alguien pidiendo.
     */
    private val HARD_BLOCKERS: List<Regex> = listOf(
        Regex("\\b(rechazad|declinad|fallid|denegad|reprobad|no aprobad)[oa]s?\\b"),
        Regex("\\b(no se pudo|no fue posible|no pudimos|sin exito|fallo (el|la|tu)|error en (el|la|tu))\\b"),
        Regex("\\bintento (de|fallido)\\b"),
        Regex("\\b(cancelad|anulad)[oa]s?\\b"),
        Regex("\\b(solicitud|solicitaron|solicito|solicita|pidio|piden|esta pidiendo|te pide) (de )?(dinero|plata|pago|cobro|un pago|una transferencia|que le envies)\\b"),
        // "Fulano te esta pidiendo $20.000" trae verbo y monto pero nadie ha movido plata.
        Regex("\\bte (esta pidiendo|pidio|solicito|solicita|pide|mando un cobro)\\b"),
        Regex("\\b(cobro|pago|transferencia) pendiente\\b"),
        Regex("\\bpendiente de (aprobacion|confirmacion|autorizacion|pago)\\b"),
        // Solo códigos de seguridad: "codigo de autorizacion" SÍ aparece en compras reales.
        Regex("\\bcodigo de (verificacion|seguridad|acceso|confirmacion)\\b"),
        Regex("\\b(tu|el) codigo (es|para)\\b"),
        Regex("\\b(clave dinamica|clave temporal|otp|token de seguridad|contrasena)\\b")
    )

    /**
     * Marcadores que solo descartan la notificación cuando NO hay un verbo transaccional
     * fuerte. Una compra real puede venir con "no olvides" o "vence" en el mismo texto y
     * antes esas frases hacían desaparecer el egreso.
     */
    private val SOFT_BLOCKERS: List<Regex> = listOf(
        Regex("\\b(aprueba|autoriza|confirma|verifica|valida) (tu|el|la|este|esta)\\b"),
        Regex("\\b(recordatorio|recuerda que|proximo pago|no olvides|vence|vencimiento|fecha limite|programaste|programa tu|agendaste)\\b"),
        Regex("\\b(estado de cuenta|extracto|resumen del mes|resumen mensual|informe)\\b"),
        Regex("\\b(inicio de sesion|ingresaste a la app|nuevo dispositivo)\\b")
    )

    /**
     * Marcadores publicitarios. Igual que los [SOFT_BLOCKERS], solo descartan si no hay
     * verbo transaccional fuerte: una compra real puede venir con "a 3 cuotas sin interés"
     * o "ganaste puntos" en el mismo texto.
     */
    private val PROMOTIONAL: List<Regex> = listOf(
        Regex("\\b(descuento|descuentos|promocion|promociones|promo|cupon|cupones|oferta|ofertas|sorteo|rifa|premios)\\b"),
        Regex("\\b(ganate|participa|aprovecha|descubre|conoce|invita|refiere|activalo|actualiza|disfruta|llevate)\\b"),
        Regex("\\b(puntos|beneficios|preaprobado|cupo disponible|te prestamos|credito preaprobado)\\b"),
        Regex("\\b(nueva version|actualiza tu app|actualiza la app|terminos y condiciones|encuesta|califica tu experiencia|novedades)\\b")
    )

    /**
     * Vocabulario financiero genérico. Es la última red: si hay monto pero ningún verbo
     * conocido, al menos exige que el texto hable de dinero antes de considerarlo.
     */
    private val GENERIC_FINANCIAL = Regex(
        "\\b(transaccion|transferencia|movimiento|cuenta|tarjeta|bolsillo|billetera|" +
            "saldo|pse|qr|datafono|cajero|comercio|factura|plata|dinero|pesos)\\b"
    )

    /** Devuelve la señal más específica encontrada, o `null` si el texto no tiene ninguna. */
    fun detect(normalized: String, extraSignals: List<Signal> = emptyList()): Detection? {
        var best: Detection? = null
        for (signal in extraSignals + BASE_SIGNALS) {
            val match = signal.pattern.find(normalized) ?: continue
            val candidate = Detection(signal.type, signal.weight, match.range.first, match.value)
            val current = best
            val wins = current == null ||
                candidate.weight > current.weight ||
                (candidate.weight == current.weight && candidate.position < current.position)
            if (wins) best = candidate
        }
        return best
    }

    fun isHardBlocked(normalized: String): Boolean =
        HARD_BLOCKERS.any { it.containsMatchIn(normalized) }

    fun isSoftBlocked(normalized: String): Boolean =
        SOFT_BLOCKERS.any { it.containsMatchIn(normalized) }

    fun isPromotional(normalized: String): Boolean =
        PROMOTIONAL.any { it.containsMatchIn(normalized) }

    fun hasGenericFinancialContext(normalized: String): Boolean =
        GENERIC_FINANCIAL.containsMatchIn(normalized)
}
