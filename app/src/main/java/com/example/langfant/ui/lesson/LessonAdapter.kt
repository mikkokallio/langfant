package com.example.langfant.ui.lesson

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.langfant.R
import android.widget.Button
import com.example.langfant.ExerciseActivity

class LessonAdapter(private val lessons: List<Lesson>) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    private val expandedState = Array(lessons.size) { false }

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lessonName: TextView = itemView.findViewById(R.id.lessonName)
        val lessonImage: ImageView = itemView.findViewById(R.id.lessonImage)
        //val lessonRepetitions: TextView = itemView.findViewById(R.id.lessonRepetitions)
        //val lessonWords: TextView = itemView.findViewById(R.id.lessonWords)
        val lessonDescription: TextView = itemView.findViewById(R.id.lessonDescription)
        val startButton: Button = itemView.findViewById(R.id.startButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val currentLesson = lessons[position]
        holder.lessonName.text = currentLesson.name
        holder.lessonImage.setImageResource(currentLesson.imageResource)
        holder.lessonDescription.text = currentLesson.description

        // Show/hide description and start button based on expanded state
        if (expandedState[position]) {
            holder.lessonDescription.visibility = View.VISIBLE
            holder.startButton.visibility = View.VISIBLE
        } else {
            holder.lessonDescription.visibility = View.GONE
            holder.startButton.visibility = View.GONE
        }

        // Toggle expanded state on lesson item click
        holder.itemView.setOnClickListener {
            expandedState[position] = !expandedState[position]
            notifyDataSetChanged()
        }
        holder.startButton.setOnClickListener {
            // Start ExerciseActivity and pass lesson words as extras
            val intent = Intent(holder.itemView.context, ExerciseActivity::class.java)
            val keywordsArray = lessons[position].keywords.toTypedArray()
            val vocabularyObject = lessons[position].vocabulary
            val vocabularyArray = vocabularyObject.entries.map { it.key }.toTypedArray()
            intent.putExtra("keywords", keywordsArray)
            intent.putExtra("vocabulary", vocabularyArray)
            intent.putExtra("maxWords", lessons[position].maxWords)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = lessons.size
}
