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
    private val currentUserId: String? = null,
    private val showUserNameInTitle: Boolean = false,
    private val onEditClicked: ((Experience) -> Unit)? = null
) : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val binding =
            ExperienceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienceViewHolder(binding, currentUserId, showUserNameInTitle, onEditClicked)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        holder.bind(experiences[position])
    }

    override fun getItemCount() = experiences.size

    fun updateData(newExperiences: List<Experience>) {
        experiences = newExperiences
        notifyDataSetChanged()
    }

    inner class ExperienceViewHolder(
        private val binding: ExperienceCardBinding,
        private val currentUserId: String?,
        private val showUserNameInTitle: Boolean,
        private val onEditClicked: ((Experience) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: Experience) {
            if (showUserNameInTitle) {
                binding.experienceTitle.text = "${experience.userName.ifEmpty { "Someone" }} says"
            } else if (experience.restaurantName.isNotEmpty()) {
                binding.experienceTitle.text = experience.restaurantName
            } else {
                binding.experienceTitle.text = "My Review"
            }
            binding.experienceRating.rating = experience.rating
            binding.experienceDescription.text = experience.review
            if (!experience.imageUrl.isNullOrEmpty()) {
                binding.imageProgress.visibility = View.VISIBLE
                Picasso.get()
                    .load(experience.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.experienceImage, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            binding.imageProgress.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            binding.imageProgress.visibility = View.GONE
                        }
                    })
            } else {
                binding.experienceImage.setImageResource(R.drawable.placeholder)
                binding.imageProgress.visibility = View.GONE
            }

            // Show edit icon if:
            // 1. Edit callback is provided
            // 2. Experience has an ID (saved in DB)
            // 3. EITHER currentUserId is null (assume authorized context like MyExperiences)
            //    OR experience.userId matches currentUserId
            val isOwner = currentUserId == null || experience.userId == currentUserId
            
            if (onEditClicked != null && experience.firestoreId != null && isOwner) {
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