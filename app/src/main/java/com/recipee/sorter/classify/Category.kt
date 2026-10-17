package com.recipee.sorter.classify

/** The four shelves recipes are sorted into. */
enum class Category(val label: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SMOOTHIES_DESSERTS("Smoothies & Desserts");

    companion object {
        /** Safe lookup for values read back from the database. */
        fun fromName(name: String?): Category =
            entries.firstOrNull { it.name == name } ?: RecipeClassifier.DEFAULT
    }
}
