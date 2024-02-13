package com.example.langfant.ui.lesson

data class Lesson(
    val name: String,
    val imageResource: Int,
    val description: String,
    val type: String,
    val keywords: List<String>,
    val vocabulary: List<String>,
    val template: String,
    val maxWords: Int
    )
