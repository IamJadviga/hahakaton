package com.example.voicememos.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicememos.BlueFAB
import com.example.voicememos.components.EmptyStateContent
import com.example.voicememos.components.SearchBar
import com.example.voicememos.data.RecordingState
import com.example.voicememos.data.model.Memo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    memos: List<Memo>,
    onMenuClick: () -> Unit,
    onRecordClick: () -> Unit,
    onSearchChange: (String) -> Unit = {},
    isRecording: Boolean = false,
    recordingState: RecordingState = RecordingState.Idle,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                navigationIcon = {
                    Box(modifier = Modifier.width(48.dp)) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                },
                title = {
                    Text(
                        text = "Voice Memos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                actions = {
                    Box(modifier = Modifier.width(48.dp))
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(modifier = Modifier.padding(bottom = 24.dp)) {
                    RecordFab(
                        isRecording = isRecording,
                        recordingState = recordingState,
                        onClick = onRecordClick
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    onSearchChange(it)
                }
            )
            Spacer(modifier = Modifier.weight(1f))

            if (memos.isEmpty()) {
                EmptyStateContent()
            } else {
                MemoList(memos = memos)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun RecordFab(
    isRecording: Boolean,
    recordingState: RecordingState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isRecording -> Color(0xFFE53935)
        else -> Color(0xFF2196F3)
    }

    FloatingActionButton(
        onClick = onClick,
        containerColor = backgroundColor,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = modifier
            .size(56.dp)
    ) {
        if (isRecording) {

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.White, CircleShape)
                    .then(
                        if (recordingState == RecordingState.Listening) {
                            Modifier.animateContentSize()
                        } else {
                            Modifier
                        }
                    )
            )
        } else {
            Icon(
                Icons.Default.Phone,
                contentDescription = "Start Recording",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MemoList(
    memos: List<Memo>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(memos) { memo ->
            MemoItem(memo = memo)
        }
    }
}

@Composable
fun MemoItem(
    memo: Memo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = memo.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = memo.content,
                color = Color(0xFF757575),
                fontSize = 14.sp
            )
        }
    }
}