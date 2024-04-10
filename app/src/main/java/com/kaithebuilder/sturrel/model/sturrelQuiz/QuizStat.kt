package com.kaithebuilder.sturrel.model.sturrelQuiz

import androidx.compose.ui.graphics.Color

enum class QuizStat {
    TOTAL, COMPLETED, REMAINING, WRONG, CORRECT;

    fun color(): Color {
        return when (this) {
            TOTAL -> Color(0xFFFFA500) // Orange
            COMPLETED -> Color.Magenta // Indigo
            REMAINING -> Color.Cyan
            WRONG -> Color.Red
            CORRECT -> Color.Green
        }
    }
}