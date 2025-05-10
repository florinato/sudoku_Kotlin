// SudokuGrid.kt (Jetpack Compose)
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.sudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.colorResource
import com.example.sudoku.R

fun isDiagonal(row: Int, col: Int): Boolean {
    return row == col || row + col == 8
}

@Composable
fun SudokuGrid(puzzle: IntArray, onValueChange: (index: Int, value: Int?) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        for (row in 0 until 9) {
            Row {
                for (col in 0 until 9) {
                    val idx = row * 9 + col
                    var cell by remember { mutableStateOf(if (puzzle[idx] != 0) puzzle[idx].toString() else "") }
                    val backgroundColor = if (isDiagonal(row, col)) {
                        colorResource(id = R.color.diagonal)
                    } else {
                        Color.White
                    }
                    TextField(
                        value = cell,
                        onValueChange = { new ->
                            val digit = new.take(1).toIntOrNull()
                            cell = digit?.toString() ?: ""
                            onValueChange(idx, digit)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, Color.Gray)
                            .background(backgroundColor)
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}
