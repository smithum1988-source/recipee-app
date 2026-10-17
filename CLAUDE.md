# Recipe Sorter (Android)

Android app that imports PDF recipes, reads their text, and sorts them into four categories:
**Breakfast, Lunch, Dinner, Smoothies & Desserts**. The user can move or delete any recipe.

## IMPORTANT: this code has never been compiled
It was written without an Android SDK, so no build or Android test has run yet. Only the
classifier logic was sanity-checked (outside Gradle). Your first job is to build it and fix
whatever breaks.

## First steps
1. No Gradle wrapper is checked in. Generate one: `gradle wrapper --gradle-version 8.7`
   (or open the project in Android Studio and let it sync), then commit `gradlew`,
   `gradlew.bat` and `gradle/wrapper/`.
2. `./gradlew testDebugUnitTest` - JUnit tests for the classifier and title parser. Fix failures.
3. `./gradlew assembleDebug` - fix any compile errors (Compose, Room/KSP, PdfBox imports).
4. Start an emulator (API 34 image) and run `./gradlew connectedDebugAndroidTest` (Room DAO tests).
5. Install with `./gradlew installDebug` and test by hand with several real recipe PDFs:
   import, tabs, move-to, delete, open viewer, rotate the screen, relaunch the app.
6. When changing the build, keep `.github/workflows/android.yml` working (CI runs unit tests and
   builds the debug APK on every push).

## Stack
- Kotlin 1.9.24, Jetpack Compose (BOM 2024.06.00, Material 3), AGP 8.5.2, minSdk 26, compileSdk 34
- Room 2.6.1 with KSP for storage
- PdfBox-Android (`com.tom-roush:pdfbox-android`) for text extraction
- Android's built-in `PdfRenderer` for showing PDFs
- No navigation library: `MainActivity` switches between list and viewer on a saved recipe id

## Layout (`app/src/main/java/com/recipee/sorter/`)
- `classify/` - `Category`, `RecipeClassifier` (keyword scoring), `RecipeTextParser` (title and snippet).
  Pure Kotlin, no Android imports, so it stays unit-testable.
- `pdf/` - `PdfTextExtractor` (PdfBox), `PdfPageSource` (PdfRenderer wrapper)
- `data/` - `Recipe` entity, `RecipeDao`, `AppDatabase`
- `ui/` - `RecipeListScreen` (tabs, cards, add button), `PdfViewerScreen`, `Theme`
- `RecipeViewModel` - import flow (copy PDF into app storage, extract text, classify, insert)
- `RecipeApp` - initialises PdfBox resources once

## How sorting works
Title keywords count 3x, body keywords 1x (max 3 hits per keyword). Highest category score wins;
ties prefer Smoothies & Desserts, then Breakfast, Lunch, Dinner. No keyword match falls back to Dinner.
Keyword lists live in `RecipeClassifier.KEYWORDS`; tune them against real PDFs and add a test for
each misclassification you fix.

## Known gaps / next ideas
- Scanned (image-only) PDFs have no text layer: they are sorted by file name only and the user is told.
  Possible fix: ML Kit OCR.
- `PdfRenderer` is assumed to scale pages to the bitmap size when no transform is given; check that pages
  look right and are not cropped or blurry.
- No search yet (extracted text is already stored, so a search box over `extractedText` is easy).
- No launcher icon (default is used); no UI tests yet (add Compose UI tests for the tabs and move-to menu).
- Large PDFs: viewer renders one bitmap per page; watch memory on long documents.

## Conventions
- Keep classification logic out of Android classes; add JUnit tests for every behaviour change.
- Don't add network permissions or analytics; everything stays on the device.
- Commit small and keep CI green.
