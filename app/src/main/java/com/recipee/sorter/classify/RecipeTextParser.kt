package com.recipee.sorter.classify

/** Small helpers for turning extracted PDF text into display data. */
object RecipeTextParser {
    private const val MAX_TITLE_LENGTH = 80

    /**
     * Uses the first meaningful line of the PDF as the recipe title (skipping blank lines
     * and stray page numbers). Falls back to [fallback], e.g. the file name.
     */
    fun title(text: String, fallback: String): String {
        val first = text.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.length >= 3 && it.any(Char::isLetter) }
        return (first ?: fallback).take(MAX_TITLE_LENGTH).trim()
    }

    /** A short preview of the body for list cards. */
    fun snippet(text: String, title: String, maxLength: Int = 120): String {
        val body = text.replace(Regex("\\s+"), " ").trim().removePrefix(title).trim()
        return if (body.length <= maxLength) body else body.take(maxLength).trimEnd() + "…"
    }
}
