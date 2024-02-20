package com.example.langfant

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.langfant.R
import com.google.android.flexbox.FlexboxLayout
import org.json.JSONArray
import org.json.JSONObject

import java.io.InputStream

abstract class ExerciseActivity : AppCompatActivity() {
    lateinit var exercises: JSONArray
    //private lateinit var lessonWords: List<String>
    var currentIndex = 0
    lateinit var progressBar: ProgressBar
    private lateinit var vocabulary: MutableList<String>
    var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // Initialize progress bar
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Retrieve extras from intent
        id = intent.getIntExtra("id", 0)
        val vocabularyArray = intent.getStringArrayExtra("vocabulary") ?: arrayOf()
        vocabulary = ArrayList<String>(vocabularyArray.toList())
        val maxWords = intent.getIntExtra("maxWords", 0)
        val template = intent.getStringExtra("template") ?: ".*"
        val type = intent.getStringExtra("type")

        // Hide all exercise specific elements
        findViewById<View>(R.id.translation_content).visibility = View.VISIBLE
        findViewById<View>(R.id.wordmatch_content).visibility = View.GONE

        // Load the appropriate layout based on the lesson's type
        if (type == "translation") {
            findViewById<View>(R.id.translation_content).visibility = View.VISIBLE
        } else if (type == "word-match") {
            findViewById<View>(R.id.wordmatch_content).visibility = View.VISIBLE
        }

        exercises = getExercises(maxWords, template, vocabulary as ArrayList<String>)

        progressBar.max = exercises.length()
        progressBar.progress = 0
        println(exercises)

        // Display initial exercise
        displayExercise(currentIndex)

        // Find the submit button
        val submitButton: Button = findViewById(R.id.buttonSubmit)
        setOnClickSubmit(submitButton)
    }
    fun saveCompletedLessonId() {
        val sharedPreferences = getSharedPreferences("completed_lessons", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val completedLessonSet = sharedPreferences.getStringSet("completed_lessons", HashSet<String>()) ?: HashSet<String>()
        completedLessonSet.add(id.toString())
        editor.putStringSet("completed_lessons", completedLessonSet)
        editor.apply()
    }

    abstract fun setOnClickSubmit(submitButton: Button)
    abstract fun getExercises(maxWords: Int, template: String, vocabularyList: ArrayList<String>): JSONArray
    abstract fun displayExercise(index: Int)
}

class TranslationExercise : ExerciseActivity() {
    private var selectedWords = mutableListOf<String>()
    private var englishToCroatian = true

    override fun setOnClickSubmit(submitButton: Button) {
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
    override fun getExercises(maxWords: Int, template: String, vocabularyList: ArrayList<String>): JSONArray {
        // Read JSON file
        val json = resources.openRawResource(R.raw.exercises).bufferedReader().use { it.readText() }
        exercises = JSONArray(json)

        // Filter exercises based constraints
        val filteredExercises = filterExercises(exercises, maxWords, template)

        // Shuffle the filteredExercises and take the first 15 exercises
        val shuffledExercises = filteredExercises.shuffled()
        val limitedExercises = shuffledExercises.take(15)

        // Convert filtered exercises to JSONArray
        return JSONArray(limitedExercises.toString())
    }
    private fun filterExercises(exercises: JSONArray, maxWords: Int, template: String): List<JSONObject> {
        return (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { exercise ->
                val answer = exercise.getString("Croatian")
                val answerWords = answer
                    .replace("[^\\p{L}\\s']".toRegex(), "")
                    .split(" ").map { it.trim() }
                val withinMaxWordLimit = answerWords.size <= maxWords
                val matchesTemplate = template.toRegex().matches(answer)
                withinMaxWordLimit && matchesTemplate
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

    override fun displayExercise(index: Int) {
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
            saveCompletedLessonId()

            // Navigate back to the lessons view
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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
            // Flash and grow animation for the progress bar
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            val animation = AnimationUtils.loadAnimation(this, R.anim.progress_bar_animation)
            progressBar.startAnimation(animation)
        } else {
            // Sentences don't match, handle incorrect answer
            Toast.makeText(this, Html.fromHtml("<big>Incorrect. The correct answer is:<br/>$answer</big>"), Toast.LENGTH_LONG).show()
        }
    }
}

class WordMatchExercise : ExerciseActivity() {
    private var currentlySelected = -1
    private var currentButton: Button? = null
    private lateinit var filteredVocabulary: JSONArray
    override fun setOnClickSubmit(submitButton: Button) {
        // Set OnClickListener for the submit button
        submitButton.setOnClickListener {
            // Handle button click here
            // For now, let's just display the next exercise
            //checkAnswer()
            currentIndex++
            progressBar.progress = currentIndex
            displayExercise(currentIndex)
        }
    }

    override fun getExercises(maxWords: Int, template: String, vocabularyList: ArrayList<String>): JSONArray {
        val json = resources.openRawResource(R.raw.vocabulary).bufferedReader().use { it.readText() }
        val vocabulary = JSONArray(json)

        // Filter vocabulary array to include only words in the vocabulary list
        filteredVocabulary = JSONArray()
        for (i in 0 until vocabulary.length()) {
            val word = vocabulary.getJSONObject(i).getString("Croatian")
            if (vocabularyList.contains(word)) {
                filteredVocabulary.put(vocabulary.getJSONObject(i))
            }
        }

        val exercises = JSONArray()

        for (i in 1..5) {
            val exercise = JSONObject()

            val words = mutableListOf<String>()
            val translations = mutableListOf<String>()

            // Select 5 random pairs of words and translations
            for (j in 1..5) {
                val index = (0 until filteredVocabulary.length()).random()
                val word = filteredVocabulary.getJSONObject(index).getString("Croatian")
                val translation = filteredVocabulary.getJSONObject(index).getString("English")

                words.add(word)
                translations.add(translation)
            }

            translations.shuffle()

            exercise.put("words", JSONArray(words))
            exercise.put("translations", JSONArray(translations))

            exercises.put(exercise)
        }

        return exercises
    }

    override fun displayExercise(index: Int) {
        if (index < exercises.length()) {
            val exercise = exercises.getJSONObject(index)
            val wordsArray = exercise.getJSONArray("words")
            val translationsArray = exercise.getJSONArray("translations")

            val words = mutableListOf<String>()
            for (i in 0 until wordsArray.length()) {
                words.add(wordsArray.getString(i))
            }
            val translations = mutableListOf<String>()
            for (i in 0 until translationsArray.length()) {
                translations.add(translationsArray.getString(i))
            }
            val layout: GridLayout = findViewById(R.id.wordMatchLayout)
            layout.removeAllViews()

            for (i in words.indices) {
                val buttonCroatian = Button(this)
                buttonCroatian.text = words[i]
                buttonCroatian.setOnClickListener {
                    clickButton(i, buttonCroatian)
                }
                layout.addView(buttonCroatian)

                val buttonEnglish = Button(this)
                buttonEnglish.text = translations[i]
                buttonEnglish.setOnClickListener {
                    clickButton(i+5, buttonEnglish)
                }
                layout.addView(buttonEnglish)
            }
        } else {
            // No more exercises, handle end of exercises
            Toast.makeText(this, "No more exercises", Toast.LENGTH_SHORT).show()
            saveCompletedLessonId()

            // Navigate back to the lessons view
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun clickButton(index: Int, button: Button) {
        if (currentlySelected == -1) {
            currentlySelected = index
            currentButton = button
            button.setTextColor(Color.YELLOW)
        } else if (index == currentlySelected) {
            currentlySelected = -1
            currentButton = null
            button.setTextColor(Color.BLACK)
        } else if (index <= 4 && currentlySelected <= 4 || index > 4 && currentlySelected > 4) {
            currentButton?.setTextColor(Color.BLACK)
            button.setTextColor(Color.YELLOW)
            currentButton = button
            currentlySelected = index
        } else {
            val vocabularyList = (0 until filteredVocabulary.length()).map { filteredVocabulary.getJSONObject(it) }
            val isFirstCroatian = currentlySelected < 5
            val matchingPair = if (isFirstCroatian) {
                vocabularyList.find { it.getString("Croatian") == currentButton?.text && it.getString("English") == button.text }
            } else {
                vocabularyList.find { it.getString("English") == currentButton?.text && it.getString("Croatian") == button.text }
            }
            if (matchingPair != null) {
                // The word and its translation match, remove both buttons
                currentButton?.visibility = View.INVISIBLE
                button.visibility = View.INVISIBLE
                currentlySelected = -1
                currentButton = null
            } else {
                // The word and its translation do not match, deselect both buttons
                currentButton?.setTextColor(Color.BLACK)
                button.setTextColor(Color.BLACK)
                currentlySelected = -1
                currentButton = null
            }
        }
    }
}