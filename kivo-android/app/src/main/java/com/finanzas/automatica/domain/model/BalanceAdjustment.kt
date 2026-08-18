package com.finanzas.automatica.domain.model

import kotlin.math.abs

/**
 * El calculo detras de "cuadrar saldo".
 *
 * Vive aparte del ViewModel, como funcion pura sin nada de Android, porque es
 * aritmetica **de dinero**: si el signo se invierte, Kivo registra un gasto donde
 * iba un ingreso y el saldo se aleja el doble en vez de cuadrar. Eso merece
 * tests, y un ViewModel con `Context` y Room encima no se puede probar asi.
 */
data class BalanceAdjustment(
    /** Diferencia con signo, en centavos: positiva si sobraba dinero. */
    val differenceCents: Long
) {
    /** Si sobraba dinero, el ajuste entra como ingreso; si faltaba, como gasto. */
    val type: MovementType
        get() = if (differenceCents > 0) MovementType.INCOME else MovementType.EXPENSE

    /**
     * Monto del movimiento a registrar. Siempre positivo: en Kivo el signo lo
     * lleva el `type`, nunca el `amount` (ver [Movement]). Guardar un monto
     * negativo aqui haria que las sumas de ingresos y gastos se cancelaran solas
     * sin que nada lo delate.
     */
    val amountCents: Long
        get() = abs(differenceCents)

    /** No hay nada que registrar: la cuenta ya cuadraba. */
    val isNoOp: Boolean
        get() = differenceCents == 0L

    companion object {
        /**
         * @param realCents lo que el usuario dice que tiene de verdad.
         * @param kivoCents lo que Kivo calculo sumando lo confirmado.
         */
        fun between(realCents: Long, kivoCents: Long) = BalanceAdjustment(realCents - kivoCents)
    }
}
