package com.recipee.sorter.classify

import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeClassifierTest {

    @Test
    fun pancakes_are_breakfast() {
        val result = RecipeClassifier.classify(
            "Blueberry Pancakes",
            "Ingredients: flour, milk, eggs, syrup. Whisk and cook on a griddle.",
        )
        assertEquals(Category.BREAKFAST, result)
    }

    @Test
    fun smoothie_is_smoothies_and_desserts() {
        val result = RecipeClassifier.classify(
            "Strawberry Banana Smoothie",
            "Blend banana, strawberries, yogurt and ice until smooth.",
        )
        assertEquals(Category.SMOOTHIES_DESSERTS, result)
    }

    @Test
    fun brownies_are_dessert() {
        assertEquals(
            Category.SMOOTHIES_DESSERTS,
            RecipeClassifier.classify("Chocolate Fudge Brownies", ""),
        )
    }

    @Test
    fun chicken_pasta_is_dinner() {
        val result = RecipeClassifier.classify(
            "Chicken Alfredo Pasta",
            "Boil the pasta. Add cooked chicken and alfredo sauce.",
        )
        assertEquals(Category.DINNER, result)
    }

    @Test
    fun tuna_salad_sandwich_is_lunch() {
        val result = RecipeClassifier.classify(
            "Tuna Salad Sandwich",
            "Mix tuna with mayo and spread on bread.",
        )
        assertEquals(Category.LUNCH, result)
    }

    @Test
    fun unknown_recipe_falls_back_to_default() {
        val result = RecipeClassifier.classify("Grandma's Mystery Recipe", "Mix everything well and serve.")
        assertEquals(RecipeClassifier.DEFAULT, result)
    }

    @Test
    fun empty_input_falls_back_to_default() {
        assertEquals(RecipeClassifier.DEFAULT, RecipeClassifier.classify("", ""))
    }

    @Test
    fun matching_ignores_case() {
        assertEquals(
            Category.SMOOTHIES_DESSERTS,
            RecipeClassifier.classify("SMOOTHIE BOWL", "BLEND EVERYTHING"),
        )
    }

    @Test
    fun title_outweighs_body() {
        val result = RecipeClassifier.classify("Pancakes", "Serve with a scoop of ice cream.")
        assertEquals(Category.BREAKFAST, result)
    }

    @Test
    fun pancake_does_not_count_as_cake() {
        val scores = RecipeClassifier.scores("Pancakes", "")
        assertEquals(0, scores.getValue(Category.SMOOTHIES_DESSERTS))
        assertEquals(3, scores.getValue(Category.BREAKFAST))
    }

    @Test
    fun ties_go_to_smoothies_and_desserts() {
        // "oatmeal" (breakfast) and "cookies" (dessert) both score 3.
        assertEquals(Category.SMOOTHIES_DESSERTS, RecipeClassifier.classify("Oatmeal Cookies", ""))
    }

    @Test
    fun repeated_body_word_is_capped() {
        // Without the cap, ten "cake" hits would beat the dinner words.
        val body = "cake ".repeat(10) + "pasta pasta chicken chicken"
        assertEquals(Category.DINNER, RecipeClassifier.classify("Mixed", body))
    }

    @Test
    fun category_from_unknown_name_uses_default() {
        assertEquals(RecipeClassifier.DEFAULT, Category.fromName("NOT_A_CATEGORY"))
        assertEquals(Category.LUNCH, Category.fromName("LUNCH"))
    }
}
