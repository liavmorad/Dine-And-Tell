package com.example.dine_and_tell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dine_and_tell.databinding.FragmentAddExperienceBinding

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.dine_and_tell.model.Experience
import com.example.dine_and_tell.viewmodel.ExperienceViewModel

class AddExperienceFragment : Fragment() {
    private var _binding: FragmentAddExperienceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExperienceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExperienceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            val review = binding.reviewEditText.text.toString()
            val rating = binding.ratingBar.rating

            if (review.isNotEmpty()) {
                // Hardcoded values for demonstration
                val restaurantId = "restaurant123"
                val userId = "user123"

                val experience = Experience(
                    restaurantId = restaurantId,
                    userId = userId,
                    review = review,
                    rating = rating,
                    dateOfVisit = System.currentTimeMillis()
                )
                viewModel.addExperience(experience)
            } else {
                Toast.makeText(context, "Please write a review", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addExperienceStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Experience saved!", Toast.LENGTH_SHORT).show()
                // Optionally navigate back or clear fields
                binding.reviewEditText.text.clear()
                binding.ratingBar.rating = 0f
            } else {
                Toast.makeText(context, "Failed to save experience", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}