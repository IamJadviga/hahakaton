package com.example.voicememos.data.repository

import androidx.compose.ui.graphics.Color
import com.example.voicememos.data.model.Folder
import com.example.voicememos.data.model.Memo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object InMemoryRepository {

    private var _nextId = 0L

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _memos = MutableStateFlow<List<Memo>>(emptyList())
    val memos: StateFlow<List<Memo>> = _memos.asStateFlow()

    init {
        val initialFolders = listOf(
            Folder(id = ++_nextId, name = "All Memos", color = Color(0xFF1976D2), isFilled = true),
            Folder(id = ++_nextId, name = "Personal", color = Color(0xFF4CAF50)),
            Folder(id = ++_nextId, name = "Work", color = Color(0xFFFF9800))
        )
        _folders.value = initialFolders
    }

    fun addFolder(name: String, color: Color) {
        val currentList = _folders.value.toMutableList()
        currentList.add(Folder(id = ++_nextId, name = name, color = color))
        _folders.value = currentList
    }

    fun updateFolder(id: Long, name: String, color: Color) {
        val currentList = _folders.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(name = name, color = color)
            _folders.value = currentList
        }
    }

    fun deleteFolder(id: Long) {
        val currentList = _folders.value.toMutableList()
        currentList.removeAll { it.id == id }
        _folders.value = currentList
    }

    fun addMemo(memo: Memo) {
        val currentList = _memos.value.toMutableList()
        currentList.add(0, memo)
        _memos.value = currentList
    }

    fun deleteMemo(memoId: Long) {
        val currentList = _memos.value.toMutableList()
        currentList.removeAll { it.id == memoId }
        _memos.value = currentList
    }

    fun updateMemo(memo: Memo) {
        val currentList = _memos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == memo.id }
        if (index != -1) {
            currentList[index] = memo
            _memos.value = currentList
        }
    }
}