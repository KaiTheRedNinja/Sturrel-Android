package com.kaithebuilder.sturrel.sturrelTypes

import java.util.*

data class Vocab(
    val id: UUID = UUID.randomUUID(),
    val word: String,
    val isHCL: Boolean,
    val englishDefinition: String,
    val definition: String,
    val sentences: List<String>,
    val wordBuilding: List<String>
) {

}