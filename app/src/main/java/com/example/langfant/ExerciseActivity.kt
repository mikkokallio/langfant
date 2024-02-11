package com.example.langfant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.langfant.R

class ExerciseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // Find the submit button
        val submitButton: Button = findViewById(R.id.buttonSubmit)

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener {
            // Handle button click here
            handleSubmission()
        }
    }

    private fun handleSubmission() {
        // Add your logic to handle the submission here
        // For example, you can retrieve the user's answer from the EditText
        val userAnswer = findViewById<EditText>(R.id.editTextAnswer).text.toString()

        // Do something with the user's answer, such as validating it or processing it
        // For now, let's just display a toast message with the user's answer
        Toast.makeText(this, "Submitted answer: $userAnswer", Toast.LENGTH_SHORT).show()
    }
}
