package com.example.langfant

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.langfant.R
import com.google.android.flexbox.FlexboxLayout
import org.json.JSONArray
import java.io.InputStream

class ExerciseActivity : AppCompatActivity() {
    private lateinit var exercises: JSONArray
    private var currentIndex = 0
    private var selectedWords = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // Read JSON file
        val json = resources.openRawResource(R.raw.exercises).bufferedReader().use { it.readText() }
        exercises = JSONArray(json)

        // Display initial exercise
        displayExercise(currentIndex)

        // Find the submit button
        val submitButton: Button = findViewById(R.id.buttonSubmit)

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener {
            // Handle button click here
            // For now, let's just display the next exercise
            currentIndex++
            displayExercise(currentIndex)
        }
    }

    private fun displayExercise(index: Int) {
        if (index < exercises.length()) {
            val exercise = exercises.getJSONObject(index)
            val sentence = exercise.getString("sentence")
            val answer = exercise.getString("answer").replace("[^\\p{L}\\s]".toRegex(), "")

            val textSentence: TextView = findViewById(R.id.textSentence)
            textSentence.text = sentence

            val layout: FlexboxLayout = findViewById(R.id.wordButtonsLayout)
            layout.removeAllViews() // Clear previous buttons

            // Clear the list of selected words for each new exercise
            selectedWords.clear()

            val words = answer.split(" ")
            for (word in words) {
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
            // For now, let's just display a toast message
            Toast.makeText(this, "No more exercises", Toast.LENGTH_SHORT).show()
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
}
