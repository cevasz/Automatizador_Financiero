package com.finanzas.automatica.format

import com.finanzas.automatica.presentation.ui.format.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * El dinero es el contenido principal de Kivo: si se formatea mal, el error se
 * ve en cada pantalla y ademas es del tipo que nadie reporta como bug ("me
 * parecio raro el numero"). Estos tests fijan el contrato del unico formateador.
 */
class MoneyTest {

    /** Normaliza el espacio duro (NBSP) que mete NumberFormat entre el simbolo y la cifra. */
    private fun norm(s: String) = s.replace(' ', ' ').replace(" ", " ")

    @Test
    fun `recibe centavos, no pesos`() {
        // 5.000.000 centavos = $50.000. Si alguien pasara pesos, veria $5.000.000
        // en pantalla: es justo la confusion que causaban las divisiones sueltas
        // repartidas por las pantallas.
        assertTrue(norm(Money.format(5_000_000L)).contains("50.000"))
        assertTrue(norm(Money.format(100L)).contains("1"))
    }

    @Test
    fun `no muestra decimales`() {
        // En pesos colombianos los centavos no existen en la practica y solo
        // agregan ruido a cada cifra de la pantalla.
        val texto = norm(Money.format(123_456L))
        assertTrue(!texto.contains(",0") && !texto.contains(".00")) { "no deberia traer decimales: $texto" }
    }

    @Test
    fun `el cero se formatea sin sorpresas`() {
        assertTrue(norm(Money.format(0L)).contains("0"))
    }

    @Test
    fun `el signo lo decide el tipo de movimiento, no el signo del numero`() {
        // Los montos se guardan siempre positivos; lo que dice si suma o resta es
        // el `type` del movimiento. Por eso formatSigned recibe un booleano.
        assertTrue(Money.formatSigned(5_000_000L, isIncome = true).startsWith("+"))
        assertTrue(Money.formatSigned(5_000_000L, isIncome = false).startsWith("−"))
        // Y un monto que por error llegara negativo no debe producir "− − $"
        assertEquals(1, Money.formatSigned(-5_000_000L, isIncome = false).count { it == '−' })
    }

    @Test
    fun `compact acorta lo justo para caber en un eje de grafica`() {
        assertTrue(norm(Money.compact(120_000_000_00L)).contains("M"))   // $120 millones
        assertTrue(norm(Money.compact(50_000_00L)).contains("K"))        // $50 mil
        // Por debajo de mil pesos no se abrevia: "$ 0 K" no le dice nada a nadie.
        assertTrue(!norm(Money.compact(50_000L)).contains("K"))
    }

    @Test
    fun `parsePesos ignora lo que el usuario escriba de mas`() {
        assertEquals(50_000_00L, Money.parsePesos("50.000"))
        assertEquals(50_000_00L, Money.parsePesos("$ 50.000"))
        assertEquals(50_000_00L, Money.parsePesos("50000"))
    }

    @Test
    fun `parsePesos devuelve null en vez de guardar un cero silencioso`() {
        // Devolver 0 haria que un presupuesto vacio se guardara como "limite de
        // cero pesos", que es peor que no guardarlo: se ve como excedido siempre.
        assertNull(Money.parsePesos(""))
        assertNull(Money.parsePesos("   "))
        assertNull(Money.parsePesos("abc"))
    }
}
