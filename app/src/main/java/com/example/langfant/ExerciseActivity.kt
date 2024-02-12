package com.example.langfant

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.langfant.R
import com.google.android.flexbox.FlexboxLayout
import org.json.JSONArray
import java.io.InputStream

class ExerciseActivity : AppCompatActivity() {
    private lateinit var exercises: JSONArray
    //private lateinit var lessonWords: List<String>
    private var currentIndex = 0
    private var selectedWords = mutableListOf<String>()
    private var englishToCroatian = true
    private lateinit var progressBar: ProgressBar
    private lateinit var keywords: MutableList<String>
    private lateinit var vocabulary: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // Initialize progress bar
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Retrieve keywords and vocabulary from intent
        val keywordsArray = intent.getStringArrayExtra("keywords") ?: arrayOf()
        keywords = ArrayList<String>(keywordsArray.toList())
        val vocabularyArray = intent.getStringArrayExtra("vocabulary") ?: arrayOf()
        vocabulary = ArrayList<String>(vocabularyArray.toList())

        // Read JSON file
        val json = resources.openRawResource(R.raw.exercises).bufferedReader().use { it.readText() }
        exercises = JSONArray(json)

        // Filter exercises based on the presence of lesson words
        val filteredExercises = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { exercise ->
                val answer = exercise.getString("Croatian").replace("[^\\p{L}\\s']".toRegex(), "")
                val answerWords = answer.split(" ").map { it.trim() }
                answerWords.any { word -> keywords.contains(word) }
            }

        // Shuffle the filteredExercises and take the first 15 exercises
        val shuffledExercises = filteredExercises.shuffled()
        val limitedExercises = shuffledExercises.take(15)

        // Convert filtered exercises to JSONArray
        exercises = JSONArray(limitedExercises.toString())
        progressBar.max = exercises.length()
        progressBar.progress = 0
        println(exercises)

        // Display initial exercise
        displayExercise(currentIndex)

        // Find the submit button
        val submitButton: Button = findViewById(R.id.buttonSubmit)

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener {
            // Handle button click here
            // For now, let's just display the next exercise
            checkAnswer()
            currentIndex++
            progressBar.progress = currentIndex
            englishToCroatian = !englishToCroatian
            displayExercise(currentIndex)
        }
    }

    private fun displayExercise(index: Int) {
        if (index < exercises.length()) {
            val exercise = exercises.getJSONObject(index)
            val sentence = exercise.getString(if (englishToCroatian) "English" else "Croatian")
            val answer = exercise.getString(if (englishToCroatian) "Croatian" else "English").replace("[^\\p{L}\\s']".toRegex(), "")

            val textSentence: TextView = findViewById(R.id.textSentence)
            textSentence.text = sentence

            val layout: FlexboxLayout = findViewById(R.id.wordButtonsLayout)
            layout.removeAllViews() // Clear previous buttons

            // Clear the list of selected words for each new exercise
            selectedWords.clear()
            updateSelectedWordsTextView()

            val words = answer.split(" ").toMutableList()

            // Select a random exercise from the filtered list
            val randomExercise = exercises.getJSONObject((0 until exercises.length()).random())
            val randomExerciseAnswer = randomExercise.getString((if (englishToCroatian) "Croatian" else "English")).replace("[^\\p{L}\\s']".toRegex(), "")
            val randomExerciseWords = randomExerciseAnswer.split(" ").map { it.trim() }
            // Get additional words from the random exercise, excluding words from the current exercise's answer
            val additionalWords = randomExerciseWords.filter { !words.contains(it) }
            // Add additional words to the shuffledWords list
            words.addAll(additionalWords)
            // Shuffle the list of words randomly
            val shuffledWords = words.shuffled()

            for (word in shuffledWords) {
                val button = Button(this)
                button.text = word
                button.setOnClickListener {
                    toggleWordSelection(button, word)
                    updateSelectedWordsTextView()
                }
                layout.addView(button)
            }
        } else {
            // No more exercises, handle end of exercises
            Toast.makeText(this, "No more exercises", Toast.LENGTH_SHORT).show()

            // Navigate back to the lessons view
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toggleWordSelection(button: Button, word: String) {
        // Toggle word selection by changing its background color
        if (selectedWords.contains(word)) {
            // Word is already selected, remove it
            selectedWords.remove(word)
            button.setTextColor(Color.BLACK)
        } else {
            // Word is not selected, add it
            selectedWords.add(word)
            button.setTextColor(Color.YELLOW)
        }
    }

    private fun updateSelectedWordsTextView() {
        // Display the selected words above the buttons
        val selectedWordsText = selectedWords.joinToString(" ")
        val selectedWordsTextView: TextView = findViewById(R.id.selectedWordsTextView)
        selectedWordsTextView.text = selectedWordsText
    }

    private fun checkAnswer() {
        val exercise = exercises.getJSONObject(currentIndex)
        val answer = exercise.getString(if (englishToCroatian) "Croatian" else "English").replace("[^\\p{L}\\s']".toRegex(), "")

        // Concatenate selected words into a single sentence
        val selectedSentence = selectedWords.joinToString(" ")

        // Convert both the answer and selected sentence to lowercase for case-insensitive comparison
        if (answer.lowercase() == selectedSentence.lowercase()) {
            // Sentences match, handle correct answer
            Toast.makeText(this, Html.fromHtml("<big>Correct!</big>"), Toast.LENGTH_SHORT).show()
        } else {
            // Sentences don't match, handle incorrect answer
            Toast.makeText(this, Html.fromHtml("<big>Incorrect. The correct answer is:<br/>$answer</big>"), Toast.LENGTH_LONG).show()
        }
    }
}