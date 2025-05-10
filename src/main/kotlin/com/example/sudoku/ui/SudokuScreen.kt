// SudokuScreen.kt (Jetpack Compose)
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.sudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.core.PuzzleGenerator
import com.example.sudoku.core.Puzzle

@Composable
fun SudokuScreen(onExit: () -> Unit) {
    val context = LocalContext.current

    val ColorInitial = Color(0xFF06b838)
    val ColorDefault = Color.Black
    val ColorHighlight = Color(0xFFFFD700)
    val ColorCursorInit = Color(0xFFFFD700)
    val ColorError = Color(0xFFFFCE9E)
    val ColorHint = Color(0xFFFFCE9E) // Usamos el mismo color que los errores
    val ColorDiagonal = Color(0xFFA0D8EF)
    val ColorButton = Color(0xFFDDDDDD)
    val ColorWhite = Color.White
    val ColorBlack = Color.Black

    var expanded by remember { mutableStateOf(false) }

    val difficultyLevels = mapOf(
        "Fácil" to 40,
        "Medio" to 36,
        "Difícil" to 30,
        "Experto" to 24,
        "Infernal" to 20
    )

    var currentDifficulty by remember { mutableStateOf("Fácil") }

    var puzzle by remember(currentDifficulty) {
        mutableStateOf(PuzzleGenerator.generateRandom(context, difficultyLevels[currentDifficulty]!!))
    }
    val initialGrid = remember { puzzle.puzzle.copyOf() }
    val solutionGrid = remember { puzzle.solution.copyOf() }
    var userGrid by remember { mutableStateOf(initialGrid.map { it }.toMutableList()) }
    var hintFlags by remember { mutableStateOf(Array(9) { BooleanArray(9) { false } }) }
    var cursorPos by remember { mutableStateOf(0 to 0) }
    var errorCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    val numberLabels = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    val arrowTextStyle = TextStyle(fontSize = 14.sp, color = ColorBlack)

    fun evaluate() {
        val errors = mutableSetOf<Pair<Int, Int>>()
        fun checkLine(cells: List<Pair<Int, Int>>) {
            val seen = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
            for ((r, c) in cells) {
                val v = userGrid[r * 9 + c]
                if (v != 0) seen.getOrPut(v) { mutableListOf() }.add(r to c)
            }
            seen.filter { it.value.size > 1 }.values.flatten().forEach { errors.add(it) }
        }
        for (i in 0 until 9) {
            checkLine((0 until 9).map { i to it })
            checkLine((0 until 9).map { it to i })
        }
        for (br in 0 until 3) for (bc in 0 until 3) {
            val cells = mutableListOf<Pair<Int, Int>>()
            for (r in br * 3 until br * 3 + 3) for (c in bc * 3 until bc * 3 + 3) cells.add(r to c)
            checkLine(cells)
        }
        checkLine((0 until 9).map { it to it })
        checkLine((0 until 9).map { it to (8 - it) })
        errorCells = errors
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.wrapContentSize(Alignment.TopStart)) {
            TextButton(onClick = { expanded = true }, colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)) {
                Text(text = "Dificultad: $currentDifficulty", color = ColorBlack)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                difficultyLevels.forEach { (level, value) ->
                    DropdownMenuItem(text = { Text(level, color = ColorBlack) }, onClick = {
                        currentDifficulty = level
                        expanded = false
                        puzzle = PuzzleGenerator.generateRandom(context, value)
                        initialGrid.indices.forEach { initialGrid[it] = puzzle.puzzle[it] }
                        userGrid = puzzle.puzzle.map { it }.toMutableList()
                        hintFlags = Array(9) { BooleanArray(9) }
                        errorCells = emptySet()
                    })
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (r in 0 until 9) {
                Row {
                    for (c in 0 until 9) {
                        val idx = r * 9 + c
                        val isInit = initialGrid[idx] != 0
                        val value = if (userGrid[idx] == 0) "" else userGrid[idx].toString()
                        val bg = when {
                            cursorPos == r to c -> ColorCursorInit
                            errorCells.contains(r to c) -> ColorError
                            hintFlags[r][c] -> ColorHint
                            r == c || r + c == 8 -> ColorDiagonal
                            else -> ColorWhite
                        }
                        val textColor = if (isInit) ColorInitial else ColorDefault
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .border(1.dp, ColorBlack)
                                .background(bg)
                                .clickable { cursorPos = r to c }
                        ) {
                            Text(text = value, color = textColor, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                numberLabels.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.Center) {
                        row.forEach { label ->
                            TextButton(
                                onClick = {
                                    val (r, c) = cursorPos
                                    val idx = r * 9 + c
                                    if (initialGrid[idx] == 0) {
                                        userGrid = userGrid.toMutableList().also {
                                            it[idx] = label.toInt()
                                        }
                                        evaluate()
                                    }
                                },
                                modifier = Modifier.padding(2.dp).size(36.dp),
                                colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)
                            ) {
                                Text(text = label, fontSize = 14.sp, color = ColorBlack)
                            }
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(
                    onClick = { cursorPos = ((cursorPos.first - 1).mod(9)) to cursorPos.second },
                    modifier = Modifier.size(36.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)
                ) { Text("↑", style = arrowTextStyle) }
                Row {
                    TextButton(
                        onClick = { cursorPos = cursorPos.first to ((cursorPos.second - 1).mod(9)) },
                        modifier = Modifier.size(36.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)
                    ) { Text("←", style = arrowTextStyle) }
                    TextButton(
                        onClick = {
                            val (r, c) = cursorPos
                            val idx = r * 9 + c
                            userGrid = userGrid.toMutableList().also {
                                it[idx] = 0
                            }
                            evaluate()
                        },
                        modifier = Modifier.size(36.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)
                    ) { Text("C", style = arrowTextStyle) }
                    TextButton(
                        onClick = { cursorPos = cursorPos.first to ((cursorPos.second + 1).mod(9)) },
                        modifier = Modifier.size(36.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)
                    ) { Text("→", style = arrowTextStyle) }
                }
                TextButton(
                    onClick = { cursorPos = ((cursorPos.first + 1).mod(9)) to cursorPos.second },
                    modifier = Modifier.size(36.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)
                ) { Text("↓", style = arrowTextStyle) }
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = {
            hintFlags = Array(9) { r -> BooleanArray(9) { c ->
                val idx = r * 9 + c
                initialGrid[idx] == 0 && userGrid[idx] != 0 && userGrid[idx] != solutionGrid[idx]
            } }
        }, colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)) {
            Text("Pista", color = ColorBlack)
        }

        Spacer(Modifier.height(4.dp))
        TextButton(onClick = {
            puzzle = PuzzleGenerator.generateRandom(context, difficultyLevels[currentDifficulty]!!)
            initialGrid.indices.forEach { initialGrid[it] = puzzle.puzzle[it] }
            userGrid = puzzle.puzzle.map { it }.toMutableList()
            hintFlags = Array(9) { BooleanArray(9) }
            errorCells = emptySet()
        }, colors = ButtonDefaults.textButtonColors(containerColor = ColorButton)) {
            Text("Nuevo", color = ColorBlack)
        }
    }
}
