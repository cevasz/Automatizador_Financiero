package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.PaymentMethod
import com.finanzas.automatica.domain.model.RawMovement
import java.time.Instant
import java.util.Locale
import java.util.regex.Pattern

class NuParser : BaseBankParser(
    bankEntity = BankEntity.NU,
    // com.nu.production es el paquete real en Google Play, compartido por Nu Colombia,
    // Mexico y Brasil (verificado 2026-08-15, ver docs/PENDIENTES.md) -- "co.nubank" no
    // existe. Debe coincidir con notification_listener_config.xml.
    supportedPackageNames = listOf("com.nu.production", "co.nubank", "br.com.nubank")
) {

    override fun parse(notificationText: String): ParseResult {
        val lowerText = notificationText.lowercase(Locale.getDefault())
        val type = when {
            // "devolución"/"devolucion" debe revisarse ANTES que "compra": una devolución
            // suele redactarse como "Devolución de $X en tu compra" y contiene la palabra
            // "compra", pero es dinero que vuelve al usuario (ingreso), no un egreso.
            lowerText.contains("recibiste") || lowerText.contains("pix recibido") ||
                lowerText.contains("transferencia recibida") || lowerText.contains("devolución") ||
                lowerText.contains("devolucion") -> MovementType.INCOME
            lowerText.contains("pagaste") || lowerText.contains("pix enviado") || lowerText.contains("compra") || lowerText.contains("transferencia enviada") -> MovementType.EXPENSE
            else -> determineType(lowerText)
        }

        val amount = parseAmount(notificationText)
            ?: return ParseResult.Failure("No se pudo extraer monto", notificationText)

        val counterparty = extractCounterparty(lowerText)
        val paymentMethod = if (lowerText.contains("pix")) PaymentMethod.PSE else PaymentMethod.NU
        val date = parseDate(notificationText) ?: Instant.now()

        return buildRawMovement(
            type = type,
            amount = amount,
            paymentMethod = paymentMethod,
            counterpartyRaw = counterparty,
            date = date,
            rawText = notificationText,
            confidence = 0.8
        )
    }

    private fun extractCounterparty(text: String): String {
        val patterns = listOf(
            Pattern.compile("de\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("para\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("en\\s+([^\\.\\n]+)", Pattern.CASE_INSENSITIVE),
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1).trim()
        }
        return "Nu Colombia"
    }
}