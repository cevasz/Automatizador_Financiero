package com.finanzas.automatica.importer

import com.finanzas.automatica.domain.importer.PdfStatementExtractor
import com.finanzas.automatica.domain.importer.StatementImporter
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class PdfStatementExtractorTest {

    @Test
    fun `extract statement text from real PDF fixture and classify it`() {
        val fixture = javaClass.classLoader
            .getResourceAsStream("fixtures/extracto_bancolombia.pdf")!!
            .use { it.readBytes() }

        val text = PdfStatementExtractor.extractText(fixture)
        assert(text.contains("Transferencia LUIS RINCON"))

        // El texto extraído se alimenta al mismo motor de parseo/clasificación.
        val summary = StatementImporter.parseStatementText(text, BankEntity.BANCOLOMBIA)

        assert(summary.totalCount == 3)
        assert(summary.incomeCount == 1)
        assert(summary.expenseCount == 2)
        assert(summary.totalIncomeAmount == 10000000L) // $100.000 COP en centavos
        assert(summary.totalExpenseAmount == 16500000L) // $165.000 COP en centavos

        val first = summary.importedMovements.first()
        assert(first.type == MovementType.INCOME)
        assert(first.amount == 10000000L)
        assert(first.counterpartyRaw == "Transferencia LUIS RINCON")
    }

    @Test
    fun `unprotected PDF does not require a password`() {
        val fixture = javaClass.classLoader
            .getResourceAsStream("fixtures/extracto_bancolombia.pdf")!!
            .use { it.readBytes() }

        assert(!PdfStatementExtractor.requiresPassword(fixture))
    }

    @Test
    fun `password-protected PDF is detected and decrypted with the right password`() {
        // Muchos extractos bancarios colombianos vienen protegidos con contraseña (p.ej.
        // la cédula del titular). Se cifra el fixture real en memoria con pdfbox mismo
        // para probar el ciclo completo de deteccion + descifrado sin inventar un
        // "formato" de banco -- la proteccion por contraseña es una capacidad generica
        // de PDF, no algo especifico de Bancolombia.
        val fixture = javaClass.classLoader
            .getResourceAsStream("fixtures/extracto_bancolombia.pdf")!!
            .use { it.readBytes() }

        val protectedBytes = PDDocument.load(fixture).use { document ->
            val policy = StandardProtectionPolicy("owner123", "1234567890", AccessPermission())
            document.protect(policy)
            ByteArrayOutputStream().use { out ->
                document.save(out)
                out.toByteArray()
            }
        }

        assert(PdfStatementExtractor.requiresPassword(protectedBytes))

        val text = PdfStatementExtractor.extractText(protectedBytes, "1234567890")
        assert(text.contains("Transferencia LUIS RINCON"))
    }

    @Test
    fun `wrong password throws InvalidPasswordException instead of failing silently`() {
        val fixture = javaClass.classLoader
            .getResourceAsStream("fixtures/extracto_bancolombia.pdf")!!
            .use { it.readBytes() }

        val protectedBytes = PDDocument.load(fixture).use { document ->
            val policy = StandardProtectionPolicy("owner123", "1234567890", AccessPermission())
            document.protect(policy)
            ByteArrayOutputStream().use { out ->
                document.save(out)
                out.toByteArray()
            }
        }

        try {
            PdfStatementExtractor.extractText(protectedBytes, "clave-incorrecta")
            assert(false) { "Se esperaba InvalidPasswordException" }
        } catch (e: InvalidPasswordException) {
            // esperado
        }
    }
}
