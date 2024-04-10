package com.kaithebuilder.sturrel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.pinYin.PinYin
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder
import java.util.UUID

@Composable
fun FolderListView(
    folderId: UUID,
    nav: NavHostController
) {
    val folder = FoldersDataManager.instance.getFolder(folderId = folderId)!!
    FolderListViewContents(folder = folder, nav = nav)
}

@Composable
private fun FolderListViewContents(
    folder: VocabFolder,
    nav: NavHostController
) {
    NavList(title = folder.name, nav = nav) {
        if (folder.subfolders.isNotEmpty()) {
            item {
                ListSectionHeader(header = "Folders")
            }
            itemsIndexed(folder.subfolders) { index, uuid ->
                ListItem(index = index, totalSize = folder.subfolders.count()) {
                    Box(
                        modifier = Modifier.clickable {
                            println("UUID: $uuid")
                            nav.navigate("folder/$uuid")
                        }
                    ) {
                        FolderListPreview(id = uuid)
                    }
                }
            }
        }

        if (folder.vocab.isNotEmpty()) {
            item {
                ListSectionHeader(header = "Vocab")
            }
            itemsIndexed(folder.vocab) { index, uuid ->
                ListItem(index = index, totalSize = folder.vocab.count()) {
                    Box(
                        modifier = Modifier.clickable {
                            println("UUID: $uuid")
                            nav.navigate("vocab/$uuid")
                        }
                    ) {
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
        modifier = Modifier.padding(15.dp)
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