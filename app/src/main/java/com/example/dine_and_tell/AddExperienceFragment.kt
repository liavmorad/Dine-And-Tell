package com.example.dine_and_tell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dine_and_tell.databinding.FragmentAddExperienceBinding

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dine_and_tell.model.Experience
import com.example.dine_and_tell.viewmodel.ExperienceViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class AddExperienceFragment : Fragment() {
    private var _binding: FragmentAddExperienceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExperienceViewModel by viewModels()
    private val args: AddExperienceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExperienceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val experience = args.experience
        if (experience != null) {
            binding.reviewEditText.setText(experience.review)
            binding.ratingBar.rating = experience.rating
            binding.saveButton.text = "Update Review"
        }

        binding.saveButton.setOnClickListener {
            val review = binding.reviewEditText.text.toString()
            val rating = binding.ratingBar.rating
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                Toast.makeText(requireContext(), "You must be signed in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (review.isNotEmpty()) {
                if (experience != null) {
                    val updatedExperience = experience.copy(
                        review = review,
                        rating = rating,
                        dateOfVisit = System.currentTimeMillis()
                    )

                    viewModel.updateExperience(updatedExperience)
                    Toast.makeText(context, "Experience updated!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    val restaurantId = args.restaurantId ?: ""
                    val restaurantName = args.restaurantName ?: ""
                    val userId = currentUser.uid

                    val newExperience = Experience(
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        userId = userId,
                        review = review,
                        rating = rating,
                        dateOfVisit = System.currentTimeMillis()
                    )
                    viewModel.addExperience(newExperience)
                }
            } else {
                Toast.makeText(context, "Please write a review", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addExperienceStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                if (experience == null) {
                    Toast.makeText(context, "Experience saved!", Toast.LENGTH_SHORT).show()
                    binding.reviewEditText.text.clear()
                    binding.ratingBar.rating = 0f
                    findNavController().popBackStack()
                }
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