package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.parser.TransactionLexicon.signal

class DaviplataParser : BaseBankParser(
    bankEntity = BankEntity.DAVIPLATA,
    // com.davivienda.daviplataapp es el paquete real en Google Play (verificado
    // 2026-08-15, ver docs/PENDIENTES.md) -- "com.daviplata.daviplata" no existe.
    // Debe coincidir con notification_listener_config.xml.
    supportedPackageNames = listOf("com.davivienda.daviplataapp", "com.daviplata.daviplata"),
    bankNamePattern = Regex("\\b(daviplata|davivienda)\\b"),
    defaultPaymentMethod = PaymentMethod.DAVIPLATA,
    displayName = "Daviplata",
    baseConfidence = 0.88
) {

    /**
     * DaviPlata mezcla el trato coloquial de billetera ("te enviaron", "sacaste") con
     * términos de Davivienda ("traslado", "recaudo"), y sus retiros en corresponsal o sin
     * tarjeta son de las salidas más frecuentes del producto.
     */
    override val extraSignals = listOf(
        signal("\\bte (enviaron|mandaron|pasaron) (plata|dinero|un giro)\\b", MovementType.INCOME, 6),
        signal("\\b(recaudo|traslado) recibido\\b", MovementType.INCOME, 5),
        signal("\\btraslado (a|hacia) (tu|su) (cuenta|daviplata)\\b", MovementType.INCOME, 5),
        signal("\\bretiro (sin tarjeta|en corresponsal|por cajero)\\b", MovementType.EXPENSE, 6),
        signal("\\b(sacaste|enviaste|pasaste) (plata|dinero)\\b", MovementType.EXPENSE, 6),
        signal("\\bpagaste (con|en|por|tu)\\b", MovementType.EXPENSE, 5)
    )
}
