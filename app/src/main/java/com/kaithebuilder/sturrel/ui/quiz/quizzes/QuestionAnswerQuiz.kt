package com.kaithebuilder.sturrel.ui.quiz.quizzes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaithebuilder.sturrel.model.sturrelQuiz.QAType
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuestionAttempt

@Composable
fun QuestionAnswerQuizContents(
    question: Question,
    answerType: QAType,
    questionPool: List<Question>,
    didAttemptQuestion: (QuestionAttempt) -> Unit
) {
    var candidateAnswers by remember {
        mutableStateOf(emptyList<Question>())
    }

    LaunchedEffect(question) {
        // get new answers using the question pool

        // select five random questions
        val randomQuestions = questionPool.shuffled().subList(0, 5)

        // if one of them is `question`, discard it, else remove the last item.
        val curQuesIndex = randomQuestions.indexOf(question)
        candidateAnswers = if (curQuesIndex != -1) {
            (
                randomQuestions.subList(0, curQuesIndex) +
                randomQuestions.subList(curQuesIndex+1, 5)
            )
        } else {
            randomQuestions.subList(0, 4)
        }
    }

    Column(
        modifier = Modifier.padding(all = 15.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
            Text(
                text = "What is the ${answerType.description()} of ${question.question}?",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black // Adjust color as needed
                ),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
//            for (rowNum in 0..<2) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(10.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                        .weight(1f)
//                ) {
//                    for (colNum in 0..<2) {
//                        val item = questionPool.getOrNull(rowNum*2+colNum)
//                        if (item != null) {
//                            QuestionAnswerCard(
//                                text = item.answer,
//                                optionNumber = rowNum*2+colNum,
//                                onTap = {
//                                    Log.d("QAQuiz", "Selected answer: ${item.answer}")
//                                }
//                            )
//                        } else {
//                            Spacer(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .fillMaxHeight()
//                                    .weight(1f)
//                            )
//                        }
//                    }
//                }
//            }
            for (itemNum in 0..<4) {
                val item = questionPool.getOrNull(itemNum)
                if (item != null) {
                    QuestionAnswerCard(
                        text = item.answer,
                        optionNumber = itemNum,
                        onTap = {
                            Log.d("QAQuiz", "Selected answer: ${item.answer}")
                        }
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.QuestionAnswerCard(
    text: String,
    optionNumber: Int,
    onTap: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (optionNumber) {
                0 -> Color.Green
                1 -> Color.Blue
                2 -> Color.Yellow
                3 -> Color(0xFFFFA500) // orange
                else -> Color.Gray
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .weight(1f)
            .clickable { onTap() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Adjust color as needed
                ),
            )
        }
    }
}