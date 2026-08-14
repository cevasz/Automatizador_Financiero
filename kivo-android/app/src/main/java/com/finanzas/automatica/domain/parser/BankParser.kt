package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.ParseResult

// Interfaz que debe implementar cada parser bancario
interface BankParser {
    val bankEntity: BankEntity
    val supportedPackageNames: List<String>

    fun canParse(packageName: String, notificationText: String): Boolean
    fun parse(notificationText: String): ParseResult
}