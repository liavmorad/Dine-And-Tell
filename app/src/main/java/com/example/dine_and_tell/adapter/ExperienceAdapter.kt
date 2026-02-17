package com.example.dine_and_tell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.R
import com.example.dine_and_tell.databinding.ExperienceCardBinding
import com.example.dine_and_tell.model.Experience
import com.squareup.picasso.Picasso

class ExperienceAdapter(
    private var experiences: List<Experience>,
    private val onEditClicked: ((Experience) -> Unit)? = null
) : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val binding =
            ExperienceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienceViewHolder(binding, onEditClicked)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        holder.bind(experiences[position])
    }

    override fun getItemCount() = experiences.size

    fun updateData(newExperiences: List<Experience>) {
        experiences = newExperiences
        notifyDataSetChanged()
    }

    inner class ExperienceViewHolder(private val binding: ExperienceCardBinding, private val onEditClicked: ((Experience) -> Unit)?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: Experience) {
            if (experience.restaurantName.isNotEmpty()) {
                binding.experienceTitle.text = experience.restaurantName
            } else {
                binding.experienceTitle.text = "My Review"
            }
            binding.experienceRating.rating = experience.rating
            binding.experienceDescription.text = experience.review
            if (!experience.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(experience.imageUrl)
                    .placeholder(R.drawable.default_restaurant)
                    .error(R.drawable.default_restaurant)
                    .into(binding.experienceImage)
            } else {
                binding.experienceImage.setImageResource(R.drawable.default_restaurant)
            }

            if (onEditClicked != null && experience.firestoreId != null) {
                binding.editIcon.visibility = View.VISIBLE
                binding.editIcon.setOnClickListener {
                    onEditClicked.invoke(experience)
                }
            } else {
                binding.editIcon.visibility = View.GONE
            }
        }
    }
}