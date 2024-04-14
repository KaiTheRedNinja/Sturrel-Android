package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import com.kaithebuilder.sturrel.ui.components.ListItem
import com.kaithebuilder.sturrel.ui.components.NavList
import com.kaithebuilder.sturrel.ui.theme.PastelColor

enum class AttemptType {
    CORRECT, WRONG, ALL;

    fun description(): String {
        return when (this) {
            CORRECT -> "Correct"
            WRONG -> "Wrong"
            ALL -> "All"
        }
    }

    fun showAttempt(correct: Boolean): Boolean {
        return when (this) {
            CORRECT -> correct
            WRONG -> !correct
            ALL -> true
        }
    }

    companion object {
        val allCases: List<AttemptType> = listOf(CORRECT, WRONG, ALL)
    }
}

@Composable
fun QuizResultsView(
    manager: QuizManager,
    nav: NavHostController,
    resetGame: () -> Unit
) {
    var qnTypeExpanded by remember {
        mutableStateOf(false)
    }

    var attemptType by remember {
        mutableStateOf(AttemptType.ALL)
    }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text("Question Filter")

                    Row(
                        modifier = Modifier
                            .clickable { qnTypeExpanded = true }
                    ) {
                        Text(
                            attemptType.description(),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Answer Type",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    DropdownMenu(
                        expanded = qnTypeExpanded,
                        onDismissRequest = { qnTypeExpanded = false }
                    ) {
                        for (case in AttemptType.allCases) {
                            DropdownMenuItem(text = {
                                Text(case.description())
                            }, onClick = {
                                attemptType = case
                            })
                        }
                    }
                }
            }
        }

        itemsIndexed(manager.attempts) { index, attempt ->
            if (attemptType.showAttempt(correct = attempt.isCorrect())) {
                ListItem(index = index + 1, totalSize = attemptCount + 1) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(attempt.question.question)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PastelColor.Green
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
                                    containerColor = PastelColor.Red
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
}