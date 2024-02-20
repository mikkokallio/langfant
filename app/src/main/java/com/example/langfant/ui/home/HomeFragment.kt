package com.example.langfant.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.TextView
import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.example.langfant.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langfant.R
import com.example.langfant.ui.lesson.Lesson
import com.example.langfant.ui.lesson.LessonAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class HomeFragment : Fragment() {

    private lateinit var lessonAdapter: LessonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        val lessonRecyclerView: RecyclerView = rootView.findViewById(R.id.lessonRecyclerView)

        val lessonList = loadLessonsFromJson()

        // Initialize the LessonAdapter with the lesson list
        lessonAdapter = LessonAdapter(lessonList)

        // Set layout manager and adapter for the RecyclerView
        lessonRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        lessonRecyclerView.adapter = lessonAdapter

        return rootView
    }
    override fun onResume() {
        super.onResume()

        val completedLessonIds = getCompletedLessonIds(requireContext())
        Log.d("Completed Lessons", completedLessonIds.toString())
    }

    private fun loadLessonsFromJson(): List<Lesson> {
        val lessons = mutableListOf<Lesson>()

        try {
            val json = resources.openRawResource(R.raw.lessons).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val lessonJson = jsonArray.getJSONObject(i)
                val id = lessonJson.getInt("id")
                val name = lessonJson.getString("name")
                val imageResource = lessonJson.getString("image_resource")
                val description = lessonJson.getString("description")
                val type = lessonJson.getString("type")
                val vocabulary = parseJsonArray(lessonJson.getJSONArray("vocabulary"))
                val template = lessonJson.getString("template")
                val maxWords = lessonJson.getInt("max_words")

                val drawableId = resources.getIdentifier(imageResource, "drawable", requireContext().packageName)
                val lesson = Lesson(id, name, drawableId, description, type, vocabulary, template, maxWords)
                lessons.add(lesson)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lessons
    }
    private fun parseJsonArray(jsonArray: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }
    private fun getCompletedLessonIds(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences("completed_lessons", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("completed_lessons", HashSet<String>()) ?: HashSet<String>()
    }

}