package com.example.voicememos.data.model

import androidx.compose.ui.graphics.Color

data class Folder(
    val id: Long,
    val name: String,
    val color: Color,
    val isFilled: Boolean = false
)