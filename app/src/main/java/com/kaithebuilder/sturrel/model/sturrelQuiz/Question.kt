package com.kaithebuilder.sturrel.model.sturrelQuiz

import com.kaithebuilder.sturrel.base.sturrelTypes.Vocab
import java.util.UUID

data class Question(
    var id: UUID,
    var associatedVocab: Vocab,
    var question: String,
    var answer: String
)