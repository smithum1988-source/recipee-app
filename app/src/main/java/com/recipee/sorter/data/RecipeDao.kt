package com.recipee.sorter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<Recipe>>

    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Query("UPDATE recipes SET category = :category, manuallySorted = 1 WHERE id = :id")
    suspend fun setCategory(id: Long, category: String)

    @Delete
    suspend fun delete(recipe: Recipe)
}
