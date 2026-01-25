package com.example.dine_and_tell.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.databinding.ExperienceItemBinding
import com.example.dine_and_tell.model.Experience

class ExperienceAdapter(
    private var experiences: List<Experience>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val binding = ExperienceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        holder.bind(experiences[position])
    }

    override fun getItemCount() = experiences.size

    fun updateData(newExperiences: List<Experience>) {
        experiences = newExperiences
        notifyDataSetChanged()
    }

    inner class ExperienceViewHolder(private val binding: ExperienceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: Experience) {
            binding.reviewText.text = experience.review
            binding.ratingText.text = "Rating: ${experience.rating}"
            binding.deleteButton.setOnClickListener {
                experience.firestoreId?.let { id -> onDeleteClick(id) }
            }
        }
    }
}
