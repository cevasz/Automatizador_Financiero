package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.RawMovement

// Interfaz que debe implementar cada parser bancario
interface BankParser {
    val bankEntity: BankEntity
    val supportedPackageNames: List<String>

    fun canParse(packageName: String, notificationText: String): Boolean
    fun parse(notificationText: String): ParseResult
}

enum class BankEntity {
    NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, UNKNOWN
}