package com.finanzas.automatica.parser

import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.parser.*
import org.junit.jupiter.api.Test
import java.io.File

class BankParserTest {

    private val registry = ParserRegistry.createDefault()
    private val fixtureDir = File("src/test/resources/fixtures")

    private fun fixtureLines(fileName: String, headerLine: String): List<String> {
        val fixture = File(fixtureDir, fileName).readText()
        return fixture.lines()
            .filter { it.isNotBlank() && !it.startsWith("#") && it != headerLine }
            .toList()
    }

    @Test
    fun `parse Nequi notifications from fixtures with correct type and amount`() {
        // amount en centavos COP (ver CLAUDE.md - los montos siempre se guardan en centavos)
        val expected = mapOf(
            "Recibiste $ 50.000 de 3204567890" to (MovementType.INCOME to 5_000_000L),
            "Te recargaron $ 100.000" to (MovementType.INCOME to 10_000_000L),
            "Enviaste $ 25.000 a 3109876543" to (MovementType.EXPENSE to 2_500_000L),
            "Pagaste $ 15.500 en Supermercado La 14" to (MovementType.EXPENSE to 1_550_000L),
            "Retiraste $ 50.000 en cajero" to (MovementType.EXPENSE to 5_000_000L)
        )

        fixtureLines("nequi_notifications.txt", "Nequi").forEach { line ->
            val result = registry.parse("com.nequi.app", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            val (expectedType, expectedAmount) = expected.getValue(line)
            assert(movement.type == expectedType) { "$line -> expected $expectedType, got ${movement.type}" }
            assert(movement.amount == expectedAmount) { "$line -> expected $expectedAmount, got ${movement.amount}" }
        }
    }

    @Test
    fun `parse Bancolombia notifications from fixtures with correct type and amount`() {
        val expected = mapOf(
            "Abono a su cuenta ****1234 por $ 500.000" to (MovementType.INCOME to 50_000_000L),
            "Transferencia recibida por $ 1.200.000 en su cuenta ****5678" to (MovementType.INCOME to 120_000_000L),
            "Compra realizada por $ 85.000 en Exito" to (MovementType.EXPENSE to 8_500_000L),
            "Pago de factura por $ 120.000" to (MovementType.EXPENSE to 12_000_000L),
            "Transferencia enviada por $ 200.000 desde su cuenta ****1234" to (MovementType.EXPENSE to 20_000_000L),
            "Bancolombia: Recibiste una transferencia por $100,000 de LUIS RINCON en tu cuenta **3463, el 11/08/2026 a las 18:43. Si tienes dudas, hablemos: 018000931987. Siempre a tu lado." to (MovementType.INCOME to 10_000_000L)
        )

        fixtureLines("bancolombia_notifications.txt", "Bancolombia").forEach { line ->
            val result = registry.parse("com.bancolombia.certipersonas", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            val (expectedType, expectedAmount) = expected.getValue(line)
            assert(movement.type == expectedType) { "$line -> expected $expectedType, got ${movement.type}" }
            assert(movement.amount == expectedAmount) { "$line -> expected $expectedAmount, got ${movement.amount}" }
        }
    }

    @Test
    fun `parse Bancolombia SMS transfer from SMS app package`() {
        val smsText = "Bancolombia: Recibiste una transferencia por $100,000 de LUIS RINCON en tu cuenta **3463, el 11/08/2026 a las 18:43. Si tienes dudas, hablemos: 018000931987. Siempre a tu lado."
        val result = registry.parse("com.google.android.apps.messaging", smsText)
        assert(result is ParseResult.Success) { "Failed to parse SMS: $smsText -> $result" }
        val movement = (result as ParseResult.Success).movement
        assert(movement.type == MovementType.INCOME)
        assert(movement.amount == 10000000L) // $100.000 COP en centavos
        assert(movement.counterpartyRaw == "LUIS RINCON")
    }

    @Test
    fun `parse Bancolombia transfer arriving by Gmail`() {
        val gmailText = "Bancolombia: Recibiste una transferencia por $100,000 de LUIS RINCON en tu cuenta **3463, el 11/08/2026 a las 18:43."
        val result = registry.parse("com.google.android.gm", gmailText)
        assert(result is ParseResult.Success) { "Failed to parse Gmail: $gmailText -> $result" }
        val movement = (result as ParseResult.Success).movement
        assert(movement.type == MovementType.INCOME)
        assert(movement.amount == 10000000L)
        assert(movement.counterpartyRaw == "LUIS RINCON")
    }

    @Test
    fun `parse Daviplata notifications from fixtures with correct type and amount`() {
        val expected = mapOf(
            "Recibiste $ 75.000 de 3156677889" to (MovementType.INCOME to 7_500_000L),
            "Te recargaron $ 200.000" to (MovementType.INCOME to 20_000_000L),
            "Enviaste $ 30.000 a 3112233445" to (MovementType.EXPENSE to 3_000_000L),
            "Pagaste $ 45.000 en Drogueria La Rebaja" to (MovementType.EXPENSE to 4_500_000L)
        )

        fixtureLines("daviplata_notifications.txt", "Daviplata").forEach { line ->
            val result = registry.parse("com.daviplata.daviplata", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            val (expectedType, expectedAmount) = expected.getValue(line)
            assert(movement.type == expectedType) { "$line -> expected $expectedType, got ${movement.type}" }
            assert(movement.amount == expectedAmount) { "$line -> expected $expectedAmount, got ${movement.amount}" }
        }
    }

    @Test
    fun `parse Nu notifications from fixtures with correct type and amount`() {
        val expected = mapOf(
            "Recibiste una transferencia de $ 300.000 COP" to (MovementType.INCOME to 30_000_000L),
            // Es un reembolso (dinero que vuelve), no un egreso, aunque el texto mencione "compra".
            "Devolución de $ 50.000 COP en tu compra" to (MovementType.INCOME to 5_000_000L),
            "Pagaste $ 120.000 COP en Rappi" to (MovementType.EXPENSE to 12_000_000L),
            "Enviaste $ 100.000 COP a Juan Pérez" to (MovementType.EXPENSE to 10_000_000L),
            "Pago de suscripción $ 29.900 COP en Netflix" to (MovementType.EXPENSE to 2_990_000L)
        )

        fixtureLines("nu_notifications.txt", "Nu Colombia").forEach { line ->
            val result = registry.parse("co.nubank", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            val (expectedType, expectedAmount) = expected.getValue(line)
            assert(movement.type == expectedType) { "$line -> expected $expectedType, got ${movement.type}" }
            assert(movement.amount == expectedAmount) { "$line -> expected $expectedAmount, got ${movement.amount}" }
        }
    }

    @Test
    fun `parse Lulo notifications from fixtures with correct type and amount`() {
        val expected = mapOf(
            "Abono de $ 500.000 en tu cuenta Lulo" to (MovementType.INCOME to 50_000_000L),
            "Recibiste $ 150.000 de transferencia" to (MovementType.INCOME to 15_000_000L),
            "Compra de $ 80.000 en Mercado Libre" to (MovementType.EXPENSE to 8_000_000L),
            "Pago QR de $ 25.000 en Café Central" to (MovementType.EXPENSE to 2_500_000L),
            "Retiro de $ 100.000 en cajero" to (MovementType.EXPENSE to 10_000_000L)
        )

        fixtureLines("lulo_notifications.txt", "Lulo Bank").forEach { line ->
            val result = registry.parse("com.lulobank.app", line)
            assert(result is ParseResult.Success) { "Failed to parse: $line -> $result" }
            val movement = (result as ParseResult.Success).movement
            val (expectedType, expectedAmount) = expected.getValue(line)
            assert(movement.type == expectedType) { "$line -> expected $expectedType, got ${movement.type}" }
            assert(movement.amount == expectedAmount) { "$line -> expected $expectedAmount, got ${movement.amount}" }
        }
    }

    @Test
    fun `unknown package returns failure`() {
        val result = registry.parse("com.unknown.app", "Algun texto $ 100.000")
        assert(result is ParseResult.Failure)
    }

    // --- Regresión: los paquetes de Android declarados deben coincidir con las apps
    // reales en Google Play (verificados 2026-08-15, ver docs/PENDIENTES.md). Los
    // paquetes anteriores ("com.nequi.app", "com.bancolombia.certipersonas",
    // "com.daviplata.daviplata", "co.nubank", "com.lulobank.app") no correspondian a
    // ninguna app real -- como notification_listener_config.xml usa un filtro de
    // inclusion que aplica el sistema operativo ANTES de que el codigo de la app se
    // ejecute, un paquete incorrecto ahi significa que la notificacion nunca llega ni
    // siquiera a onNotificationPosted(), sin importar que tan bien funcione el parser.
    // Este test cubre el otro extremo (que ParserRegistry SI reconozca el paquete real)
    // para que un cambio futuro no vuelva a romper esto en silencio.

    @Test
    fun `recognizes the real Nequi package name from Google Play`() {
        val result = registry.parse("com.nequi.MobileApp", "Recibiste $ 50.000 de 3204567890")
        assert(result is ParseResult.Success) { "com.nequi.MobileApp es el paquete real de Nequi en Play Store: $result" }
    }

    @Test
    fun `recognizes the real Bancolombia package name from Google Play`() {
        val result = registry.parse("co.com.bancolombia.personas.superapp", "Abono a su cuenta ****1234 por $ 500.000")
        assert(result is ParseResult.Success) { "co.com.bancolombia.personas.superapp es el paquete real de Mi Bancolombia en Play Store: $result" }
    }

    @Test
    fun `recognizes the real Daviplata package name from Google Play`() {
        val result = registry.parse("com.davivienda.daviplataapp", "Recibiste $ 75.000 de 3156677889")
        assert(result is ParseResult.Success) { "com.davivienda.daviplataapp es el paquete real de Daviplata en Play Store: $result" }
    }

    @Test
    fun `recognizes the real Nu package name from Google Play`() {
        val result = registry.parse("com.nu.production", "Recibiste una transferencia de $ 300.000 COP")
        assert(result is ParseResult.Success) { "com.nu.production es el paquete real de Nu en Play Store: $result" }
    }

    @Test
    fun `recognizes the real Lulo Bank package name from Google Play`() {
        val result = registry.parse("co.com.lulobank.production", "Abono de $ 500.000 en tu cuenta Lulo")
        assert(result is ParseResult.Success) { "co.com.lulobank.production es el paquete real de Lulo Bank en Play Store: $result" }
    }

    // --- Regresión: monto con decimales no debe inflarse 10x/100x (bug del "cero de más") ---

    @Test
    fun `amount with one decimal digit is not inflated 10x`() {
        val result = registry.parse("com.nequi.app", "Recibiste $ 50.000,5 de 3204567890")
        assert(result is ParseResult.Success) { "Failed to parse: $result" }
        val movement = (result as ParseResult.Success).movement
        // 50.000,5 pesos = 5.000.050 centavos (antes del fix daba 50.000.500 = 10x de más)
        assert(movement.amount == 5_000_050L) { "expected 5000050, got ${movement.amount}" }
    }

    @Test
    fun `amount with two decimal digits is not inflated 100x`() {
        val result = registry.parse("co.nubank", "Pago de suscripción $ 29.900,99 COP en Netflix")
        assert(result is ParseResult.Success) { "Failed to parse: $result" }
        val movement = (result as ParseResult.Success).movement
        assert(movement.amount == 2_990_099L) { "expected 2990099, got ${movement.amount}" }
    }

    // --- Regresión: notificaciones no transaccionales de la app oficial no deben
    // generar movimientos falsos, aunque contengan algún número suelto. ---

    @Test
    fun `promotional notification with a bare number is not parsed as a movement`() {
        val result = registry.parse("com.nequi.app", "Recibiste 500 puntos Nequi por tus compras del mes")
        assert(result is ParseResult.Failure) { "Promotional/loyalty notification should not parse as a movement: $result" }
    }

    @Test
    fun `security code notification is not parsed as a movement`() {
        val result = registry.parse("com.bancolombia.personas", "Bancolombia: tu código de verificación es 482913. No lo compartas con nadie.")
        assert(result is ParseResult.Failure) { "Security/OTP notification should not parse as a movement: $result" }
    }

    @Test
    fun `installment promo notification without currency amount is not parsed as a movement`() {
        val result = registry.parse("com.daviplata.daviplata", "Daviplata: ahora puedes pagar en 3 cuotas sin interés. Actívalo desde la app.")
        assert(result is ParseResult.Failure) { "Marketing notification with a bare short number should not parse as a movement: $result" }
    }
}
