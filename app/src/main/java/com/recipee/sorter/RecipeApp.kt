package com.recipee.sorter

import android.app.Application
import com.recipee.sorter.data.AppDatabase
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class RecipeApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.create(this) }

    override fun onCreate() {
        super.onCreate()
        // PdfBox-Android needs its font/glyph resources initialised once.
        PDFBoxResourceLoader.init(applicationContext)
    }
}
