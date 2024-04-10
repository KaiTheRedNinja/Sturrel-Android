package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager

@Composable
fun DragAndMatchQuiz(
    manager: QuizManager,
) {
    Column {
        QuizInfoView(manager = manager, endGame = { manager.inPlay = false })
        Text("hello there")
    }
}
