package com.finanzas.automatica.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.parser.ParserRegistry
import org.junit.jupiter.api.Test
import java.time.ZoneId

/**
 * Robustez del motor de lectura de notificaciones.
 *
 * IMPORTANTE: a diferencia de `src/test/resources/fixtures/`, que solo contiene texto
 * REAL de notificaciones (ver CLAUDE.md), este archivo prueba el MOTOR de reglas frente a
 * variantes de redacción: los bancos reescriben sus mensajes sin avisar y el motor tiene
 * que seguir clasificando por familia de verbo, no por la frase exacta. Cuando llegue el
 * texto real de una variante nueva, va a fixtures y su caso concreto al test de fixtures.
 *
 * Cada bloque corresponde a una falla observada: ingresos leídos como egresos, egresos
 * que no se registraban y movimientos fantasma a partir de avisos que no mueven plata.
 */
class NotificationVariantsTest {

    private val registry = ParserRegistry.createDefault()

    private val nequi = "com.nequi.MobileApp"
    private val bancolombia = "co.com.bancolombia.personas.superapp"
    private val daviplata = "com.davivienda.daviplataapp"
    private val nu = "com.nu.production"
    private val lulo = "co.com.lulobank.production"

    private fun success(packageName: String, text: String): com.finanzas.automatica.domain.model.RawMovement {
        val result = registry.parse(packageName, text)
        assert(result is ParseResult.Success) { "Debio parsearse: \"$text\" -> $result" }
        return (result as ParseResult.Success).movement
    }

    private fun assertIgnored(packageName: String, text: String) {
        val result = registry.parse(packageName, text)
        assert(result is ParseResult.Failure) { "No debio registrarse un movimiento: \"$text\" -> $result" }
    }

    // --- Ingresos con redacciones distintas a "Recibiste" -------------------------------
    // Todas terminaban como EGRESO por el default conservador cuando el verbo no estaba
    // en la lista corta de cada parser.

    @Test
    fun `variantes de ingreso se clasifican como ingreso`() {
        val variantes = listOf(
            nequi to "Te llegaron $ 200.000 de MARIA GOMEZ",
            nequi to "Te enviaron plata: $ 80.000",
            nequi to "Te consignaron $ 1.000.000 en tu Nequi",
            nequi to "Te hicieron una transferencia por $ 50.000",
            bancolombia to "Bancolombia le informa que se realizo un abono por $ 350.000 en su cuenta de ahorros",
            bancolombia to "Nomina por $ 2.500.000 acreditada en tu cuenta Bancolombia",
            daviplata to "Te enviaron $ 45.000 a tu DaviPlata",
            nu to "Rendimientos del dia: $ 1.250 COP en tu cuenta Nu",
            lulo to "Te depositaron $ 300.000 en tu cuenta Lulo"
        )
        variantes.forEach { (pkg, text) ->
            val movement = success(pkg, text)
            assert(movement.type == MovementType.INCOME) {
                "\"$text\" debio ser INGRESO, quedo como ${movement.type}"
            }
        }
    }

    @Test
    fun `variantes de egreso se clasifican como egreso`() {
        val variantes = listOf(
            nequi to "Transferiste $ 80.000 a JUAN PEREZ",
            nequi to "Sacaste $ 100.000 en un cajero",
            nequi to "Pagaste con QR $ 32.500 en PANADERIA CENTRAL",
            bancolombia to "Bancolombia: se realizo una compra por $ 150.000 en FALABELLA con tu tarjeta *1234",
            bancolombia to "Te cobramos $ 15.900 por cuota de manejo",
            daviplata to "Retiro sin tarjeta por $ 60.000 en corresponsal",
            nu to "Compra aprobada en RAPPI por $ 43.200 COP",
            lulo to "Pago QR de $ 25.000 en CAFE CENTRAL"
        )
        variantes.forEach { (pkg, text) ->
            val movement = success(pkg, text)
            assert(movement.type == MovementType.EXPENSE) {
                "\"$text\" debio ser EGRESO, quedo como ${movement.type}"
            }
        }
    }

    // --- Frases que antes borraban movimientos reales -----------------------------------

    @Test
    fun `una compra real con la frase si no reconoces sigue registrandose`() {
        // Regresión directa: la palabra prohibida "conoce" hacía match dentro de
        // "reconoces" y descartaba la compra entera. Es la frase de cierre más común en
        // las notificaciones de compra con tarjeta.
        val movement = success(
            bancolombia,
            "Compra por $ 85.000 en EXITO CALLE 80 con tu tarjeta *1234. Si no reconoces esta compra, comunicate al 018000912345."
        )
        assert(movement.type == MovementType.EXPENSE)
        assert(movement.amount == 8_500_000L) { "monto ${movement.amount}" }
    }

    @Test
    fun `una compra a cuotas sigue registrandose pese al texto promocional`() {
        val movement = success(nu, "Pagaste $ 120.000 COP en FALABELLA a 3 cuotas sin interes")
        assert(movement.type == MovementType.EXPENSE)
        assert(movement.amount == 12_000_000L)
    }

    @Test
    fun `una compra que ademas otorga puntos sigue registrandose`() {
        val movement = success(bancolombia, "Compra por $ 45.000 en CARULLA. Ganaste 45 puntos Colombia.")
        assert(movement.type == MovementType.EXPENSE)
        assert(movement.amount == 4_500_000L)
    }

    // --- Avisos que NO mueven plata ------------------------------------------------------

    @Test
    fun `un pago rechazado no genera egreso`() {
        assertIgnored(bancolombia, "Tu pago por $ 250.000 en NETFLIX fue rechazado")
    }

    @Test
    fun `una solicitud de plata de un tercero no genera movimiento`() {
        assertIgnored(nequi, "JUAN PEREZ te esta pidiendo $ 20.000 desde tu cuenta Nequi")
    }

    @Test
    fun `un recordatorio de pago no genera egreso`() {
        assertIgnored(bancolombia, "Recuerda que tu factura de $ 180.000 vence manana. Bancolombia")
    }

    @Test
    fun `una promocion con monto no genera movimiento`() {
        assertIgnored(nequi, "Llevate $ 50.000 de descuento en tu proxima compra con Nequi")
    }

    // --- Selección del monto correcto ----------------------------------------------------

    @Test
    fun `el saldo informado no se confunde con el monto de la transaccion`() {
        val despues = success(nequi, "Pagaste $ 15.500 en LA 14. Tu saldo disponible es $ 1.230.000")
        assert(despues.amount == 1_550_000L) { "monto ${despues.amount}" }

        val antes = success(nequi, "Tu saldo disponible es $ 1.230.000. Pagaste $ 15.500 en LA 14")
        assert(antes.amount == 1_550_000L) { "monto ${antes.amount}" }
    }

    // --- Normalización: emojis, tildes y mayúsculas --------------------------------------

    @Test
    fun `emojis y tildes ausentes no impiden la lectura`() {
        val conEmoji = success(nequi, "💸 ¡Te llego plata! Recibiste $ 60.000 de ANA MARIA")
        assert(conEmoji.type == MovementType.INCOME)
        assert(conEmoji.amount == 6_000_000L)

        val sinTilde = success(nu, "Devolucion de $ 50.000 COP en tu compra")
        assert(sinTilde.type == MovementType.INCOME) { "una devolucion es plata que vuelve" }
    }

    // --- Recargas: la misma palabra en los dos sentidos ----------------------------------

    @Test
    fun `recargar el celular es egreso y recibir una recarga es ingreso`() {
        assert(success(nequi, "Recargaste tu celular por $ 10.000").type == MovementType.EXPENSE)
        assert(success(nequi, "Te recargaron $ 100.000 en tu Nequi").type == MovementType.INCOME)
    }

    // --- Elección de banco cuando la notificación menciona a otro ------------------------

    @Test
    fun `una notificacion de Bancolombia que menciona Nequi la procesa Bancolombia`() {
        val porPaquete = success(bancolombia, "Transferiste $ 50.000 a tu Nequi desde tu cuenta de ahorros")
        assert(porPaquete.bankEntity == BankEntity.BANCOLOMBIA) {
            "el paquete emisor manda sobre la mencion en el texto: ${porPaquete.bankEntity}"
        }

        // Sin paquete (OCR de una captura) gana el banco nombrado primero.
        val porTexto = success("", "Bancolombia le informa: transferiste $ 50.000 a tu Nequi")
        assert(porTexto.bankEntity == BankEntity.BANCOLOMBIA) { "${porTexto.bankEntity}" }
    }

    @Test
    fun `la palabra nu no hace match dentro de otras palabras`() {
        // "menu" contenia "nu " y arrastraba notificaciones ajenas al parser de Nu.
        val movement = success(daviplata, "Pagaste $ 28.000 en el menu del dia con DaviPlata")
        assert(movement.bankEntity == BankEntity.DAVIPLATA) { "${movement.bankEntity}" }
    }

    // --- SMS y correo del banco -----------------------------------------------------

    @Test
    fun `un SMS del banco con verbo claro si se registra`() {
        val movement = success(
            "com.google.android.apps.messaging",
            "Bancolombia: Compra por $ 85.000 en EXITO CALLE 80 con tu tarjeta *1234."
        )
        assert(movement.type == MovementType.EXPENSE)
        assert(movement.bankEntity == BankEntity.BANCOLOMBIA)
    }

    @Test
    fun `un mensaje personal que menciona al banco no genera movimiento`() {
        // En la app de mensajes conviven los avisos del banco y las conversaciones: por
        // eso ahi se exige un verbo transaccional inequivoco, no solo el nombre y un monto.
        assertIgnored(
            "com.google.android.apps.messaging",
            "Hola! ya te mande los $ 50.000 por Nequi, mira a ver si te llegaron"
        )
    }

    // --- Datos derivados -----------------------------------------------------------------

    @Test
    fun `la hora del texto se usa en la fecha del movimiento`() {
        val movement = success(
            bancolombia,
            "Bancolombia: Recibiste una transferencia por $100,000 de LUIS RINCON en tu cuenta **3463, el 11/08/2026 a las 18:43."
        )
        val local = movement.date.atZone(ZoneId.systemDefault())
        assert(local.hour == 18 && local.minute == 43) { "quedo en ${local.hour}:${local.minute}" }
        assert(movement.counterpartyRaw == "LUIS RINCON") { movement.counterpartyRaw }
    }

    @Test
    fun `el medio de pago se deduce del texto`() {
        assert(success(nequi, "Pagaste con QR $ 12.000 en TIENDA D1").paymentMethod == PaymentMethod.QR)
        assert(success(bancolombia, "Pago PSE por $ 90.000 a CLARO").paymentMethod == PaymentMethod.PSE)
        assert(success(lulo, "Retiro en cajero por $ 100.000").paymentMethod == PaymentMethod.CASH)
    }

    @Test
    fun `un movimiento sin verbo reconocido queda con confianza baja para revision`() {
        val movement = success(nequi, "Movimiento en tu cuenta Nequi por $ 30.000")
        assert(movement.confidence < 0.6) {
            "sin verbo el tipo es una suposicion y debe quedar para confirmar: ${movement.confidence}"
        }
    }
}
