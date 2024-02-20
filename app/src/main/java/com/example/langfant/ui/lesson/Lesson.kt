package com.example.langfant.ui.lesson

data class Lesson(
    val id: Int,
    val name: String,
    val imageResource: Int,
    val description: String,
    val type: String,
    val vocabulary: List<String>,
    val template: String,
    val maxWords: Int
    )
