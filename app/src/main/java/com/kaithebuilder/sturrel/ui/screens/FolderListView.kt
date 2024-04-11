package com.kaithebuilder.sturrel.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.ui.components.ListItem
import com.kaithebuilder.sturrel.ui.components.ListSectionHeader
import com.kaithebuilder.sturrel.ui.components.NavList
import com.kaithebuilder.sturrel.R
import com.kaithebuilder.sturrel.base.pinYin.PinYin
import com.kaithebuilder.sturrel.model.sturrelSearch.SearchManager
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.base.sturrelTypes.FolderOrVocab
import com.kaithebuilder.sturrel.base.sturrelTypes.VocabFolder
import com.kaithebuilder.sturrel.ui.components.EmbeddedSearchBar
import com.kaithebuilder.sturrel.ui.components.NavBox
import java.util.UUID

@Composable
fun FolderListView(
    folderId: UUID,
    nav: NavHostController
) {
    val folder = FoldersDataManager.instance.getFolder(folderId = folderId)!!
    FolderListViewContents(folder = folder, nav = nav)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FolderListViewContents(
    folder: VocabFolder,
    nav: NavHostController
) {
    var searchTerm by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }

    NavList(title = folder.name, nav = nav, topBar = {
        EmbeddedSearchBar(
            onQueryChange = {
                searchTerm = it
            },
            isSearchActive = searchActive,
            onActiveChanged = {
                searchActive = it
            }
        ) {
            LazyColumn {
                items(SearchManager.instance.searchResultsWithin(
                    folderId = folder.id,
                    searchText = searchTerm
                )) {
                    val dest = it.steps.reversed().map { item -> "f$item" }
                    when (it.result.contains) {
                        FolderOrVocab.FOLDER -> {
                            NavBox(nav, dest + "f${it.result.id}") {
                                FolderListPreview(id = it.result.id)
                            }
                        }
                        FolderOrVocab.VOCAB -> {
                            NavBox(nav, dest + "v${it.result.id}") {
                                VocabListPreview(id = it.result.id)
                            }
                        }
                    }
                }
            }
        }
    }, actions = {
        IconButton(onClick = {
            nav.navigate("q${folder.id}")
        }) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play Quiz"
            )
        }
    }) {
        if (folder.subfolders.isNotEmpty()) {
            stickyHeader {
                ListSectionHeader(header = "Folders")
            }
            itemsIndexed(folder.subfolders) { index, uuid ->
                ListItem(index = index, totalSize = folder.subfolders.count()) {
                    NavBox(nav, "f$uuid") {
                        FolderListPreview(id = uuid)
                    }
                }
            }
        }

        if (folder.vocab.isNotEmpty()) {
            stickyHeader {
                ListSectionHeader(header = "Vocab")
            }
            itemsIndexed(folder.vocab) { index, uuid ->
                ListItem(index = index, totalSize = folder.vocab.count()) {
                    NavBox(nav, "v$uuid") {
                        VocabListPreview(id = uuid)
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderListPreview(id: UUID) {
    val folderDetails = FoldersDataManager.instance.getFolder(id)!!
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_folder_24),
            contentDescription = "Folder icon",
            modifier = Modifier.padding(end = 15.dp)
        )
        Text(
            text = folderDetails.name
        )
    }
}

@Composable
private fun VocabListPreview(id: UUID) {
    val vocabDetails = VocabDataManager.instance.getVocab(id)!!

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(
            text = vocabDetails.word,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = PinYin.instance.getPinyinString(vocabDetails.word)
        )
    }
}