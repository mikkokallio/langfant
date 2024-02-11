package com.example.langfant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.langfant.R
import org.json.JSONArray
import java.io.InputStream

class ExerciseActivity : AppCompatActivity() {
    private lateinit var exercises: JSONArray
    private var currentIndex = 0
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
            val answer = exercise.getString("answer")

            val textSentence: TextView = findViewById(R.id.textSentence)
            textSentence.text = sentence

            val layout: LinearLayout = findViewById(R.id.wordButtonsLayout)
            layout.removeAllViews() // Clear previous buttons

            val words = answer.split(" ")
            for (word in words) {
                val button = Button(this)
                button.text = word
                layout.addView(button)
            }
        } else {
            // No more exercises, handle end of exercises
            // For now, let's just display a toast message
            Toast.makeText(this, "No more exercises", Toast.LENGTH_SHORT).show()
        }
    }
    /*private fun displayExercise(index: Int) {
        if (index < exercises.length()) {
            val exercise = exercises.getJSONObject(index)
            val prompt = exercise.getString("prompt")
            val sentence = exercise.getString("sentence")

            val textPrompt: TextView = findViewById(R.id.textPrompt)
            val textSentence: TextView = findViewById(R.id.textSentence)

            textPrompt.text = prompt
            textSentence.text = sentence

            // Clear answer EditText
            val editTextAnswer: EditText = findViewById(R.id.editTextAnswer)
            editTextAnswer.text.clear()
        } else {
            // No more exercises, handle end of exercises
            // For now, let's just display a toast message
            Toast.makeText(this, "No more exercises", Toast.LENGTH_SHORT).show()
        }
    }*/
}
