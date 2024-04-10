package com.kaithebuilder.sturrel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kaithebuilder.sturrel.model.pinYin.PinYin
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import com.kaithebuilder.sturrel.model.sturrelVocab.VocabDataManager
import com.kaithebuilder.sturrel.sturrelTypes.Vocab
import java.util.UUID

@Composable
fun VocabDetailView(
    vocabId: UUID,
    nav: NavHostController
) {
    val vocab = VocabDataManager.instance.getVocab(vocabId = vocabId)!!

    VocabDetailViewContents(vocab = vocab, nav = nav)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VocabDetailViewContents(
    vocab: Vocab,
    nav: NavHostController
) {
    NavList(title = "Vocab", nav = nav) {
        item {
            if (vocab.isHCL) {
                Spacer(modifier = Modifier
                    .padding(top = 50.dp))
                ListSectionHeader(header = "Higher Chinese")
            }
            WordSection(word = vocab.word)
        }

        if (vocab.englishDefinition.isNotEmpty() || vocab.definition.isNotEmpty()) {
            item {
                ListSectionHeader(header = "Definition")
                DefinitionSection(
                    english = vocab.englishDefinition,
                    chinese = vocab.definition
                )
            }
        }

        if (vocab.sentences.isNotEmpty()) {
            item {
                ListSectionHeader(header = "Sentences")
            }
            itemsIndexed(vocab.sentences) { index, sentence ->
                ListItem(index = index, totalSize = vocab.sentences.count()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            text = sentence,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (vocab.wordBuilding.isNotEmpty()) {
            item {
                ListSectionHeader(header = "Words")
            }
            itemsIndexed(vocab.wordBuilding) { index, word ->
                ListItem(index = index, totalSize = vocab.wordBuilding.count()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            text = word,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordSection(word: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = word,
            style = TextStyle(
                fontSize = 36.sp, // Adjust the font size as needed
                color = Color.Black, // Customize the color
                fontWeight = FontWeight.Bold
                // You can customize other properties such as font family, letter spacing, etc. here
            ),
            modifier = Modifier
                .padding(vertical = 16.dp) // Adjust the padding as needed
        )
        Text(
            text = PinYin.instance.getPinyinString(word)
        )
    }
}

@Composable
private fun DefinitionSection(english: String, chinese: String) {
    val count = english.isNotEmpty().compareTo(false) + chinese.isNotEmpty().compareTo(false)
    if (english.isNotEmpty()) {
        ListItem(index = 0, totalSize = count) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(
                    text = english,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    if (chinese.isNotEmpty()) {
        ListItem(index = if (english.isEmpty()) 0 else 1, totalSize = count) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(
                    text = chinese,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun VocabDetailViewPreviews() {
    VocabDetailViewContents(
        vocab = Vocab(
            word = "世界",
            isHCL = true,
            englishDefinition = "World; Earth",
            definition = "地球",
            sentences = listOf("你好世界", "世界很大"),
            wordBuilding = listOf("我不知道", "词语")
        ),
        nav = rememberNavController()
    )
}