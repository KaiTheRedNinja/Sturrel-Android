package com.kaithebuilder.sturrel.model.sturrelQuiz

import androidx.compose.ui.graphics.Color
import com.kaithebuilder.sturrel.ui.theme.PastelColor

enum class QuizStat {
    TOTAL, COMPLETED, REMAINING, WRONG, CORRECT;
    fun color(): Color {
        return when (this) {
            TOTAL -> PastelColor.Orange
            COMPLETED -> PastelColor.Lavender
            REMAINING -> PastelColor.Cyan
            WRONG -> PastelColor.Red
            CORRECT -> PastelColor.Green
        }
    }

    companion object {
        val allCases: Array<QuizStat> = arrayOf(TOTAL, COMPLETED, REMAINING, WRONG, CORRECT)
    }
}