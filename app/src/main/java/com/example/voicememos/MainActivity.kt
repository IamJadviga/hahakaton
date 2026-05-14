package com.example.voicememos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voicememos.dialogs.CreateFolderDialog
import com.example.voicememos.dialogs.EditFolderDialog
import com.example.voicememos.screens.DrawerContent
import com.example.voicememos.screens.MainScreen
import com.example.voicememos.viewmodel.DialogState
import com.example.voicememos.viewmodel.VoiceMemosViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voicememos.data.repository.InMemoryRepository.memos
import com.example.voicememos.dialogs.DeleteConfirmDialog

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        } else {
            Toast.makeText(
                this,
                "Нужно разрешение на микрофон для записи",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val colorScheme = lightColorScheme(
                primary = Color(0xFF1976D2)
            )
            MaterialTheme(colorScheme = colorScheme) {
                VoiceMemosApp()
            }
        }
    }
}

val BlueSelected = Color(0xFF1976D2)
val BlueBackgroundSelected = Color(0xFFE3F2FD)
val GreenPersonal = Color(0xFF4CAF50)
val OrangeWork = Color(0xFFFF9800)
val BlueFAB = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceMemosApp(
    viewModel: VoiceMemosViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val memos by viewModel.memos.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                folders = folders,
                selectedFolderId = uiState.selectedFolderId,
                showFolderActionsIndex = uiState.showFolderActionsIndex,
                onFolderClick = { folder ->
                    viewModel.selectFolder(folder.id)
                    viewModel.setShowFolderActions(null)
                },
                onEditClick = { folder ->
                    viewModel.setDialogState(DialogState.EditFolder(folder.id))
                },
                onDeleteClick = { folder ->
                    viewModel.setDialogState(DialogState.DeleteFolder(folder.id))
                },
                onAddFolder = {
                    viewModel.setDialogState(DialogState.CreateFolder())
                }
            )
        },
        content = {
            MainScreen(
                memos = memos,
                onMenuClick = { scope.launch { drawerState.open() } },
                onRecordClick = {
                    if (viewModel.isRecording()) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording(context)
                    }
                },
                onSearchChange = {  },
                isRecording = viewModel.isRecording(),
                recordingState = recordingState
            )
        }
    )

    when (val dialog = uiState.activeDialog) {
        is DialogState.CreateFolder -> {
            CreateFolderDialog(
                onDismiss = { viewModel.setDialogState(DialogState.None) },
                onConfirm = { name, color ->
                    viewModel.createFolder(name, color)
                }
            )
        }
        is DialogState.EditFolder -> {
            val folder = folders.find { it.id == dialog.folderId }
            if (folder != null) {
                EditFolderDialog(
                    folder = folder,
                    onDismiss = { viewModel.setDialogState(DialogState.None) },
                    onConfirm = { name, color ->
                        viewModel.updateFolder(folder.id, name, color)
                    }
                )
            }
        }
        is DialogState.DeleteFolder -> {
            DeleteConfirmDialog(
                title = "Delete Folder",
                message = "All memos in this folder will be deleted. Continue?",
                onDismiss = { viewModel.setDialogState(DialogState.None) },
                onConfirm = {
                    viewModel.deleteFolder(dialog.folderId)
                }
            )
        }
        DialogState.None -> {}
    }
}