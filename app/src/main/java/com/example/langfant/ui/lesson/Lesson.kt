package com.example.langfant.ui.lesson

data class Lesson(
    val id: Int,
    val priority: Int,
    val name: String,
    val imageResource: Int,
    val description: String,
    val repetitions: Int,
    val words: List<String>
)
