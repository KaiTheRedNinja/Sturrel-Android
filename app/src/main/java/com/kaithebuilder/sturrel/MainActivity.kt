package com.kaithebuilder.sturrel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaithebuilder.sturrel.model.sturrelVocab.FileManager
import com.kaithebuilder.sturrel.model.sturrelVocab.FileName
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.sturrelTypes.DefaultFolder
import com.kaithebuilder.sturrel.sturrelTypes.Vocab
import com.kaithebuilder.sturrel.sturrelTypes.VocabFolder
import com.kaithebuilder.sturrel.ui.theme.SturrelTheme
import java.io.File
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileManager.instance.filesDir = this.filesDir

        /*
        val sampleFolders = mutableListOf<VocabFolder>()
        for (i in 1..5) {
            val folderName = "Sample Folder $i"
            val vocabFolder = VocabFolder(name = folderName, subfolders = emptyList(), vocab = emptyList())
            sampleFolders.add(vocabFolder)
            FoldersDataManager.instance.saveFolder(vocabFolder)
        }

        val sampleVocabs = mutableListOf<Vocab>()
        for (i in 1..10) {
            val vocabName = "你好世界 $i"
            val vocab = Vocab(
                word = vocabName,
                isHCL = false,
                englishDefinition =  "",
                definition = "",
                sentences = emptyList(),
                wordBuilding = emptyList()
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
        */

        val p1FileName = FileName()
        p1FileName.path = "DEFAULT_P1.json"

        val p1DefaultFolder = FileManager.instance.read(
            stream = this.assets.open("DEFAULT_P1.json"),
            decodeType = DefaultFolder::class.java
        )

        val p1Folder = VocabFolder(
            name = "P1",
            subfolders = p1DefaultFolder.folders.map { it.id },
            vocab = emptyList()
        )

        for (folder in p1DefaultFolder.folders) {
            folder.subfolders = emptyList()
            FoldersDataManager.instance.saveFolder(folder = folder)
        }
        for (vocab in p1DefaultFolder.vocab) {
            VocabDataManager.instance.saveVocab(vocab = vocab)
        }
        FoldersDataManager.instance.saveFolder(folder = p1Folder)

        setContent {
            SturrelTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "root") {
                        composable("root") {
                            FolderListView(
                                folderId = p1Folder.id,
                                nav = navController
                            )
                        }
                        composable(
                            "folder/{folderId}",
                            arguments = listOf(navArgument("folderId") { type = NavType.StringType }),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                                )
                            }
                        ) { backStackEntry ->
                            FolderListView(
                                folderId = UUID.fromString(
                                    backStackEntry.arguments!!
                                        .getString("folderId")!!
                                        .toString()
                                ),
                                nav = navController
                            )
                        }
                        composable(
                            "vocab/{vocabId}",
                            arguments = listOf(navArgument("vocabId") { type = NavType.StringType }),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                                )
                            }
                        ) { backStackEntry ->
                            VocabDetailView(
                                vocabId = UUID.fromString(
                                    backStackEntry.arguments!!
                                        .getString("vocabId")!!
                                        .toString()
                                ),
                                nav = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
