package com.kaithebuilder.sturrel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder
import java.util.UUID

@Composable
fun VocabListView(
    folder: VocabFolder
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        // on below line we are specifying horizontal alignment
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = folder.name,
            style = TextStyle(
                color = Color.Black,
                fontSize = TextUnit(value = 30.0F, type = TextUnitType.Sp)
            ),
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 30.dp)
        )

        LazyColumn(
            Modifier.padding(all = 10.dp)
        ) {
            item {
                Text(
                    text = "Folders",
                    modifier = Modifier.padding(start = 15.dp, top = 30.dp),
                    color = Color.Gray
                )
            }
            items(folder.subfolders) { uuid ->
                FolderListPreview(id = uuid)
                Divider()
            }

            item {
                Text(
                    text = "Vocab",
                    modifier = Modifier.padding(start = 15.dp, top = 30.dp),
                    color = Color.Gray
                )
            }
            items(folder.vocab) { uuid ->
                VocabListPreview(id = uuid)
                Divider()
            }
        }
    }
}

@Composable
fun FolderListPreview(id: UUID) {
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
fun VocabListPreview(id: UUID) {
    val vocabDetails = VocabDataManager.instance.getVocab(id)!!

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(15.dp)
    ) {
        Text(
            text = vocabDetails.word
        )
    }
}