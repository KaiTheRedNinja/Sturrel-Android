package com.kaithebuilder.sturrel.sturrelTypes

import java.util.UUID

data class FolderOrVocabID(
    var contains: FolderOrVocab,
    var id: UUID
) {
}

enum class FolderOrVocab {
    FOLDER, VOCAB
}