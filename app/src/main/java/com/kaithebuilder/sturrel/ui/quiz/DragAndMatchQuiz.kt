package com.kaithebuilder.sturrel.ui.quiz

import android.graphics.Point
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuestionAttempt
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun DragAndMatchQuiz(
    manager: QuizManager,
    nav: NavHostController
) {
    var loadedQuestions by remember {
        mutableStateOf(listOf<Question>())
    }

    var inPlay by remember {
        mutableStateOf(manager.inPlay)
    }

    var gameStateCounter by remember {
        mutableIntStateOf(1)
    }

    var flashColor by remember {
        mutableStateOf(Color.Unspecified)
    }

    val flashColorState by animateColorAsState(targetValue = flashColor, label = "flashColor")

    LaunchedEffect(manager) {
        loadedQuestions = emptyList()
        for (i in 0..<5) {
            val qn = manager.nextQuestion() ?: break
            loadedQuestions += qn
        }
    }

    fun newQuestion(solvedQuestion: UUID?) {
        // determine the question to remove
        if (solvedQuestion != null) {
            val removeIndex = loadedQuestions.indexOfFirst {
                it.id == solvedQuestion
            }
            if (removeIndex != -1) {
                loadedQuestions =
                    loadedQuestions.subList(0, removeIndex) +
                    loadedQuestions.subList(removeIndex + 1, loadedQuestions.count())
            }
        }

        // new question
        val newQn = manager.nextQuestion()
        if (newQn != null) {
            loadedQuestions += newQn
        }

        if (loadedQuestions.isEmpty()) {
            inPlay = false
            manager.inPlay = false
        }
    }

    if (inPlay) {
        Column(
            modifier = Modifier
                .background(color = flashColorState)
        ) {
            key(gameStateCounter) {
                QuizInfoView(manager = manager, endGame = {
                    inPlay = false
                    manager.inPlay = false
                })
            }

            DragAndMatchQuizContents(
                loadedQuestions = loadedQuestions,
                didAttemptQuestion = { attempt ->
                    if (attempt.isCorrect()) {
                        flashColor = Color.Green.copy(alpha = 0.5f)
                        // load next question
                        newQuestion(solvedQuestion = attempt.question.id)
                    } else {
                        flashColor = Color.Red.copy(alpha = 0.5f)
                    }

                    manager.makeAttempt(attempt)

                    gameStateCounter += 1

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(300)
                        flashColor = Color.Unspecified
                    }
                }
            )
        }
    } else {
        // results
        QuizResultsView(
            manager = manager,
            nav = nav,
            resetGame = {
                inPlay = true
                manager.inPlay = true
            }
        )
    }
}

@Composable
fun DragAndMatchQuizContents(
    loadedQuestions: List<Question>,
    didAttemptQuestion: (QuestionAttempt) -> Unit
) {
    var rightPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    var rightHeight by remember {
        mutableIntStateOf(0)
    }

    var answers by remember {
        mutableStateOf<List<Question>>(emptyList())
    }

    LaunchedEffect(loadedQuestions) {
        answers = loadedQuestions.shuffled()
    }

    fun verifyAnswer(index: Int, pos: Offset) {
        val item = loadedQuestions[index]

        if (!(pos.x > rightPosition.x)) {
            return
        }

        // determine which item it was on
        val boxHeight = rightHeight / loadedQuestions.count()
        val boxNo = ((pos.y - rightPosition.y) / boxHeight).toInt()

        val attempt = QuestionAttempt(question = item, givenAnswer = answers[boxNo].answer)

        didAttemptQuestion(attempt)
    }

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
                .weight(1f)
                .zIndex(2f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            key(loadedQuestions, answers) {
                loadedQuestions.forEachIndexed { index, item ->
                    key(item.question) {
                        DragMatchCard(
                            text = item.question,
                            color = Color(0xFFFFA500), // orange
                            onDrop = { pos ->
                                verifyAnswer(index, pos)
                            }
                        )
                    }
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
                .weight(1f)
                .zIndex(1f)
                .onGloballyPositioned { coordinates ->
                    rightPosition = coordinates.positionInRoot()
                    rightHeight = coordinates.size.height
                },
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            answers.forEach { item ->
                key(item.answer) {
                    DragMatchCard(
                        text = item.answer,
                        color = Color.Blue,
                        onDrop = null
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.DragMatchCard(
    text: String,
    color: Color,
    onDrop: ((Offset) -> Unit)?
) {
    var offsetX by remember(text) { mutableFloatStateOf(0f) }
    var offsetY by remember(text) { mutableFloatStateOf(0f) }

    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var positionInRootTopBar by remember(text) { mutableStateOf(Offset.Zero) }

    val offsetXState = animateFloatAsState(targetValue = offsetX, label = "offsetX")
    val offsetYState = animateFloatAsState(targetValue = offsetY, label = "offsetY")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .weight(1f)
            .offset { IntOffset(offsetXState.value.roundToInt(), offsetYState.value.roundToInt()) }
            .pointerInput(Unit) {
                if (onDrop != null) {
                    detectDragGestures(
                        onDragEnd = {
                            Log.d(
                                "Drag Match",
                                "$offsetX, $offsetY, $positionInRootTopBar"
                            )

                            // get the midpoint
                            val midX = positionInRootTopBar.x + boxSize.width / 2
                            val midY = positionInRootTopBar.y + boxSize.height / 2

                            onDrop(Offset(midX, midY))
                            offsetX = 0f
                            offsetY = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
            }
            .onGloballyPositioned { coordinates ->
                coordinates.positionInRoot()
                boxSize = coordinates.size
                positionInRootTopBar = coordinates.positionInRoot()
            }
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
