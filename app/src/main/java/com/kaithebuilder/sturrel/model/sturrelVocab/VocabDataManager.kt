package com.kaithebuilder.sturrel.model.sturrelVocab

import com.kaithebuilder.sturrel.sturrelTypes.Vocab
import java.util.UUID

class VocabDataManager private constructor() {
    companion object {
        val instance: VocabDataManager by lazy {
            VocabDataManager()
        }
    }

    private val vocabs: MutableMap<UUID, Vocab> = mutableMapOf()

    fun getVocab(vocabId: UUID): Vocab? {
        return vocabs[vocabId]
    }

    fun saveVocab(vocab: Vocab) {
        vocabs[vocab.id] = vocab
        // TODO: save to file system
    }

    fun removeVocab(vocabId: UUID) {
        vocabs.remove(vocabId)
        // TODO: save to file system
    }
}