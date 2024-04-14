package com.kaithebuilder.sturrel.ui.quiz.quizzes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuestionAttempt
import java.util.UUID

data class QAItem(
    var id: UUID = UUID.randomUUID(),
    var qaType: QuestionOrAnswer,
    var question: Question
) {
    fun text(): String {
        return when (this.qaType) {
            QuestionOrAnswer.QN -> question.question
            QuestionOrAnswer.ANS -> question.answer
        }
    }
}

enum class QuestionOrAnswer {
    QN, ANS
}

@Composable
fun MemoryCardsQuizContents(
    loadedQuestions: List<Question>,
    questionSet: Int,
    didAttemptQuestion: (QuestionAttempt) -> Unit
) {
    var clickedItem by remember {
        mutableStateOf<QAItem?>(null)
    }

    var gridSize by remember {
        mutableIntStateOf(2)
    }

    var order by remember {
        mutableStateOf(emptyList<QAItem?>())
    }

    LaunchedEffect(questionSet) {
        // determine the size of grid, by the closest square number to 2*qnCount
        val qnCount = loadedQuestions.count()
        gridSize = 2
        while (gridSize*gridSize < qnCount*2) { gridSize += 1 }

        // populate `order`
        val items = loadedQuestions.map {
            QAItem(qaType = QuestionOrAnswer.QN, question = it)
        } + loadedQuestions.map {
            QAItem(qaType = QuestionOrAnswer.ANS, question = it)
        }

        order = items.shuffled()
    }

    fun checkMatch(item1: QAItem, item2: QAItem) {
        clickedItem = null

        if (item1.question.id != item2.question.id) {
            // got it wrong
            val attempt = QuestionAttempt(question = item1.question, givenAnswer = item2.text())
            didAttemptQuestion(attempt)
            return
        }

        // got it right!
        val attempt = QuestionAttempt(question = item1.question, givenAnswer = item1.question.answer)
        didAttemptQuestion(attempt)
        // remove the item from `order`
        for (i in 0..<order.count()) {
            val curItem = order[i]
            if (curItem?.id == item1.id || curItem?.id == item2.id) {
                val mutableOrder = order.toMutableList()
                mutableOrder[i] = null
                order = mutableOrder
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(all = 10.dp)
    ) {
        for (rowNum in 0..<gridSize) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                for (colNum in 0..<gridSize) {
                    val item = order.getOrNull(rowNum*gridSize + colNum)
                    if (item != null) {
                        MemoryCard(
                            qaItem = item,
                            flipped = clickedItem?.id == item.id,
                            onTap = {
                                if (clickedItem?.id == item.id) {
                                    clickedItem = null
                                } else {
                                    if (clickedItem != null) {
                                        checkMatch(clickedItem!!, item)
                                    } else { // attempting to match
                                        clickedItem = item
                                    }
                                }
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
}

@Composable
fun RowScope.MemoryCard(
    qaItem: QAItem,
    flipped: Boolean,
    onTap: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (flipped) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
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
                qaItem.text(),
                textAlign = TextAlign.Center
            )
        }
    }
}