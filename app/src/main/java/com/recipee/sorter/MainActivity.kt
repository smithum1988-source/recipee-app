package com.recipee.sorter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.recipee.sorter.ui.PdfViewerScreen
import com.recipee.sorter.ui.RecipeListScreen
import com.recipee.sorter.ui.RecipeTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeTheme {
                var openRecipeId by rememberSaveable { mutableStateOf<Long?>(null) }
                val recipes by viewModel.recipes.collectAsState()
                val open = recipes.firstOrNull { it.id == openRecipeId }

                if (open != null) {
                    BackHandler { openRecipeId = null }
                    PdfViewerScreen(
                        title = open.title,
                        file = File(open.filePath),
                        onBack = { openRecipeId = null },
                    )
                } else {
                    RecipeListScreen(viewModel, onOpen = { openRecipeId = it.id })
                }
            }
        }
    }
}
