package com.kaithebuilder.sturrel.sturrelTypes

import com.google.gson.annotations.SerializedName
import java.util.*

data class Vocab(
    val id: UUID = UUID.randomUUID(),
    var word: String,
    var isHCL: Boolean,
    @SerializedName("english_definition")
    var englishDefinition: String,
    var definition: String,
    @SerializedName("model_sentences")
    var sentences: List<String>,
    @SerializedName("word_building")
    var wordBuilding: List<String>
) {

}