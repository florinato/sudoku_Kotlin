// PuzzleGenerator.kt
package com.example.sudoku.core

import android.content.Context
import kotlin.random.Random

/**
 * Genera un puzzle mapeando letras a dígitos aleatorios.
 */
data class Puzzle(val solution: IntArray, val puzzle: IntArray)

object PuzzleGenerator {
    fun generate(template: String, numCluesToKeep: Int): Puzzle {
        require(template.length == 81) { "Template must be 81 characters long" }

        val chars = template.toSet().toList()
        val digits = (1..9).shuffled().take(chars.size)
        val map = chars.zip(digits).toMap()

        val solution = IntArray(81) { i ->
            map[template[i]] ?: 0
        }

        val puzzle = solution.copyOf()
        val indicesToRemove = (0 until 81).shuffled().take(81 - numCluesToKeep)
        for (index in indicesToRemove) {
            puzzle[index] = 0
        }

        return Puzzle(solution, puzzle)
    }

    fun generateRandom(context: Context, numCluesToKeep: Int): Puzzle {
        val templates = TemplateLoader.loadTemplates(context)
        if (templates.isEmpty()) {
            throw IllegalStateException("No Sudoku templates loaded.")
        }
        val template = templates.random()
        return generate(template, numCluesToKeep)
    }
}
