package com.finanzas.automatica.domain.parser

import com.finanzas.automatica.domain.model.ParseResult
import com.finanzas.automatica.domain.model.RawMovement

class ParserRegistry(private val parsers: List<BankParser>) {

    fun parse(packageName: String, notificationText: String): ParseResult {
        val parser = parsers.find { it.canParse(packageName, notificationText) }
            ?: return ParseResult.Failure("No parser found for package: $packageName", notificationText)

        return parser.parse(notificationText)
    }

    fun getSupportedPackages(): List<String> = parsers.flatMap { it.supportedPackageNames }.distinct()

    companion object {
        fun createDefault(): ParserRegistry {
            return ParserRegistry(listOf(
                NequiParser(),
                BancolombiaParser(),
                DaviplataParser(),
                NuParser(),
                LuloParser()
            ))
        }
    }
}