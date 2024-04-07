package com.kaithebuilder.sturrel.sturrelTypes

import java.util.*

data class VocabFolder(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val subfolders: List<UUID>,
    val vocab: List<UUID>
) {

}