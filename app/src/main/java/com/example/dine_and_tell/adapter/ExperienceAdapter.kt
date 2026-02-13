package com.example.dine_and_tell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.databinding.ExperienceCardBinding
import com.example.dine_and_tell.model.Experience
import com.squareup.picasso.Picasso

class ExperienceAdapter(
    private var experiences: List<Experience>,
    private val onDeleteClicked: ((String) -> Unit)? = null
) : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val binding =
            ExperienceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ExperienceViewHolder(private val binding: ExperienceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: Experience) {
            binding.experienceTitle.text = "${experience.userId} says"
            binding.experienceDescription.text = experience.review
            experience.imageUrl.let {
                Picasso.get().load(it).into(binding.experienceImage)
            }

            if (onDeleteClicked != null && experience.firestoreId != null) {
                binding.deleteIcon.visibility = View.VISIBLE
                binding.deleteIcon.setOnClickListener {
                    onDeleteClicked.invoke(experience.firestoreId)
                }
            } else {
                binding.deleteIcon.visibility = View.GONE
            }
        }
    }
}