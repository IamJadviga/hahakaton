package com.example.voicememos.screens

import DrawerItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicememos.data.model.Folder

@Composable
fun DrawerContent(
    folders: List<Folder>,
    selectedFolderId: Long?,
    showFolderActionsIndex: Int?,
    onFolderClick: (Folder) -> Unit,
    onEditClick: (Folder) -> Unit,
    onDeleteClick: (Folder) -> Unit,
    onAddFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.White)
            .width(300.dp)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Folders",
            fontSize = 18.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        folders.forEachIndexed { index, folder ->
            DrawerItem(
                icon = if (folder.isFilled) Icons.Default.Email else Icons.Outlined.Email,
                text = folder.name,
                isSelected = selectedFolderId == folder.id,
                tint = folder.color,
                showActions = showFolderActionsIndex == index,
                onClick = { onFolderClick(folder) },
                onEditClick = { onEditClick(folder) },
                onDeleteClick = { onDeleteClick(folder) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        DrawerItem(
            icon = Icons.Default.Add,
            text = "New Folder",
            isSelected = false,
            tint = Color(0xFF757575),
            showActions = false,
            onClick = onAddFolder,
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}