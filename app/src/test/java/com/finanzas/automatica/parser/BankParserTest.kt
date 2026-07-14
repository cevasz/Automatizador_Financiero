package com.finanzas.automatica.parser

import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.parser.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class BankParserTest {

    private val registry = ParserRegistry.createDefault()

    @Test
    fun `parse Nequi notifications from fixtures`() {
        val fixture = File("app/src/test/resources/fixtures/nequi_notifications.txt").readText()
        val lines = fixture.lines().filter { it.isNotBlank() && !it.startsWith("#") }.toList()

        lines.forEach { line ->
            val result = registry.parse("com.nequi.app", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Nequi: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Bancolombia notifications from fixtures`() {
        val fixture = File("app/src/test/resources/fixtures/bancolombia_notifications.txt").readText()
        val lines = fixture.lines().filter { it.isNotBlank() && !it.startsWith("#") }.toList()

        lines.forEach { line ->
            val result = registry.parse("com.bancolombia.app", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Bancolombia: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Daviplata notifications from fixtures`() {
        val fixture = File("app/src/test/resources/fixtures/daviplata_notifications.txt").readText()
        val lines = fixture.lines().filter { it.isNotBlank() && !it.startsWith("#") }.toList()

        lines.forEach { line ->
            val result = registry.parse("com.daviplata.daviplata", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Daviplata: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Nu notifications from fixtures`() {
        val fixture = File("app/src/test/resources/fixtures/nu_notifications.txt").readText()
        val lines = fixture.lines().filter { it.isNotBlank() && !it.startsWith("#") }.toList()

        lines.forEach { line ->
            val result = registry.parse("com.nu.bank", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Nu: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Lulo notifications from fixtures`() {
        val fixture = File("app/src/test/resources/fixtures/lulo_notifications.txt").readText()
        val lines = fixture.lines().filter { it.isNotBlank() && !it.startsWith("#") }.toList()

        lines.forEach { line ->
            val result = registry.parse("com.lulobank.app", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Lulo: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `unknown package returns failure`() {
        val result = registry.parse("com.unknown.app", "Algun texto $ 100.000")
        assert(result is ParseResult.Failure)
    }
}