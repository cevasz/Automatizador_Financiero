package com.finanzas.automatica.parser

import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.parser.*
import org.junit.jupiter.api.Test
import java.io.File

class BankParserTest {

    private val registry = ParserRegistry.createDefault()
    private val fixtureDir = File("src/test/resources/fixtures")

    @Test
    fun `parse Nequi notifications from fixtures`() {
        val fixture = File(fixtureDir, "nequi_notifications.txt").readText()
        val lines = fixture.lines()
            .filter { it.isNotBlank() && !it.startsWith("#") && it != "Nequi" }
            .toList()

        lines.forEach { line ->
            val result = registry.parse("com.nequi.app", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Nequi: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Bancolombia notifications from fixtures`() {
        val fixture = File(fixtureDir, "bancolombia_notifications.txt").readText()
        val lines = fixture.lines()
            .filter { it.isNotBlank() && !it.startsWith("#") && it != "Bancolombia" }
            .toList()

        lines.forEach { line ->
            val result = registry.parse("com.bancolombia.certipersonas", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Bancolombia: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Daviplata notifications from fixtures`() {
        val fixture = File(fixtureDir, "daviplata_notifications.txt").readText()
        val lines = fixture.lines()
            .filter { it.isNotBlank() && !it.startsWith("#") && it != "Daviplata" }
            .toList()

        lines.forEach { line ->
            val result = registry.parse("com.daviplata.daviplata", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Daviplata: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Nu notifications from fixtures`() {
        val fixture = File(fixtureDir, "nu_notifications.txt").readText()
        val lines = fixture.lines()
            .filter { it.isNotBlank() && !it.startsWith("#") && it != "Nu Colombia" }
            .toList()

        lines.forEach { line ->
            val result = registry.parse("co.nubank", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            println("Nu: $line -> ${movement.type} ${movement.amount} ${movement.counterpartyRaw}")
        }
    }

    @Test
    fun `parse Lulo notifications from fixtures`() {
        val fixture = File(fixtureDir, "lulo_notifications.txt").readText()
        val lines = fixture.lines()
            .filter { it.isNotBlank() && !it.startsWith("#") && it != "Lulo Bank" }
            .toList()

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