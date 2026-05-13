package com.example.voicememos.viewmodel

import kotlinx.coroutines.flow.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.voicememos.data.repository.InMemoryRepository
import kotlinx.coroutines.flow.StateFlow
import com.example.voicememos.data.RecordingState
import com.example.voicememos.data.SpeechToTextManager
import com.example.voicememos.data.model.Folder
import com.example.voicememos.data.model.Memo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceMemosViewModel : ViewModel() {

    private val repository = InMemoryRepository

    val folders: StateFlow<List<Folder>> = repository.folders
    val memos: StateFlow<List<Memo>> = repository.memos

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private var speechManager: SpeechToTextManager? = null

    fun startRecording(context: android.content.Context) {
        if (speechManager == null) {
            speechManager = SpeechToTextManager(
                context = context,
                onResult = { text ->
                    createMemo(title = "New Memo", content = text)
                },
                onError = { error ->
                    _uiState.update { it.copy(error = error) }
                },
                onStatusChange = { state ->
                    _recordingState.value = state
                }
            )
        }
        speechManager?.startListening()
    }

    fun stopRecording() {
        speechManager?.stopListening()
    }

    fun isRecording(): Boolean = speechManager?.isListening() == true

    fun selectFolder(folderId: Long?) {
        _uiState.update { it.copy(selectedFolderId = folderId) }
    }

    fun setShowFolderActions(index: Int?) {
        _uiState.update { it.copy(showFolderActionsIndex = index) }
    }

    fun setDialogState(dialog: DialogState) {
        _uiState.update { it.copy(activeDialog = dialog) }
    }

    fun createFolder(name: String, color: Color) {
        repository.addFolder(name, color)
        setDialogState(DialogState.None)
    }

    fun updateFolder(id: Long, name: String, color: Color) {
        repository.updateFolder(id, name, color)
        setDialogState(DialogState.None)
    }

    fun deleteFolder(id: Long) {
        repository.deleteFolder(id)
        if (_uiState.value.selectedFolderId == id) {
            selectFolder(null)
        }
        setDialogState(DialogState.None)
    }

    fun createMemo(title: String, content: String) {
        repository.addMemo(
            Memo(
                id = System.currentTimeMillis(),
                title = title,
                content = content,
            )
        )
        _uiState.update { it.copy(error = null) }
    }

    fun deleteMemo(memoId: Long) {
        repository.deleteMemo(memoId)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager?.destroy()
        speechManager = null
    }
}

data class UiState(
    val selectedFolderId: Long? = null,
    val showFolderActionsIndex: Int? = null,
    val activeDialog: DialogState = DialogState.None,
    val error: String? = null
)

sealed class DialogState {
    object None : DialogState()
    data class CreateFolder(val initialName: String = "") : DialogState()
    data class EditFolder(val folderId: Long) : DialogState()
    data class DeleteFolder(val folderId: Long) : DialogState()
}