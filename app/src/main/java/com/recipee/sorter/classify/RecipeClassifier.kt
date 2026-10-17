package com.recipee.sorter.classify

/**
 * Offline keyword classifier. Each category has a keyword list; a keyword found in the
 * title counts [TITLE_WEIGHT] times per hit, a keyword found in the body counts once per
 * hit (capped at [MAX_BODY_HITS_PER_KEYWORD] so one repeated word can't dominate).
 *
 * Ties go to the first category in [PRIORITY]; if nothing matches at all the result is
 * [DEFAULT]. The user can always override the result in the app.
 */
object RecipeClassifier {
    val DEFAULT = Category.DINNER

    private const val TITLE_WEIGHT = 3
    private const val MAX_BODY_HITS_PER_KEYWORD = 3

    private val PRIORITY = listOf(
        Category.SMOOTHIES_DESSERTS,
        Category.BREAKFAST,
        Category.LUNCH,
        Category.DINNER,
    )

    private val KEYWORDS: Map<Category, List<String>> = mapOf(
        Category.BREAKFAST to listOf(
            "breakfast", "brunch", "pancake", "waffle", "oatmeal", "oats", "porridge",
            "omelette", "omelet", "frittata", "scrambled eggs", "french toast", "toast",
            "bagel", "muffin", "granola", "cereal", "crepe", "hash brown",
        ),
        Category.LUNCH to listOf(
            "lunch", "sandwich", "salad", "soup", "wrap", "burger", "quesadilla",
            "panini", "burrito",
        ),
        Category.DINNER to listOf(
            "dinner", "roast", "steak", "pasta", "spaghetti", "lasagna", "lasagne", "curry",
            "casserole", "stew", "stir fry", "stir-fry", "chicken", "salmon", "meatball",
            "risotto", "pork", "lamb", "ribs", "enchilada", "chili", "pizza", "mac and cheese",
        ),
        Category.SMOOTHIES_DESSERTS to listOf(
            "smoothie", "blend", "milkshake", "dessert", "cake", "cupcake", "cheesecake",
            "brownie", "cookie", "pie", "pudding", "ice cream", "sorbet", "mousse", "tart",
            "fudge", "custard", "popsicle", "parfait", "cobbler", "truffle", "frozen yogurt",
        ),
    )

    // Whole-word match, allowing a plural ending ("pancake" matches "pancakes").
    private val PATTERNS: Map<Category, List<Regex>> = KEYWORDS.mapValues { (_, words) ->
        words.map { Regex("\\b" + Regex.escape(it) + "(?:s|es)?\\b") }
    }

    fun scores(title: String, text: String): Map<Category, Int> {
        val t = title.lowercase()
        val b = text.lowercase()
        return PATTERNS.mapValues { (_, patterns) ->
            patterns.sumOf { p ->
                val titleHits = p.findAll(t).count()
                val bodyHits = minOf(p.findAll(b).count(), MAX_BODY_HITS_PER_KEYWORD)
                titleHits * TITLE_WEIGHT + bodyHits
            }
        }
    }

    fun classify(title: String, text: String): Category {
        val scores = scores(title, text)
        val best = PRIORITY.maxByOrNull { scores.getValue(it) } ?: return DEFAULT
        return if (scores.getValue(best) == 0) DEFAULT else best
    }
}
