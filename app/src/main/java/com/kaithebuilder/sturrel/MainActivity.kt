package com.kaithebuilder.sturrel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.sturrelTypes.Vocab
import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder
import com.kaithebuilder.sturrel.ui.theme.SturrelTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sampleFolders = mutableListOf<VocabFolder>()
        for (i in 1..10) {
            val folderName = "Sample Folder $i"
            val vocabFolder = VocabFolder(name = folderName, subfolders = emptyList(), vocab = emptyList())
            sampleFolders.add(vocabFolder)
            FoldersDataManager.instance.saveFolder(vocabFolder)
        }

        val sampleVocabs = mutableListOf<Vocab>()
        for (i in 1..10) {
            val vocabName = "Sample Vocab $i"
            val vocab = Vocab(
                word = vocabName,
                isHCL = false,
                englishDefinition =  "",
                definition = "",
                sentences = emptyArray(),
                wordBuilding = emptyArray()
            )
            sampleVocabs.add(vocab)
            VocabDataManager.instance.saveVocab(vocab)
        }

        val rootFolder = VocabFolder(
            name = "Root",
            subfolders = sampleFolders.map { it.id },
            vocab = sampleVocabs.map { it.id }
        )

        FoldersDataManager.instance.saveFolder(rootFolder)

        setContent {
            SturrelTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VocabListView(
                        folder = rootFolder
                    )
                }
            }
        }
    }
}
