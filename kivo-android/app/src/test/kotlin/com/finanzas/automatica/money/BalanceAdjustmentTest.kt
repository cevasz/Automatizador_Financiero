package com.finanzas.automatica.money

import com.finanzas.automatica.domain.model.BalanceAdjustment
import com.finanzas.automatica.domain.model.MovementType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Si el signo del ajuste se invierte, Kivo registra un gasto donde iba un ingreso
 * y el saldo se aleja **el doble** en vez de cuadrar — y el usuario no tendría
 * cómo notarlo salvo volviendo a cuadrar y viendo que empeoró.
 */
class BalanceAdjustmentTest {

    @Test
    fun `si sobra dinero, el ajuste entra como ingreso`() {
        // El caso que reportó el usuario: "tengo 2k más de los que debería".
        val ajuste = BalanceAdjustment.between(realCents = 52_000_00L, kivoCents = 50_000_00L)

        assertEquals(MovementType.INCOME, ajuste.type)
        assertEquals(2_000_00L, ajuste.amountCents)
        assertFalse(ajuste.isNoOp)
    }

    @Test
    fun `si falta dinero, el ajuste entra como gasto`() {
        val ajuste = BalanceAdjustment.between(realCents = 48_000_00L, kivoCents = 50_000_00L)

        assertEquals(MovementType.EXPENSE, ajuste.type)
        assertEquals(2_000_00L, ajuste.amountCents)
    }

    @Test
    fun `el monto nunca es negativo`() {
        // En Kivo el signo lo lleva el `type`, nunca el `amount`. Un monto negativo
        // haría que las sumas de ingresos y gastos se cancelaran solas.
        assertTrue(BalanceAdjustment.between(0L, 99_999_00L).amountCents > 0)
        assertTrue(BalanceAdjustment.between(99_999_00L, 0L).amountCents > 0)
    }

    @Test
    fun `cuando ya cuadra no se registra nada`() {
        val ajuste = BalanceAdjustment.between(realCents = 50_000_00L, kivoCents = 50_000_00L)

        assertTrue(ajuste.isNoOp)
        assertEquals(0L, ajuste.amountCents)
    }

    @Test
    fun `cuadrar desde cero registra todo el saldo real`() {
        // Alguien que instala Kivo hoy y ya tenía plata: su saldo en Kivo es 0 y el
        // real es lo que tenga. El ajuste debe traer todo, no la mitad ni nada.
        val ajuste = BalanceAdjustment.between(realCents = 1_250_000_00L, kivoCents = 0L)

        assertEquals(MovementType.INCOME, ajuste.type)
        assertEquals(1_250_000_00L, ajuste.amountCents)
    }

    @Test
    fun `cuadrar contra un saldo negativo tambien funciona`() {
        // Kivo puede calcular un neto negativo si solo se capturaron gastos.
        val ajuste = BalanceAdjustment.between(realCents = 10_000_00L, kivoCents = -30_000_00L)

        assertEquals(MovementType.INCOME, ajuste.type)
        assertEquals(40_000_00L, ajuste.amountCents)
    }
}
