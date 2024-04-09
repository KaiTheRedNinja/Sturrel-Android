package com.kaithebuilder.sturrel.sturrelTypes

import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder

data class DefaultFolder(
    var name: String,
    var folders: List<VocabFolder>,
    var vocab: List<Vocab>
) {
}
