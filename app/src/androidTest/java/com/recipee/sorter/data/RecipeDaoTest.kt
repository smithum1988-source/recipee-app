package com.recipee.sorter.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: RecipeDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.recipeDao()
    }

    @After
    fun tearDown() = db.close()

    private fun recipe(title: String, category: String = "DINNER", addedAt: Long = 0) = Recipe(
        title = title,
        category = category,
        filePath = "/tmp/$title.pdf",
        extractedText = "",
        addedAt = addedAt,
    )

    @Test
    fun insertedRecipesAreReturnedNewestFirst() = runBlocking {
        dao.insert(recipe("Old", addedAt = 1))
        dao.insert(recipe("New", addedAt = 2))
        assertEquals(listOf("New", "Old"), dao.observeAll().first().map { it.title })
    }

    @Test
    fun movingARecipeMarksItManuallySorted() = runBlocking {
        val id = dao.insert(recipe("Pancakes", category = "DINNER"))
        dao.setCategory(id, "BREAKFAST")
        val saved = dao.observeAll().first().single()
        assertEquals("BREAKFAST", saved.category)
        assertTrue(saved.manuallySorted)
    }

    @Test
    fun deletingARecipeRemovesIt() = runBlocking {
        val id = dao.insert(recipe("Soup"))
        dao.delete(dao.observeAll().first().single { it.id == id })
        assertTrue(dao.observeAll().first().isEmpty())
    }
}
