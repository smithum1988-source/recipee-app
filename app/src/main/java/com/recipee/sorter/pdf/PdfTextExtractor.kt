package com.recipee.sorter.pdf

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File

/**
 * Pulls plain text out of a PDF with PdfBox-Android. Scanned (image-only) PDFs have no
 * text layer and come back as an empty string; OCR is a possible later addition.
 *
 * PDFBoxResourceLoader.init(context) must have been called once (see RecipeApp).
 */
object PdfTextExtractor {
    private const val MAX_CHARS = 200_000

    fun extract(file: File): String = try {
        PDDocument.load(file).use { doc ->
            PDFTextStripper().getText(doc).take(MAX_CHARS)
        }
    } catch (e: Exception) {
        ""
    }
}
