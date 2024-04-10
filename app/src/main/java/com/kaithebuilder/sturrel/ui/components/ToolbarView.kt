package com.kaithebuilder.sturrel.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaithebuilder.sturrel.model.sturrelVocab.FoldersDataManager
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarView(
    title: String,
    nav: NavHostController,
    topBar: @Composable() () -> Unit = {},
    actions: @Composable() RowScope.() -> Unit = {},
    content: @Composable() (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        if (nav.currentDestination?.route != "root") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    nav.popBackStack()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                                if (nav.previousBackStackEntry?.destination?.route == "root") {
                                    Text("Folders")
                                } else {
                                    val name = nav.previousBackStackEntry?.arguments?.getString("folderId")
                                    if (name != null) {
                                        Text(
                                            text = FoldersDataManager.instance.getFolder(
                                                UUID.fromString(name)
                                            )!!.name,
                                            modifier = Modifier
                                                .width(80.dp),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        actions()
                    }
                )
                topBar()
            }
        }
    ) { padding ->
        content(padding)
    }
}