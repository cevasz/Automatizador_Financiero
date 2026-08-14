package com.finanzas.automatica.domain.importer

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.text.PDFTextStripper

/**
 * Extrae el texto plano de un extracto bancario en PDF para que luego
 * [StatementImporter] pueda parsearlo y clasificarlo automáticamente.
 *
 * Los extractos de los bancos colombianos casi siempre vienen protegidos con
 * contraseña (típicamente la cédula del titular o similar) -- [requiresPassword]
 * detecta esto para que la UI pida la contraseña antes de intentar importar, y
 * [extractText] la usa para descifrar el documento.
 */
object PdfStatementExtractor {

    /** true si el PDF necesita contraseña para abrirse (no se puede leer sin ella). */
    fun requiresPassword(pdfBytes: ByteArray): Boolean {
        return try {
            PDDocument.load(pdfBytes).use { }
            false
        } catch (e: InvalidPasswordException) {
            true
        }
    }

    /**
     * @throws InvalidPasswordException si el PDF esta cifrado y [password] es nula,
     * vacia o incorrecta -- el llamador debe distinguir este caso para pedirle al
     * usuario que reintente, en vez de reportarlo como una falla generica de import.
     */
    fun extractText(pdfBytes: ByteArray, password: String? = null): String {
        return PDDocument.load(pdfBytes, password ?: "").use { document ->
            PDFTextStripper().getText(document)
        }
    }
}
