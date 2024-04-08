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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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

@Composable
private fun VocabDetailViewContents(
    vocab: Vocab,
    nav: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        // on below line we are specifying horizontal alignment
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = vocab.word,
            style = TextStyle(
                color = Color.Black,
                fontSize = TextUnit(value = 30.0F, type = TextUnitType.Sp)
            ),
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 10.dp)
        )
        Divider(
            modifier = Modifier.offset(y = 10.dp)
        )

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Definition",
                        modifier = Modifier.padding(start = 15.dp, top = 30.dp),
                        color = Color.Gray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            text = vocab.englishDefinition,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Divider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            text = vocab.definition,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Divider()
                }
            }

            item {
                Text(
                    text = "Sentences",
                    modifier = Modifier.padding(start = 15.dp, top = 30.dp),
                    color = Color.Gray
                )
            }
            items(vocab.sentences) { sentence ->
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
                Divider()
            }

            item {
                Text(
                    text = "Words",
                    modifier = Modifier.padding(start = 15.dp, top = 30.dp),
                    color = Color.Gray
                )
            }
            items(vocab.wordBuilding) { word ->
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
                Divider()
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
            isHCL = false,
            englishDefinition = "World; Earth",
            definition = "地球",
            sentences = listOf("你好世界", "世界很大"),
            wordBuilding = listOf("我不知道", "词语")
        ),
        nav = rememberNavController()
    )
}