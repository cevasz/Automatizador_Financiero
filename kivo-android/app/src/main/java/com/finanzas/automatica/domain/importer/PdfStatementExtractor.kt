package com.finanzas.automatica.domain.importer

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

/**
 * Extrae el texto plano de un extracto bancario en PDF para que luego
 * [StatementImporter] pueda parsearlo y clasificarlo automáticamente.
 */
object PdfStatementExtractor {

    fun extractText(pdfBytes: ByteArray): String {
        return PDDocument.load(pdfBytes).use { document ->
            PDFTextStripper().getText(document)
        }
    }
}
