package com.kaithebuilder.sturrel.base.sturrelTypes

data class DefaultFolder(
    var name: String,
    var folders: List<VocabFolder>,
    var vocab: List<Vocab>
) {
}
