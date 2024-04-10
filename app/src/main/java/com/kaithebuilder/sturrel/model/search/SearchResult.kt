package com.kaithebuilder.sturrel.model.search

import com.kaithebuilder.sturrel.sturrelTypes.FolderOrVocabID
import java.util.UUID

data class SearchResult(
    var steps: List<UUID>,
    var result: FolderOrVocabID,
    var id: UUID = UUID.randomUUID()
) {
    fun adding(parent: UUID): SearchResult {
        val new = this.copy(steps = this.steps.toList())
        new.steps += parent
        return new
    }
}
