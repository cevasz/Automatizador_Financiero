package com.finanzas.automatica.domain.parser

/**
 * Resultado de mirar una notificación y decidir si describe un movimiento.
 *
 * Existe para poder EXPLICAR el descarte. Antes `canParse()` devolvía un booleano: cuando
 * una notificación no se registraba no quedaba rastro de en qué paso se cayó, y la única
 * pista era un `Log.w` en logcat que el usuario del teléfono nunca ve. Con el motivo a la
 * mano, la pantalla de diagnóstico puede mostrar qué llegó y por qué se ignoró, que es lo
 * que permite corregir las reglas con texto real en vez de adivinando.
 */
sealed interface CaptureDecision {

    /** La notificación describe un movimiento y se va a parsear. */
    data object Accepted : CaptureDecision

    data class Rejected(val reason: CaptureRejection) : CaptureDecision
}

/**
 * Motivo por el que una notificación no se convirtió en movimiento.
 *
 * @param label texto que se le muestra al usuario en la pantalla de diagnóstico.
 * @param loggable si el texto de esa notificación puede guardarse en el registro local.
 *   Es la línea de privacidad: de las apps de mensajería solo se guarda lo que se
 *   reconoció como aviso del banco (el banco es el remitente). Un mensaje personal que
 *   apenas menciona a Nequi se descarta y **no** se escribe en ninguna parte.
 */
enum class CaptureRejection(val label: String, val loggable: Boolean) {
    OTHER_APP("La notificación no es de un banco reconocido", loggable = false),
    MESSAGING_WITHOUT_BANK("Mensaje que no nombra a ningún banco", loggable = false),
    MESSAGING_SENDER_NOT_BANK(
        "El banco se nombra en mitad del mensaje: no lo envió el banco",
        loggable = false
    ),
    MESSAGING_WEAK_VERB(
        "Mensaje del banco, pero sin un verbo de transacción claro",
        loggable = true
    ),
    NO_AMOUNT("No trae un monto con símbolo de moneda ($ o COP)", loggable = true),
    NOT_A_MOVEMENT(
        "Aviso que no mueve plata: intento fallido, solicitud de dinero o código de seguridad",
        loggable = true
    ),
    REMINDER_OR_PROMO("Recordatorio o mensaje promocional", loggable = true),
    NO_TRANSACTION_WORDS(
        "No se reconoció ningún verbo de transacción (ni compra, ni pago, ni abono...)",
        loggable = true
    ),
    NO_AMOUNT_ON_PARSE("Se reconoció el movimiento pero no se pudo leer el monto", loggable = true)
}
