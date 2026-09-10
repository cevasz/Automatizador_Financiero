package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.parser.TransactionLexicon.signal

class BancolombiaParser : BaseBankParser(
    bankEntity = BankEntity.BANCOLOMBIA,
    // co.com.bancolombia.personas.superapp es el paquete real de "Mi Bancolombia" en
    // Google Play (verificado 2026-08-15, ver docs/PENDIENTES.md) -- Bancolombia paso
    // de "Bancolombia Personas" (com.todo1.mobile, retirado de la tienda en 2025) a esta
    // app nueva. Ninguno de los paquetes declarados antes coincidia con la app real.
    // Debe coincidir con notification_listener_config.xml.
    supportedPackageNames = listOf(
        "co.com.bancolombia.personas.superapp",
        "com.todo1.mobile",
        "com.bancolombia.personas",
        "com.bancolombia.certipersonas"
    ),
    bankNamePattern = Regex("\\bbancolombia\\b"),
    defaultPaymentMethod = PaymentMethod.BANCOLOMBIA,
    displayName = "Bancolombia",
    baseConfidence = 0.90
) {

    /**
     * Bancolombia usa registro formal e impersonal ("le informa que se realizó...") tanto
     * en la app como en el SMS y el correo, y ese "se + verbo" no lo cubren las formas
     * coloquiales del léxico compartido. También distingue productos (Ahorro a la Mano,
     * tarjeta de crédito, PSE) que aparecen en el mismo texto.
     */
    override val extraSignals = listOf(
        signal("\\bse (realizo|efectuo|registro) (un|una) (abono|consignacion|deposito)\\b", MovementType.INCOME, 6),
        signal("\\b(le|te) (informa|informamos) .{0,40}\\b(abono|consignacion|deposito)\\b", MovementType.INCOME, 5),
        signal("\\bse (realizo|efectuo|registro) (un|una) (compra|pago|retiro|transferencia|avance)\\b", MovementType.EXPENSE, 6),
        signal("\\b(compra|pago|retiro|avance|transferencia) (por|de) valor\\b", MovementType.EXPENSE, 5),
        signal("\\brealizaste (una|un) (compra|pago|transferencia|retiro|avance)\\b", MovementType.EXPENSE, 5),
        signal("\\bpago (de|a) (tu|su) tarjeta de credito\\b", MovementType.EXPENSE, 5),
        signal("\\bcuota de manejo\\b", MovementType.EXPENSE, 5)
    )

    override fun paymentMethodFor(normalized: String): PaymentMethod = when {
        Regex("\\bqr\\b").containsMatchIn(normalized) -> PaymentMethod.QR
        Regex("\\bpse\\b").containsMatchIn(normalized) -> PaymentMethod.PSE
        // Una transferencia de Bancolombia hacia Nequi sigue saliendo de Bancolombia:
        // la entidad es Bancolombia y el medio, la transferencia a la billetera.
        Regex("\\bnequi\\b").containsMatchIn(normalized) -> PaymentMethod.NEQUI
        Regex("\\b(cajero|efectivo|corresponsal)\\b").containsMatchIn(normalized) -> PaymentMethod.CASH
        else -> PaymentMethod.BANCOLOMBIA
    }
}
