package com.finanzas.automatica.sync

import com.finanzas.automatica.presentation.viewmodel.ClassificationRulesViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Una regla con una expresion invalida no falla al guardarla: falla mucho
 * despues, dentro de `DefaultClassificationEngine`, donde
 * `runCatching { rule.pattern.toRegex() }.getOrNull() ?: continue` la descarta
 * **en silencio**. El usuario veria su regla en la lista, activa, sin clasificar
 * nada nunca y sin ningun mensaje. Por eso se valida al guardar.
 */
class ClassificationRuleValidationTest {

    @Test
    fun `un patron vacio se rechaza`() {
        assertNotNull(ClassificationRulesViewModel.validar("   "))
    }

    @Test
    fun `una expresion regular rota se rechaza al guardar`() {
        assertNotNull(ClassificationRulesViewModel.validar("RAPPI("))
        assertNotNull(ClassificationRulesViewModel.validar("[a-"))
    }

    @Test
    fun `el texto suelto es una regla valida`() {
        // Lo mas comun es que alguien escriba solo "RAPPI", sin saber que es una
        // expresion regular. Tiene que funcionar sin ceremonia.
        assertNull(ClassificationRulesViewModel.validar("RAPPI"))
    }

    @Test
    fun `el probador replica lo que hara el motor de clasificacion`() {
        val texto = "Bancolombia: Compra por $32.900 en RAPPI COLOMBIA el 18/08"

        // containsMatchIn, no matches: el patron se busca DENTRO del texto crudo.
        assertEquals(true, ClassificationRulesViewModel.coincide("RAPPI", texto))
        assertEquals(false, ClassificationRulesViewModel.coincide("UBER", texto))
        assertEquals(true, ClassificationRulesViewModel.coincide("RAPPI|UBER|DIDI", texto))
    }

    @Test
    fun `el probador devuelve null si la expresion no compila, no lanza`() {
        assertNull(ClassificationRulesViewModel.coincide("RAPPI(", "cualquier cosa"))
    }

    @Test
    fun `distingue mayusculas, igual que el motor`() {
        // El motor no normaliza el patron; si alguien escribe "rappi" en
        // minusculas no va a coincidir con "RAPPI". El probador lo hace evidente
        // antes de guardar, en vez de dejarlo como una regla muerta.
        val texto = "Compra en RAPPI COLOMBIA"
        assertTrue(ClassificationRulesViewModel.coincide("RAPPI", texto) == true)
        assertFalse(ClassificationRulesViewModel.coincide("rappi", texto) == true)
    }
}
