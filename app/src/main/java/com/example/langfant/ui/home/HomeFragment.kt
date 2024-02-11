package com.example.langfant.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.TextView
import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.example.langfant.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langfant.DatabaseHelper
import com.example.langfant.R
import com.example.langfant.ui.lesson.Lesson
import com.example.langfant.ui.lesson.LessonAdapter

class HomeFragment : Fragment() {

    private lateinit var lessonAdapter: LessonAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        val lessonRecyclerView: RecyclerView = rootView.findViewById(R.id.lessonRecyclerView)

        val databaseName = "Lessons.db"
        context?.deleteDatabase(databaseName)

        databaseHelper = DatabaseHelper(requireContext())
        val lessonList = databaseHelper.getAllLessons()

        // Initialize the LessonAdapter with the lesson list
        lessonAdapter = LessonAdapter(lessonList)

        // Set layout manager and adapter for the RecyclerView
        lessonRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        lessonRecyclerView.adapter = lessonAdapter

        return rootView
    }
}
