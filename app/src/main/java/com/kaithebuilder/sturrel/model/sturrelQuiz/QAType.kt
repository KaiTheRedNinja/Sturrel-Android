package com.kaithebuilder.sturrel.model.sturrelQuiz

import com.kaithebuilder.sturrel.base.pinYin.PinYin
import com.kaithebuilder.sturrel.base.sturrelTypes.Vocab

enum class QAType {
    HANZI, PINYIN, DEFINITION;

    fun forVocab(vocab: Vocab): String {
        return when (this) {
            HANZI -> vocab.word
            PINYIN -> PinYin.instance.getPinyinString(vocab.word)
            DEFINITION -> vocab.englishDefinition
        }
    }

    fun description(): String {
        return when (this) {
            HANZI -> "Han Zi"
            PINYIN -> "Pin Yin"
            DEFINITION -> "Definition"
        }
    }

    fun deconflict(): QAType {
        return when (this) {
            HANZI -> PINYIN
            PINYIN -> DEFINITION
            DEFINITION -> HANZI
        }
    }

    companion object {
        val allCases: Array<QAType> = arrayOf(QAType.HANZI, QAType.PINYIN, DEFINITION)
    }
}