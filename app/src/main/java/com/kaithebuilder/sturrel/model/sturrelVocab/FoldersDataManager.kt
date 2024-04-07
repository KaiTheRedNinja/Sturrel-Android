package com.kaithebuilder.sturrel.model.sturrelVocab

import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder
import java.util.UUID

class FoldersDataManager private constructor() {
    companion object {
        val instance: FoldersDataManager by lazy {
            FoldersDataManager()
        }
    }

    private val folders: MutableMap<UUID, VocabFolder> = mutableMapOf()

    fun getFolder(folderId: UUID): VocabFolder? {
        return folders[folderId]
    }

    fun saveFolder(folder: VocabFolder) {
        folders[folder.id] = folder
        // TODO: save to file system
    }

    fun removeFolder(folderId: UUID) {
        folders.remove(folderId)
        // TODO: save to file system
    }
}