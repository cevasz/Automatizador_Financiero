package com.finanzas.automatica.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.parser.CaptureRejection
import com.finanzas.automatica.domain.parser.ParserRegistry
import org.junit.jupiter.api.Test

/**
 * Diagnóstico de captura: además de decidir, el motor tiene que poder EXPLICAR.
 *
 * Dos cosas se prueban aquí. La primera es que el motivo mostrado sea el útil: el del
 * banco que de verdad miró la notificación, no el "no es de un banco reconocido" de los
 * otros cuatro. La segunda es la línea de privacidad — qué textos pueden guardarse en el
 * registro local y cuáles no deben tocar el disco nunca.
 */
class CaptureDiagnosisTest {

    private val registry = ParserRegistry.createDefault()
    private val nequi = "com.nequi.MobileApp"
    private val mensajes = "com.google.android.apps.messaging"

    @Test
    fun `una notificacion capturada dice que banco la proceso`() {
        val diagnosis = registry.diagnose(nequi, "Recibiste $ 50.000 de 3204567890")
        assert(diagnosis.accepted) { "$diagnosis" }
        assert(diagnosis.parser?.bankEntity == BankEntity.NEQUI) { "$diagnosis" }
        assert(diagnosis.loggable)
    }

    @Test
    fun `el motivo mostrado es el del banco que reconocio la notificacion`() {
        // Nequi la mira y la descarta por falta de monto; los otros cuatro parsers la
        // rechazan antes, por no ser suyas. El motivo útil es el de Nequi.
        val diagnosis = registry.diagnose(nequi, "Recibiste 500 puntos Nequi por tus compras del mes")
        assert(diagnosis.rejection == CaptureRejection.NO_AMOUNT) { "$diagnosis" }
        assert(diagnosis.bankEntity == BankEntity.NEQUI) { "$diagnosis" }
        assert(diagnosis.loggable) { "un aviso del banco sí se puede guardar para diagnóstico" }
    }

    @Test
    fun `un aviso que no mueve plata se explica como tal`() {
        val diagnosis = registry.diagnose(nequi, "Tu pago por $ 250.000 en NETFLIX fue rechazado")
        assert(diagnosis.rejection == CaptureRejection.NOT_A_MOVEMENT) { "$diagnosis" }
        assert(diagnosis.loggable)
    }

    @Test
    fun `una promocion se explica como promocion`() {
        val diagnosis = registry.diagnose(nequi, "Llevate $ 50.000 de descuento en tu proxima compra con Nequi")
        assert(diagnosis.rejection == CaptureRejection.REMINDER_OR_PROMO) { "$diagnosis" }
    }

    // --- Privacidad: qué NO puede guardarse -------------------------------------------

    @Test
    fun `una notificacion de otra app no se guarda en el diagnostico`() {
        val diagnosis = registry.diagnose("com.otra.app", "Tu pedido va en camino, llega en 20 minutos")
        assert(diagnosis.rejection == CaptureRejection.OTHER_APP) { "$diagnosis" }
        assert(!diagnosis.loggable) { "el registro solo puede ver avisos bancarios" }
        assert(diagnosis.bankEntity == null) { "no hay banco que nombrar: ${diagnosis.bankEntity}" }
    }

    @Test
    fun `un mensaje de texto ajeno al banco no se guarda`() {
        val diagnosis = registry.diagnose(mensajes, "Nos vemos a las 7 en la casa de mi mama")
        assert(diagnosis.rejection == CaptureRejection.MESSAGING_WITHOUT_BANK) { "$diagnosis" }
        assert(!diagnosis.loggable)
    }

    @Test
    fun `un mensaje personal que solo menciona al banco no se guarda`() {
        // Es texto de una conversación: se descarta antes de que llegue al registro, y ni
        // siquiera se escribe el motivo con su contenido.
        val diagnosis = registry.diagnose(mensajes, "Hola! ya te mande los $ 50.000 por Nequi, avisame")
        assert(diagnosis.rejection == CaptureRejection.MESSAGING_SENDER_NOT_BANK) { "$diagnosis" }
        assert(!diagnosis.loggable) { "un mensaje personal nunca debe quedar guardado" }
    }

    @Test
    fun `un SMS del banco sin verbo claro si queda para diagnostico`() {
        val diagnosis = registry.diagnose(
            mensajes,
            "Bancolombia: tu cuenta de ahorros **3463 registra un movimiento por $ 90.000."
        )
        assert(diagnosis.rejection == CaptureRejection.MESSAGING_WEAK_VERB) { "$diagnosis" }
        assert(diagnosis.loggable) { "viene del banco: sirve para corregir las reglas" }
        assert(diagnosis.bankEntity == BankEntity.BANCOLOMBIA) { "${diagnosis.bankEntity}" }
    }
}
