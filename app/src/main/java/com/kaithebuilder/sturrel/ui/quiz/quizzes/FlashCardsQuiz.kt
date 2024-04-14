package com.kaithebuilder.sturrel.ui.quiz.quizzes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuestionAttempt

@Composable
fun FlashCardsQuizContents(
    question: Question,
    didAttemptQuestion: (QuestionAttempt) -> Unit
) {
    var knownCards by remember { mutableIntStateOf(0) }
    var unknownCards by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(bottom = 50.dp)
    ) {
        FlashCard(question = question, flipped = flipped, onTap = { flipped = !flipped })
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                onClick = {
                knownCards += 1
                didAttemptQuestion(QuestionAttempt(question = question, givenAnswer = question.answer))
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        "$knownCards",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text("I know this")
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = {
                unknownCards += 1
                didAttemptQuestion(QuestionAttempt(question = question, givenAnswer = "Unfamiliar"))
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        "$unknownCards",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text("I don't know this")
                }
            }
        }
    }
}

@Composable
fun ColumnScope.FlashCard(
    question: Question,
    flipped: Boolean,
    onTap: () -> Unit
) {
    val rotation = if (flipped) { 0f } else { 180f }
    val cardColor = if (flipped) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val rotationState = animateFloatAsState(targetValue = rotation, label = "CardRotation")
    val cardColorState = animateColorAsState(targetValue = cardColor, label = "CardColor")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColorState.value
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .aspectRatio(0.7f)
            .padding(all = 30.dp)
            .weight(1f)
            .clickable { onTap() }
            .graphicsLayer {
                rotationY = rotationState.value
                cameraDistance = 30f
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text = if (rotationState.value > 90f) {
                    question.question
                } else {
                    question.answer
                },
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .graphicsLayer {
                        rotationY = rotationState.value
                    }
            )
        }
    }
}