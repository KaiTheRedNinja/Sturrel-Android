package com.kaithebuilder.sturrel.model.sturrelQuiz

enum class Quiz() {
    DRAG_AND_MATCH;
    fun description(): String {
        when (this) {
            DRAG_AND_MATCH -> {
                return "Drag and Match"
            }
        }
    }

    fun id(): String {
        return when (this) {
            DRAG_AND_MATCH -> "dnm"
        }
    }
}
