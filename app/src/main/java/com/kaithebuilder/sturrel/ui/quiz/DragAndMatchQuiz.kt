package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager

@Composable
fun DragAndMatchQuiz(
    manager: QuizManager
) {
    var loadedQuestions by remember {
        mutableStateOf(emptyList<Question>())
    }

    LaunchedEffect(manager) {
        loadedQuestions = emptyList()
        for (i in 0..<5) {
            val qn = manager.nextQuestion() ?: break
            loadedQuestions += qn
        }
    }

    Column {
        if (manager.inPlay) {
            QuizInfoView(manager = manager, endGame = { manager.inPlay = false })

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(all = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (index in 0..5) {
                        if (index < loadedQuestions.count()) {
                            DragMatchcard(
                                text = loadedQuestions[index].answer,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFFFA500) // orange
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (index in 0..5) {
                        if (index < loadedQuestions.count()) {
                            DragMatchcard(
                                text = loadedQuestions[index].answer,
                                modifier = Modifier.weight(1f),
                                color = Color.Blue
                            )
                        }
                    }
                }
            }

            Text("hello there")
        }
    }
}

@Composable()
private fun DragMatchcard(
    text: String,
    modifier: Modifier,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(modifier)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text,
                textAlign = TextAlign.Center
            )
        }
    }
}