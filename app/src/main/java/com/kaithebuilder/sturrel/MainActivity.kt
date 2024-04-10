package com.kaithebuilder.sturrel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaithebuilder.sturrel.model.sturrelVocab.FileManager
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.base.sturrelTypes.DefaultFolder
import com.kaithebuilder.sturrel.base.sturrelTypes.VocabFolder
import com.kaithebuilder.sturrel.model.sturrelQuiz.Quiz
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import com.kaithebuilder.sturrel.ui.quiz.DragAndMatchQuiz
import com.kaithebuilder.sturrel.ui.quiz.QuizSetupView
import com.kaithebuilder.sturrel.ui.screens.FolderListView
import com.kaithebuilder.sturrel.ui.screens.VocabDetailView
import com.kaithebuilder.sturrel.ui.theme.SturrelTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileManager.instance.filesDir = this.filesDir

        val levels = listOf("P1", "P2", "P3", "P4", "P5", "P6", "S1", "S2", "S3")

        val rootFolder = VocabFolder(
            name = "Folders",
            subfolders = emptyList(),
            vocab = emptyList()
        )

        for (level in levels) {
            val defaultFolder = FileManager.instance.read(
                stream = this.assets.open("DEFAULT_$level.json"),
                decodeType = DefaultFolder::class.java
            )

            val levelFolder = VocabFolder(
                name = level,
                subfolders = defaultFolder.folders.map { it.id },
                vocab = emptyList()
            )

            for (folder in defaultFolder.folders) {
                folder.subfolders = emptyList()
                FoldersDataManager.instance.saveFolder(folder = folder)
            }
            for (vocab in defaultFolder.vocab) {
                VocabDataManager.instance.saveVocab(vocab = vocab)
            }

            FoldersDataManager.instance.saveFolder(folder = levelFolder)
            rootFolder.subfolders += levelFolder.id
        }

        FoldersDataManager.instance.saveFolder(folder = rootFolder)

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
                                folderId = rootFolder.id,
                                nav = navController
                            )
                        }
                        composable(
                            "f{folderId}",
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
                            "v{vocabId}",
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
                        composable(
                            "q{folderId}",
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
                            val folderId = UUID.fromString(
                                backStackEntry.arguments!!
                                    .getString("folderId")!!
                                    .toString()
                            )
                            val folder = FoldersDataManager.instance.getFolder(folderId)!!
                            QuizSetupView(
                                folder = folder,
                                quiz = Quiz.DRAG_AND_MATCH,
                                nav = navController
                            )
                        }
                        composable(
                            Quiz.DRAG_AND_MATCH.id(),
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
                        ) {
                            DragAndMatchQuiz(manager = QuizManager.current!!)
                        }
                    }
                }
            }
        }
    }
}
