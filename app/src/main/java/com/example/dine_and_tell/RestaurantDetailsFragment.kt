package com.example.dine_and_tell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dine_and_tell.adapter.ExperienceAdapter
import com.example.dine_and_tell.databinding.FragmentRestaurantDetailsBinding
import androidx.fragment.app.viewModels
import com.example.dine_and_tell.viewmodel.ExperienceViewModel

import com.google.firebase.auth.FirebaseAuth

class RestaurantDetailsFragment : Fragment() {
    private var _binding: FragmentRestaurantDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: RestaurantDetailsFragmentArgs by navArgs()
    private lateinit var experienceAdapter: ExperienceAdapter
    private val experienceViewModel: ExperienceViewModel by viewModels {
        val database = com.example.dine_and_tell.database.AppDatabase.getDatabase(requireContext())
        val repository = com.example.dine_and_tell.repository.ExperienceRepository.getInstance(database.experienceDao())
        com.example.dine_and_tell.viewmodel.ExperienceViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val place = args.place
        binding.restaurantName.text = place.name
        binding.restaurantAddress.text = "Address: ${place.address}"
        binding.restaurantPhone.text = "Phone: ${place.phone ?: "-"}"
        binding.restaurantOpeningHours.text = "Opening Hours: ${place.openingHours ?: "-"}"

        setupRecyclerView()
        loadExperiences(place.id)

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addExperienceFab.setOnClickListener {
            val action = RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToAddExperienceFragment(place.id, place.name)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val place = args.place

        experienceAdapter = ExperienceAdapter(
            experiences = emptyList(),
            currentUserId = currentUserId,
            onEditClicked = { experience ->
                // Navigate to AddExperienceFragment in edit mode
                val action = RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToAddExperienceFragment(
                    restaurantId = place.id,
                    restaurantName = place.name,
                    experience = experience
                )
                findNavController().navigate(action)
            }
        )
        binding.experiencesRecyclerView.apply {
            adapter = experienceAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadExperiences(restaurantId: String) {
        binding.loadingSpinner.visibility = View.VISIBLE
        
        experienceViewModel.restaurantExperiences.observe(viewLifecycleOwner) { experiences ->
            experienceAdapter.updateData(experiences)
            if (experiences.isEmpty()) {
                binding.noExperiencesMessage.visibility = View.VISIBLE
                binding.experiencesRecyclerView.visibility = View.GONE
            } else {
                binding.noExperiencesMessage.visibility = View.GONE
                binding.experiencesRecyclerView.visibility = View.VISIBLE
            }
            binding.loadingSpinner.visibility = View.GONE
        }

        experienceViewModel.getExperiencesByRestaurantId(restaurantId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}