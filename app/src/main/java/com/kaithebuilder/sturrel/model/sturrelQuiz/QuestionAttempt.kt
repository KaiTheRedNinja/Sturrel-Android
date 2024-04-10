package com.kaithebuilder.sturrel.model.sturrelQuiz

import java.util.UUID

data class QuestionAttempt(
    var id: UUID,
    var question: Question,
    var givenAnswer: String
) {
    fun isCorrect(): Boolean {
        return question.answer == givenAnswer
    }
}
