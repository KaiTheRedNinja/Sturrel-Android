package com.kaithebuilder.sturrel.model.sturrelQuiz

class QuizManager(
    var statsToShow: Set<QuizStat>,
    var questions: List<Question>,
    var questionIndex: Int = 0,
    var attempts: List<QuestionAttempt> = emptyList(),
    var inPlay: Boolean = true
) {
    fun nextQuestion(): Question? {
        if (questions.count() <= questionIndex) {
            return null
        }
        questionIndex += 1
        return questions[questionIndex-1]
    }

    fun makeAttempt(attempt: QuestionAttempt) {
        attempts += attempt
    }

    fun stat(stat: QuizStat): Int {
        return when (stat) {
            QuizStat.TOTAL -> questions.count()
            QuizStat.COMPLETED -> questionIndex
            QuizStat.REMAINING -> questions.count() - questionIndex
            QuizStat.WRONG -> attempts.count { !it.isCorrect() }
            QuizStat.CORRECT -> attempts.count { it.isCorrect() }
        }
    }

    fun restart() {
        questionIndex = 0
        attempts = emptyList()
        inPlay = true
    }
}
