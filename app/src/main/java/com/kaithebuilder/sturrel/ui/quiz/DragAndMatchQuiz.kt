package com.kaithebuilder.sturrel.ui.quiz

import android.graphics.Point
import android.util.Log
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
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DragAndMatchQuiz(
    manager: QuizManager
) {
    var loadedQuestions by remember {
        mutableStateOf(listOf<Question>())
    }

    var rightPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    var rightHeight by remember {
        mutableIntStateOf(0)
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

    Column(
        modifier = Modifier
            .background(color = flashColorState)
    ) {
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
                        .weight(1f)
                        .zIndex(2f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    loadedQuestions.forEachIndexed { index, item ->
                        DragMatchCard(
                            text = item.question,
                            color = Color(0xFFFFA500), // orange
                            onDrop = { pos ->
                                if (pos.x > rightPosition.x) {
                                    // determine which item it was on
                                    Log.d("QUESTION", "RPOS: $rightPosition, H: $rightHeight")
                                    val boxHeight = rightHeight/loadedQuestions.count()
                                    val boxNo = ((pos.y-rightPosition.y)/boxHeight).toInt()
                                    if (boxNo == index) {

                                        Log.d("QUESTION", "Y BOX $boxNo")

                                        // new question
                                        val newQn = manager.nextQuestion()

                                        loadedQuestions =
                                            loadedQuestions.subList(0, index) +
                                            loadedQuestions.subList(index + 1, loadedQuestions.count())

                                        if (newQn != null) {
                                            loadedQuestions += newQn
                                        }

                                        flashColor = Color.Green.copy(alpha = 0.5f)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(300)
                                            flashColor = Color.Unspecified
                                        }
                                    } else {
                                        // wrong

                                        Log.d("QUESTION", "N BOX $boxNo")

                                        flashColor = Color.Red.copy(alpha = 0.5f)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(300)
                                            flashColor = Color.Unspecified
                                        }
                                    }
                                }
                            }
                        )
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
                    loadedQuestions.forEach { item ->
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
}

@Composable
private fun ColumnScope.DragMatchCard(
    text: String,
    color: Color,
    onDrop: ((Offset) -> Unit)?
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var positionInRootTopBar by remember { mutableStateOf(Offset.Zero) }

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
