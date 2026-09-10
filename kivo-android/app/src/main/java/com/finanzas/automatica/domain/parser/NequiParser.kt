package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.parser.TransactionLexicon.signal

class NequiParser : BaseBankParser(
    bankEntity = BankEntity.NEQUI,
    // com.nequi.MobileApp es el paquete real en Google Play (verificado 2026-08-15,
    // ver docs/PENDIENTES.md) -- "com.nequi.app" no existe y nunca hubiera podido
    // coincidir. Debe coincidir con notification_listener_config.xml.
    supportedPackageNames = listOf("com.nequi.MobileApp", "com.nequi.app"),
    bankNamePattern = Regex("\\bnequi\\b"),
    defaultPaymentMethod = PaymentMethod.NEQUI,
    displayName = "Nequi",
    baseConfidence = 0.90
) {

    /**
     * Nequi escribe en lenguaje coloquial y cambia la redacción con frecuencia: la misma
     * transferencia entrante aparece como "Recibiste", "Te llegaron", "Te enviaron plata"
     * o "Te consignaron" según la versión de la app y el canal de origen. Las formas
     * generales viven en [TransactionLexicon]; aquí solo van las que usan "plata"/"lucas"
     * como objeto directo, típicas de Nequi, y sus productos propios.
     */
    override val extraSignals = listOf(
        signal("\\b(recibiste|te llego|te llegaron|te mandaron|te enviaron) (plata|lucas|un pago)\\b", MovementType.INCOME, 6),
        signal("\\bplata (que )?(te )?(llego|llegaron|entro)\\b", MovementType.INCOME, 5),
        signal("\\b(enviaste|mandaste|sacaste|pagaste) (plata|lucas)\\b", MovementType.EXPENSE, 6),
        signal("\\bpagaste (con|en|por|tu)\\b", MovementType.EXPENSE, 5),
        signal("\\bcompraste (en|con|por)\\b", MovementType.EXPENSE, 5),
        // Bolsillos: mover plata entre el disponible y un bolsillo propio no cambia el
        // patrimonio, pero sí es una salida del disponible; se registra como egreso solo
        // si el texto lo plantea como envío, nunca como ingreso nuevo.
        signal("\\b(metiste|guardaste|mandaste) (plata|dinero) (a|en) (tu|el) bolsillo\\b", MovementType.EXPENSE, 6),
        signal("\\b(sacaste|retiraste) (plata|dinero) (de|del) (tu )?bolsillo\\b", MovementType.INCOME, 6)
    )

}
