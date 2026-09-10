package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.ParseResult
import java.time.Instant

// Interfaz que debe implementar cada parser bancario
interface BankParser {
    val bankEntity: BankEntity
    val supportedPackageNames: List<String>

    /**
     * Reconoce al banco por el texto cuando la notificación no viene de su app oficial
     * (SMS o correo). [ParserRegistry] también lo usa para desempatar: una notificación
     * de Bancolombia que menciona "a tu Nequi" la debe procesar Bancolombia.
     */
    val bankNamePattern: Regex

    fun canParse(packageName: String, notificationText: String): Boolean

    /** Como [canParse], pero explicando en que paso se descarto la notificacion. */
    fun evaluate(packageName: String, notificationText: String): CaptureDecision

    /**
     * @param occurredAt cuando ocurrio realmente el movimiento, si se conoce por fuera del
     *   texto (el `postTime` de la notificacion). Solo se usa si el texto no trae fecha:
     *   sin esto, una notificacion leida tarde -- al reconectarse el listener, por
     *   ejemplo -- quedaba fechada en el momento de leerla y se guardaba otra vez porque
     *   la deteccion de duplicados compara fechas.
     */
    fun parse(notificationText: String, occurredAt: Instant? = null): ParseResult
}
