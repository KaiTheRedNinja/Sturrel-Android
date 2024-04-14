package com.kaithebuilder.sturrel.model.sturrelQuiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Quiz() {
    DRAG_AND_MATCH, MEMORY_CARDS, QNA, FLASH_CARDS;

    fun description(): String {
        return when (this) {
            DRAG_AND_MATCH -> "Drag and Match"
            MEMORY_CARDS -> "Memory Cards"
            QNA -> "Question-Answer"
            FLASH_CARDS -> "Flash Cards"
        }
    }

    fun icon(): ImageVector {
        return when(this) {
            DRAG_AND_MATCH -> Icons.AutoMirrored.Filled.ArrowForward
            MEMORY_CARDS -> Icons.Default.Menu
            QNA -> Icons.Default.Info
            FLASH_CARDS -> Icons.Default.FavoriteBorder
        }
    }

    fun id(): String {
        return when (this) {
            DRAG_AND_MATCH -> "dnm"
            MEMORY_CARDS -> "mc"
            QNA -> "una" // not using `q` because it conflicts with the quiz setup
            FLASH_CARDS -> "fc"
        }
    }

    companion object {
        val allCases = arrayOf(DRAG_AND_MATCH, MEMORY_CARDS, QNA, FLASH_CARDS)
    }
}
