package com.recipee.sorter.classify

import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeTextParserTest {

    @Test
    fun title_is_first_meaningful_line() {
        assertEquals("Banana Bread", RecipeTextParser.title("\n\n  Banana Bread  \nIngredients", "x"))
    }

    @Test
    fun title_skips_stray_page_numbers() {
        assertEquals("Lemon Tart", RecipeTextParser.title("1\n\nLemon Tart\n", "x"))
    }

    @Test
    fun title_uses_fallback_when_text_is_empty() {
        assertEquals("My File", RecipeTextParser.title("", "My File"))
    }

    @Test
    fun title_is_truncated() {
        assertEquals(80, RecipeTextParser.title("a".repeat(200), "x").length)
    }

    @Test
    fun snippet_drops_the_title_and_truncates() {
        val text = "Banana Bread\n" + "word ".repeat(100)
        val snippet = RecipeTextParser.snippet(text, "Banana Bread", maxLength = 20)
        assertEquals(true, snippet.startsWith("word"))
        assertEquals(true, snippet.endsWith("…"))
    }
}
