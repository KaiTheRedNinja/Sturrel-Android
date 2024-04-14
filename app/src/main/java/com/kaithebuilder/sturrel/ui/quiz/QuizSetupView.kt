package com.kaithebuilder.sturrel.ui.quiz

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.base.pinYin.PinYin
import com.kaithebuilder.sturrel.base.sturrelTypes.Vocab
import com.kaithebuilder.sturrel.base.sturrelTypes.VocabFolder
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.Quiz
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizManager
import com.kaithebuilder.sturrel.model.sturrelQuiz.QuizStat
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.ui.components.ListItem
import com.kaithebuilder.sturrel.ui.components.ListSectionHeader
import com.kaithebuilder.sturrel.ui.components.NavList
import com.leinardi.android.speeddial.compose.FabWithLabel
import com.leinardi.android.speeddial.compose.SpeedDial
import com.leinardi.android.speeddial.compose.SpeedDialOverlay
import com.leinardi.android.speeddial.compose.SpeedDialState
import java.util.UUID

class QuizSetupManager(
    private var folder: VocabFolder,
    var questionType: QAType = QAType.HANZI,
    var answerType: QAType = QAType.PINYIN,
    var randomised: Boolean = true
): ViewModel() {
    private var includedVocab = internalIncludedVocab()

    private fun internalIncludedVocab(): List<UUID> {
        val hierarchy: MutableList<Pair<VocabFolder, Int>> = mutableListOf(Pair(folder, -1))
        val vocab: MutableList<UUID> = folder.vocab.toMutableList()
        while (hierarchy.isNotEmpty()) {
            val curFolder = hierarchy.last().first
            // mark previous child as explored
            hierarchy[hierarchy.count()-1] = Pair(curFolder, hierarchy.last().second+1)

            // explore the next child
            if (curFolder.subfolders.count() > hierarchy.last().second) {
                val nextId = curFolder.subfolders[hierarchy.last().second]
                val nextFolder = FoldersDataManager.instance.getFolder(nextId)!!
                // add its children
                vocab += nextFolder.vocab
                // explore it
                hierarchy += Pair(nextFolder, -1)
            } else {
                // no more children to explore, remove this from the hierarchy
                hierarchy.removeLast()
            }
        }
        return vocab
    }

    fun produceQuestions(): List<Question> {
        val ordered = if (randomised) {
            includedVocab.shuffled()
        } else {
            includedVocab
        }

        return ordered.map { vocabId ->
            val vocab = VocabDataManager.instance.getVocab(vocabId)!!
            Question(
                associatedVocab = vocab,
                question = questionType.forVocab(vocab),
                answer = answerType.forVocab(vocab)
            )
        }
    }
}

enum class QAType {
    HANZI, PINYIN, DEFINITION;

    fun forVocab(vocab: Vocab): String {
        return when (this) {
            HANZI -> vocab.word
            PINYIN -> PinYin.instance.getPinyinString(vocab.word)
            DEFINITION -> vocab.englishDefinition
        }
    }

    fun description(): String {
        return when (this) {
            HANZI -> "Han Zi"
            PINYIN -> "Pin Yin"
            DEFINITION -> "Definition"
        }
    }

    fun deconflict(): QAType {
        return when (this) {
            HANZI -> PINYIN
            PINYIN -> DEFINITION
            DEFINITION -> HANZI
        }
    }

    companion object {
        val allCases: Array<QAType> = arrayOf(QAType.HANZI, QAType.PINYIN, DEFINITION)
    }
}

@Composable
fun QuizSetupView(
    folder: VocabFolder,
    nav: NavHostController,
    setupManager: QuizSetupManager = QuizSetupManager(folder)
) {
    var questions by remember {
        mutableStateOf(setupManager.produceQuestions())
    }

    QuizSetupView(
        setupManager = setupManager,
        nav = nav,
        questions = questions,
        onUpdateQuestionType = {
            setupManager.questionType = it
            questions = setupManager.produceQuestions()
        },
        onUpdateAnswerType = {
            setupManager.answerType = it
            questions = setupManager.produceQuestions()
        },
        onUpdateRandomised = {
            setupManager.randomised = it
            questions = setupManager.produceQuestions()
        },
        onQuizFinish = { qns, quizType ->
            QuizManager.current = QuizManager(
                statsToShow = setOf(QuizStat.REMAINING, QuizStat.CORRECT, QuizStat.WRONG),
                questions = qns
            )
            nav.navigate(quizType.id())
        }
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun QuizSetupView(
    nav: NavHostController,
    setupManager: QuizSetupManager,
    questions: List<Question>,
    onUpdateQuestionType: (QAType) -> Unit,
    onUpdateAnswerType: (QAType) -> Unit,
    onUpdateRandomised: (Boolean) -> Unit,
    onQuizFinish: (List<Question>, Quiz) -> Unit
) {
    var questionType by remember { mutableStateOf(setupManager.questionType) }
    var answerType by remember { mutableStateOf(setupManager.answerType) }
    var randomised by remember { mutableStateOf(setupManager.randomised) }

    var questionExpanded by remember { mutableStateOf(false) }
    var answerExpanded by remember { mutableStateOf(false) }

    var speedDialState by remember { mutableStateOf(SpeedDialState.Collapsed) }
    var overlayVisible: Boolean by remember { mutableStateOf(speedDialState.isExpanded()) }

    NavList(title = "Quiz Setup", nav = nav, floatingActionButton = {
        val dialColor = if (questions.isEmpty()) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }

        SpeedDial(
            state = speedDialState,
            onFabClick = { expanded ->
                if (questions.isNotEmpty()) {
                    overlayVisible = !expanded
                    speedDialState = if (expanded) {
                        SpeedDialState.Collapsed
                    } else {
                        SpeedDialState.Expanded
                    }
                }
            },
            fabClosedContent = {
                Icon(Icons.Outlined.PlayArrow, contentDescription = "Play")
            },
            fabOpenedContent = {
                Icon(Icons.Outlined.PlayArrow, contentDescription = "Play")
            },
            fabClosedBackgroundColor = dialColor,
            fabOpenedBackgroundColor = dialColor
        ) {
            for (quizType in Quiz.allCases) {
                item {
                    FabWithLabel(
                        onClick = {
                            Log.d("PlayButton", "Clicked item: ${quizType.description()}")
                            onQuizFinish(setupManager.produceQuestions(), quizType)
                        },
                        labelContent = { Text(text = quizType.description()) },
                        fabBackgroundColor = when (quizType) {
                            Quiz.DRAG_AND_MATCH -> MaterialTheme.colorScheme.tertiaryContainer
                            Quiz.MEMORY_CARDS -> MaterialTheme.colorScheme.surfaceTint
                            Quiz.QNA -> MaterialTheme.colorScheme.surfaceVariant
                            Quiz.FLASH_CARDS -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Icon(quizType.icon(), null)
                    }
                }
            }
        }
    }, overlay = {
        SpeedDialOverlay(
            visible = overlayVisible,
            onClick = {
                overlayVisible = false
                speedDialState = speedDialState.toggle()
            }
        )
    }) {
        item {
            ListItem(index = 0, totalSize = 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text("Question")

                        Row(
                            modifier = Modifier
                                .clickable { questionExpanded = true }
                        ) {
                            Text(
                                questionType.description(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Answer Type",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        DropdownMenu(
                            expanded = questionExpanded,
                            onDismissRequest = { questionExpanded = false }
                        ) {
                            for (case in QAType.allCases) {
                                DropdownMenuItem(text = {
                                    Text(case.description())
                                }, onClick = {
                                    questionType = case
                                    if (answerType == case) {
                                        answerType = case.deconflict()
                                        onUpdateAnswerType(case.deconflict())
                                    }
                                    onUpdateQuestionType(case)
                                })
                            }
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text("Answer")

                        Row(
                            modifier = Modifier
                                .clickable { answerExpanded = true }
                        ) {
                            Text(
                                answerType.description(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Answer Type",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        DropdownMenu(
                            expanded = answerExpanded,
                            onDismissRequest = { answerExpanded = false }
                        ) {
                            for (case in QAType.allCases) {
                                DropdownMenuItem(text = {
                                    Text(case.description())
                                }, onClick = {
                                    answerType = case
                                    if (questionType == case) {
                                        questionType = case.deconflict()
                                        onUpdateQuestionType(case.deconflict())
                                    }
                                    onUpdateAnswerType(case)
                                })
                            }
                        }
                    }
                }
            }

            ListItem(index = 1, totalSize = 2) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "Randomise",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = randomised,
                        onCheckedChange = {
                            randomised = it
                            onUpdateRandomised(it)
                        }
                    )
                }
            }
        }

        if (questionType != answerType) {
            val qnCount = questions.count()
            item {
                ListSectionHeader(header = "$qnCount Words")
            }
            if (qnCount == 0) {
                item {
                    ListItem(index = 0, totalSize = 1) {
                        Text(
                            "You need at least one word to play",
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }
            itemsIndexed(questions) { index, item ->
                ListItem(index = index, totalSize = qnCount) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            text = item.question,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.answer,
                            modifier = Modifier.fillMaxWidth(0.65f),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
