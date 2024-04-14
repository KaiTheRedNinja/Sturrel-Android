package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuestionAttempt
import com.kaithebuilder.sturrel.model.sturrelQuiz.Quiz
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import com.kaithebuilder.sturrel.ui.quiz.quizzes.DragAndMatchQuizContents
import com.kaithebuilder.sturrel.ui.quiz.quizzes.FlashCardsQuizContents
import com.kaithebuilder.sturrel.ui.quiz.quizzes.MemoryCardsQuizContents
import com.kaithebuilder.sturrel.ui.quiz.quizzes.QuestionAnswerQuizContents
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

    val flashColorState by animateColorAsState(targetValue = flashColor, label = "flashColor")

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

        // remove question
        when (quizType) {
            Quiz.DRAG_AND_MATCH, Quiz.MEMORY_CARDS -> {
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
            }
            Quiz.QNA, Quiz.FLASH_CARDS -> {
                loadedQuestions = emptyList()
            }
        }

        // add questions
        when (quizType) {
            Quiz.MEMORY_CARDS -> {
                if (loadedQuestions.isEmpty()) {
                    for (i in 0..<8) {
                        val newQn = manager.nextQuestion()
                        if (newQn != null) {
                            loadedQuestions += newQn
                        }
                    }

                    questionSet += 1
                }
            }
            else -> {
                // new question
                val newQn = manager.nextQuestion()
                if (newQn != null) {
                    loadedQuestions += newQn
                }
            }
        }

        // if the loaded questions are empty, game over.
        if (loadedQuestions.isEmpty()) {
            inPlay = false
            manager.inPlay = false
        }
    }

    fun attemptQuestion(attempt: QuestionAttempt) {
        if (attempt.isCorrect()) {
            flashColor = Color.Green.copy(alpha = 0.5f)
            // load next question
            newQuestion(solvedQuestion = attempt.question.id)
        } else {
            flashColor = Color.Red.copy(alpha = 0.5f)
        }

        manager.makeAttempt(attempt)

        gameStateCounter += 1

        Timer().schedule(timerTask {
            flashColor = Color.Unspecified
        }, 300)
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
                    QuestionAnswerQuizContents(
                        question = loadedQuestions.last(),
                        didAttemptQuestion = { attempt ->
                            attemptQuestion(attempt = attempt)
                        }
                    )
                }
                Quiz.FLASH_CARDS -> {
                    FlashCardsQuizContents(
                        question = loadedQuestions.last(),
                        didAttemptQuestion = { attempt ->
                            attemptQuestion(attempt = attempt)
                        }
                    )
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