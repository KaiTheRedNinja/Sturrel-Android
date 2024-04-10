package com.kaithebuilder.sturrel.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.base.pinYin.PinYin
import com.kaithebuilder.sturrel.base.sturrelTypes.Vocab
import com.kaithebuilder.sturrel.base.sturrelTypes.VocabFolder
import com.kaithebuilder.sturrel.model.sturrelQuiz.Question
import com.kaithebuilder.sturrel.model.sturrelQuiz.Quiz
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.ui.components.ListItem
import com.kaithebuilder.sturrel.ui.components.NavList
import java.util.UUID

class QuizSetupManager(
    var folder: VocabFolder,
    var questionType: QAType = QAType.HANZI,
    var answerType: QAType = QAType.PINYIN,
    var randomised: Boolean = true
): ViewModel() {
    fun includedVocab(): List<UUID> {
        // TODO: get this to actually return all the vocab
        return emptyList<UUID>()
    }

    fun produceQuestions(): List<Question> {
        return includedVocab().map { vocabId ->
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

    companion object {
        val allCases: Array<QAType> = arrayOf(QAType.HANZI, QAType.PINYIN, DEFINITION)
    }
}

@Composable
fun QuizSetupView(
    folder: VocabFolder,
    quiz: Quiz,
    nav: NavHostController,
    setupManager: QuizSetupManager = QuizSetupManager(folder)
) {
    QuizSetupView(
        setupManager = setupManager,
        nav = nav,
        onUpdateQuestionType = { setupManager.questionType = it },
        onUpdateAnswerType = { setupManager.answerType = it },
        onUpdateRandomised = { setupManager.randomised = it }
    )
}

@Composable
private fun QuizSetupView(
    nav: NavHostController,
    setupManager: QuizSetupManager,
    onUpdateQuestionType: (QAType) -> Unit,
    onUpdateAnswerType: (QAType) -> Unit,
    onUpdateRandomised: (Boolean) -> Unit,
) {
    var questionType by remember { mutableStateOf(setupManager.questionType) }
    var answerType by remember { mutableStateOf(setupManager.answerType) }
    var randomised by remember { mutableStateOf(setupManager.randomised) }

    var questionExpanded by remember { mutableStateOf(false) }
    var answerExpanded by remember { mutableStateOf(false) }

    NavList(title = "Quiz Setup", nav = nav) {
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

        val questions = setupManager.produceQuestions()
        val qnCount = questions.count()
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
                        text = item.answer
                    )
                }
            }
        }
    }
}
