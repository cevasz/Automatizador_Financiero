package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.parser.TransactionLexicon.signal

class LuloParser : BaseBankParser(
    bankEntity = BankEntity.LULO,
    // co.com.lulobank.production es el paquete real en Google Play (verificado
    // 2026-08-15, ver docs/PENDIENTES.md) -- ni "com.lulobank.app" ni "co.lulobank"
    // existen. Debe coincidir con notification_listener_config.xml.
    supportedPackageNames = listOf("co.com.lulobank.production", "com.lulobank.app", "co.lulobank"),
    bankNamePattern = Regex("\\blulo\\b"),
    defaultPaymentMethod = PaymentMethod.LULO,
    displayName = "Lulo Bank",
    baseConfidence = 0.85
) {

    /**
     * Lulo es 100% digital: casi todo llega como abono/transferencia entre cuentas, pago
     * con la tarjeta débito o retiro en cajero, y paga intereses sobre el saldo (ingreso
     * recurrente que no debe leerse como gasto).
     */
    override val extraSignals = listOf(
        signal("\\b(te depositaron|te transfirieron|entro (plata|dinero))\\b", MovementType.INCOME, 6),
        signal("\\b(intereses|rendimientos) (de tu|del) (ahorro|cuenta|saldo)\\b", MovementType.INCOME, 5),
        signal("\\btransferencia (de|desde) otra? (banco|entidad|cuenta)\\b", MovementType.INCOME, 4),
        signal("\\bcompra (con|en) (tu )?(tarjeta|lulo)\\b", MovementType.EXPENSE, 6),
        signal("\\bpago (qr|con qr|en comercio)\\b", MovementType.EXPENSE, 6),
        signal("\\bretiro (en|por) (cajero|corresponsal)\\b", MovementType.EXPENSE, 6)
    )
}
