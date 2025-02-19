package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuestionAttempt
import com.kaithebuilder.sturrel.model.sturrelQuiz.Quiz
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import com.kaithebuilder.sturrel.ui.quiz.quizzes.DragAndMatchQuizContents
import com.kaithebuilder.sturrel.ui.quiz.quizzes.FlashCardsQuizContents
import com.kaithebuilder.sturrel.ui.quiz.quizzes.MemoryCardsQuizContents
import com.kaithebuilder.sturrel.ui.quiz.quizzes.QuestionAnswerQuizContents
import com.kaithebuilder.sturrel.ui.theme.PastelColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.timerTask

@Composable
fun QuizAdaptor(
    manager: QuizManager,
    nav: NavHostController,
    quizType: Quiz
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

    var questionSet by remember { // this will only be used by MemoryCardsQuiz
        mutableIntStateOf(0)
    }

    var flashColor by remember {
        mutableStateOf(Color.Unspecified)
    }

    var showFlash by remember {
        mutableStateOf(false)
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (showFlash) 1.0f else 0f,
        label = "alpha"
    )

    LaunchedEffect(manager) {
        loadedQuestions = emptyList()
        when (quizType) {
            Quiz.DRAG_AND_MATCH, Quiz.MEMORY_CARDS -> {
                val qnCount = if (quizType == Quiz.DRAG_AND_MATCH) { 5 } else { 8 }
                for (i in 0..<qnCount) {
                    val qn = manager.nextQuestion() ?: break
                    loadedQuestions += qn
                }
            }
            Quiz.QNA, Quiz.FLASH_CARDS -> {
                val qn = manager.nextQuestion()
                if (qn != null) {
                    loadedQuestions += qn
                }
            }
        }

        questionSet += 1
    }

    fun newQuestion(solvedQuestion: UUID?) {
        // if its drag and match, remove the question and replace it
        // if its memory quiz, remove the question. Only replace once all empty.
        // if its qna or flashcards, set the qn to the next one

        var newQuestions = emptyList<Question>()
        // remove question
        when (quizType) {
            Quiz.DRAG_AND_MATCH, Quiz.MEMORY_CARDS -> {
                // determine the question to remove
                if (solvedQuestion != null) {
                    val removeIndex = loadedQuestions.indexOfFirst {
                        it.id == solvedQuestion
                    }
                    if (removeIndex != -1) {
                        newQuestions =
                            loadedQuestions.subList(0, removeIndex) +
                                    loadedQuestions.subList(removeIndex + 1, loadedQuestions.count())
                    }
                }
            }
            Quiz.QNA, Quiz.FLASH_CARDS -> {
                newQuestions = emptyList()
            }
        }

        // add questions
        when (quizType) {
            Quiz.MEMORY_CARDS -> {
                if (newQuestions.isEmpty()) {
                    for (i in 0..<8) {
                        val newQn = manager.nextQuestion()
                        if (newQn != null) {
                            newQuestions = newQuestions + newQn
                        }
                    }
                }
            }
            else -> {
                // new question
                val newQn = manager.nextQuestion()
                if (newQn != null) {
                    newQuestions = newQuestions + newQn
                }
            }
        }

        // if the loaded questions are empty, game over.
        val endGame = newQuestions.isEmpty()

        // apply the changes
        when (quizType) {
            Quiz.DRAG_AND_MATCH, Quiz.MEMORY_CARDS -> {
                loadedQuestions = newQuestions
                if (endGame) {
                    inPlay = false
                    manager.inPlay = false
                }
                questionSet += 1
            }
            Quiz.FLASH_CARDS, Quiz.QNA -> {
                Timer().schedule(timerTask {
                    loadedQuestions = newQuestions
                    if (endGame) {
                        inPlay = false
                        manager.inPlay = false
                    }
                }, if (quizType == Quiz.FLASH_CARDS) { 500 } else { 1000 })
            }
        }
    }

    fun attemptQuestion(attempt: QuestionAttempt) {
        if (attempt.isCorrect()) {
            flashColor = PastelColor.Green.copy(alpha = 0.5f)
            // load next question
            newQuestion(solvedQuestion = attempt.question.id)
        } else {
            flashColor = PastelColor.Red.copy(alpha = 0.5f)
            if (quizType == Quiz.QNA || quizType == Quiz.FLASH_CARDS) {
                // load next question
                newQuestion(solvedQuestion = attempt.question.id)
            }
        }

        if (quizType == Quiz.FLASH_CARDS) {
            flashColor = Color.Unspecified
        } else {
            showFlash = true
        }

        manager.makeAttempt(attempt)

        gameStateCounter += 1

        Timer().schedule(timerTask {
            showFlash = false
        }, 800)
    }

    if (inPlay) {
        Box {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animatedAlpha
                    }
                    .background(color = flashColor)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {}
            Column {
                key(gameStateCounter) {
                    QuizInfoView(manager = manager, endGame = {
                        inPlay = false
                        manager.inPlay = false
                    })
                }

                when (quizType) {
                    Quiz.DRAG_AND_MATCH -> {
                        DragAndMatchQuizContents(
                            loadedQuestions = loadedQuestions,
                            didAttemptQuestion = { attempt ->
                                attemptQuestion(attempt = attempt)
                            }
                        )
                    }

                    Quiz.MEMORY_CARDS -> {
                        MemoryCardsQuizContents(
                            loadedQuestions = loadedQuestions,
                            questionSet = questionSet,
                            didAttemptQuestion = { attempt ->
                                attemptQuestion(attempt = attempt)
                            }
                        )
                    }

                    Quiz.QNA -> {
                        if (loadedQuestions.isNotEmpty()) {
                            QuestionAnswerQuizContents(
                                question = loadedQuestions.last(),
                                answerType = manager.answerType,
                                questionPool = manager.questions,
                                didAttemptQuestion = { attempt ->
                                    attemptQuestion(attempt = attempt)
                                }
                            )
                        }
                    }

                    Quiz.FLASH_CARDS -> {
                        if (loadedQuestions.isNotEmpty()) {
                            FlashCardsQuizContents(
                                question = loadedQuestions.last(),
                                didAttemptQuestion = { attempt ->
                                    attemptQuestion(attempt = attempt)
                                }
                            )
                        }
                    }
                }
            }
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