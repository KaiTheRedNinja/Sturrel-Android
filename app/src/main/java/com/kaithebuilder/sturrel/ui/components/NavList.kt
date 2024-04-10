package com.kaithebuilder.sturrel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun NavList(title: String, nav: NavHostController, view: LazyListScope.() -> Unit) {
    ToolbarView(title = title, nav = nav) {
        Column {
            Spacer(
                modifier = Modifier
                    .padding(top = 50.dp)
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                    .padding(horizontal = 10.dp),
            ) {
                view()
            }
        }
    }
}

@Composable
fun ListSectionHeader(header: String) {
    Box(
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(5.dp)
            ),
    ) {
        Text(
            text = header,
            modifier = Modifier
                .padding(horizontal = 5.dp),
            color = Color.Gray
        )
    }
}

@Composable
fun ListItem(
    index: Int,
    totalSize: Int,
    view: @Composable () -> Unit
) {
    var cardMod = Modifier
        .padding(bottom = if (index == totalSize - 1) 5.dp else 0.dp)
        .fillMaxWidth()
    val cornerRadius = 10.dp

    cardMod = if (totalSize == 1) {
        cardMod.padding(vertical = 5.dp)
    } else when (index) {
        0 -> cardMod.padding(top = 5.dp)
        totalSize - 1 -> cardMod.padding(bottom = 10.dp)
        else -> cardMod.padding(vertical = 0.dp)
    }

    ElevatedCard(
        modifier = cardMod,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = when (totalSize) {
            1 -> RoundedCornerShape(cornerRadius)
            else -> when (index) {
                0 -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
                totalSize - 1 -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
                else -> RoundedCornerShape(0)
            }
        }
    ) {
        view()
        if (index+1 != totalSize) {
            Divider(
                color = Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}