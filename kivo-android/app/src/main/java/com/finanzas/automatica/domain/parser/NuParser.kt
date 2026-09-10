package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.parser.TransactionLexicon.signal

class NuParser : BaseBankParser(
    bankEntity = BankEntity.NU,
    // com.nu.production es el paquete real en Google Play, compartido por Nu Colombia,
    // Mexico y Brasil (verificado 2026-08-15, ver docs/PENDIENTES.md) -- "co.nubank" no
    // existe. Debe coincidir con notification_listener_config.xml.
    supportedPackageNames = listOf("com.nu.production", "co.nubank", "br.com.nubank"),
    // Con limite de palabra: sin el, "nu " hacia match dentro de palabras como "menu" y
    // el parser de Nu se quedaba con notificaciones de otros bancos.
    bankNamePattern = Regex("\\b(nu|nubank)\\b"),
    defaultPaymentMethod = PaymentMethod.NU,
    displayName = "Nu Colombia",
    baseConfidence = 0.88
) {

    /**
     * Nu gira alrededor de la tarjeta de crédito y la cuenta remunerada: las compras
     * llegan como "Compra aprobada", los rendimientos diarios como ingreso y el pago de
     * la tarjeta como egreso de la cuenta. Se conservan además las formas de Pix por si
     * la app (compartida con Brasil/México) las emite.
     */
    override val extraSignals = listOf(
        signal("\\bpix recibido\\b", MovementType.INCOME, 6),
        signal("\\b(rendimientos?|intereses) (de|del|diarios|ganados)\\b", MovementType.INCOME, 5),
        signal("\\b(te )?(devolvimos|acreditamos|abonamos)\\b", MovementType.INCOME, 5),
        signal("\\bpix enviado\\b", MovementType.EXPENSE, 6),
        signal("\\bcompra (aprobada|realizada) (en|por|con)\\b", MovementType.EXPENSE, 6),
        signal("\\bpago (de|a) (tu|la) tarjeta\\b", MovementType.EXPENSE, 5),
        signal("\\b(avance|adelanto) de (efectivo|cupo)\\b", MovementType.EXPENSE, 5),
        signal("\\bcuota (de tu compra|del mes)\\b", MovementType.EXPENSE, 4)
    )

    override fun paymentMethodFor(normalized: String): PaymentMethod = when {
        Regex("\\bpix\\b").containsMatchIn(normalized) -> PaymentMethod.PSE
        Regex("\\bqr\\b").containsMatchIn(normalized) -> PaymentMethod.QR
        Regex("\\bpse\\b").containsMatchIn(normalized) -> PaymentMethod.PSE
        else -> PaymentMethod.NU
    }
}
