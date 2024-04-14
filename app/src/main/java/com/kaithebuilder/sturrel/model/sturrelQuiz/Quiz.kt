package com.kaithebuilder.sturrel.model.sturrelQuiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.kaithebuilder.sturrel.R

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

    @Composable
    fun icon(): Painter {
        return when(this) {
            DRAG_AND_MATCH -> painterResource(id = R.drawable.baseline_filter_none_24)
            MEMORY_CARDS -> painterResource(id = R.drawable.baseline_grid_view_24)
            QNA -> painterResource(id = R.drawable.baseline_question_answer_24)
            FLASH_CARDS -> painterResource(id = R.drawable.baseline_flash_on_24)
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
