// TemplateLoader.kt
package com.example.sudoku.core

import android.content.Context

/**
 * Carga las plantillas desde assets/plantillas.txt.
 * Cada línea del archivo es una plantilla de 81 letras.
 */
object TemplateLoader {
    fun loadTemplates(context: Context): List<String> {
        val templates = mutableListOf<String>()
        val current = mutableListOf<String>()

        context.assets.open("sudoku_templates.txt").bufferedReader().useLines { lines ->
            for (line in lines) {
                val clean = line.trim()
                if (clean.isNotEmpty()) {
                    current.add(clean)
                    if (current.size == 9) {
                        templates.add(current.joinToString(""))
                        current.clear()
                    }
                }
            }
        }

        return templates.shuffled()
    }
}

