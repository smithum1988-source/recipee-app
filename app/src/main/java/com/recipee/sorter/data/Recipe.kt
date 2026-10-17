package com.recipee.sorter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    /** [com.recipee.sorter.classify.Category] name. */
    val category: String,
    /** Absolute path of the app's private copy of the PDF. */
    val filePath: String,
    val extractedText: String,
    val addedAt: Long,
    /** True once the user has moved the recipe themselves. */
    val manuallySorted: Boolean = false,
)
