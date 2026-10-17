package com.recipee.sorter

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.recipee.sorter.classify.Category
import com.recipee.sorter.classify.RecipeClassifier
import com.recipee.sorter.classify.RecipeTextParser
import com.recipee.sorter.data.Recipe
import com.recipee.sorter.pdf.PdfTextExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class RecipeViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = (app as RecipeApp).database.recipeDao()

    val recipes: StateFlow<List<Recipe>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _importing = MutableStateFlow(false)
    val importing: StateFlow<Boolean> = _importing.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun importPdfs(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _importing.value = true
            try {
                uris.forEach { importOne(it) }
            } finally {
                _importing.value = false
            }
        }
    }

    fun moveTo(recipe: Recipe, category: Category) {
        viewModelScope.launch { dao.setCategory(recipe.id, category.name) }
    }

    fun delete(recipe: Recipe) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { File(recipe.filePath).delete() }
            dao.delete(recipe)
        }
    }

    private suspend fun importOne(uri: Uri) {
        val context = getApplication<Application>()
        val displayName = withContext(Dispatchers.IO) { queryDisplayName(uri) }
        val fileLabel = displayName?.removeSuffix(".pdf")?.removeSuffix(".PDF") ?: "Untitled recipe"

        val target = withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "recipes").apply { mkdirs() }
            val file = File(dir, "${UUID.randomUUID()}.pdf")
            val copied = context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
                true
            } ?: false
            if (copied) file else null
        }
        if (target == null) {
            _messages.tryEmit("Couldn't open $fileLabel")
            return
        }

        val text = withContext(Dispatchers.IO) { PdfTextExtractor.extract(target) }
        if (text.isBlank()) {
            _messages.tryEmit("No readable text in $fileLabel (scanned PDF?). Sorted by file name.")
        }
        val title = RecipeTextParser.title(text, fallback = fileLabel)
        val category = RecipeClassifier.classify(title, text.ifBlank { fileLabel })

        dao.insert(
            Recipe(
                title = title,
                category = category.name,
                filePath = target.absolutePath,
                extractedText = text,
                addedAt = System.currentTimeMillis(),
            ),
        )
    }

    private fun queryDisplayName(uri: Uri): String? =
        getApplication<Application>().contentResolver
            .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
}
