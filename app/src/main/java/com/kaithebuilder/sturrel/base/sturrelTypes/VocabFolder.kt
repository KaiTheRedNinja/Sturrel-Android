package com.kaithebuilder.sturrel.base.sturrelTypes

import java.util.*

data class VocabFolder(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var subfolders: List<UUID>,
    var vocab: List<UUID>
) {

}