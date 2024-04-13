package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import com.kaithebuilder.sturrel.ui.components.ListItem
import com.kaithebuilder.sturrel.ui.components.NavList

@Composable
fun QuizResultsView(
    manager: QuizManager,
    nav: NavHostController,
    resetGame: () -> Unit
) {
    NavList(
        title = "Results",
        nav = nav) {
        item {
            ListItem(index = 0, totalSize = 2) {
                Text(
                    "${manager.questions.count()} Questions",
                    modifier = Modifier.padding(15.dp)
                )
            }
            ListItem(index = 1, totalSize = 2) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text("${manager.attempts.count()} Attempts")
                    Text("${manager.attempts.count { it.isCorrect() }} Correct")
                    Text("${manager.attempts.count { !it.isCorrect()} } Incorrect")
                }

                // TODO: visualisation goes here
            }
        }

        item {
            ListItem(index = 0, totalSize = 1) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Button(onClick = { resetGame() }) {
                        Text("Replay")
                    }
                    Button(onClick = { nav.popBackStack() }) {
                        Text("Exit")
                    }
                }
            }
        }

        val attemptCount = manager.attempts.count()

        item {
            ListItem(index = 0, totalSize = attemptCount + 1) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(15.dp)
                ) {
                    Text("Question Filter")
                    Text("Picker :)")
                }
            }
        }
        itemsIndexed(manager.attempts) { index, attempt ->
            ListItem(index = index+1, totalSize = attemptCount+1) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(15.dp)
                ) {
                    Text(attempt.question.question)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Green
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            attempt.question.answer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    if (!attempt.isCorrect()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                attempt.givenAnswer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}