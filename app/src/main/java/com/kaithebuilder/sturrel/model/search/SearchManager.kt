package com.kaithebuilder.sturrel.model.search

import com.kaithebuilder.sturrel.model.pinYin.PinYin
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.sturrelTypes.FolderOrVocab
import com.kaithebuilder.sturrel.sturrelTypes.FolderOrVocabID
import com.kaithebuilder.sturrel.sturrelTypes.Vocab
import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder
import java.util.UUID

class SearchManager private constructor() {
    companion object {
        val instance: SearchManager by lazy {
            SearchManager()
        }
    }

    private val searchTokens: Set<SearchToken> = setOf(SearchToken.FOLDERS, SearchToken.VOCAB)

    fun searchResultsWithin(
        folderId: UUID,
        searchText: String
    ): List<SearchResult> {
        if (searchText.isEmpty()) { return emptyList() }
        val lowerSearch = searchText.lowercase()

        val folder = FoldersDataManager.instance.getFolder(folderId = folderId)!!
        val results: MutableList<SearchResult> = mutableListOf()

        // check the folder itself
        if (matchesCriteria(folder = folder, searchText = lowerSearch)) {
            val result = FolderOrVocabID(contains = FolderOrVocab.FOLDER, id = folderId)
            results += SearchResult(steps = emptyList(), result = result)
        }

        // check vocabs
        for (vocabID in folder.vocab) {
            val vocab = VocabDataManager.instance.getVocab(vocabId = vocabID)!!
            if (matchesCriteria(vocab = vocab, searchText = lowerSearch)) {
                val result = FolderOrVocabID(contains = FolderOrVocab.VOCAB, id = vocabID)
                results += SearchResult(steps = emptyList(), result = result)
            }
        }

        // check folders
        for (subfolder in folder.subfolders) {
            val subfolderResults = searchResultsWithin(folderId = subfolder, searchText = searchText)
            results += subfolderResults.map { it.adding(parent = subfolder) }
        }

        return results
    }

    private fun matchesCriteria(folder: VocabFolder, searchText: String): Boolean {
        if (!searchTokens.contains(SearchToken.FOLDERS)) { return false }
        return folder.name.lowercase().contains(searchText)
    }

    private fun matchesCriteria(vocab: Vocab, searchText: String): Boolean {
        if (!searchTokens.contains(SearchToken.VOCAB)) { return false }
        return vocab.word.lowercase().contains(searchText) ||
                PinYin.instance.getPinyinString(vocab.word, format = false).contains(searchText)
    }
}