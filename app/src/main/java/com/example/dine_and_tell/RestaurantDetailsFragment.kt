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
import com.example.dine_and_tell.firebase.FirebaseExperienceService
import kotlinx.coroutines.launch
import com.example.dine_and_tell.RestaurantDetailsFragmentDirections

class RestaurantDetailsFragment : Fragment() {
    private var _binding: FragmentRestaurantDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: RestaurantDetailsFragmentArgs by navArgs()
    private lateinit var experienceAdapter: ExperienceAdapter
    private val firebaseExperienceService = FirebaseExperienceService()

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
        binding.restaurantPhone.text = "Phone: ${place.phone ?: "N/A"}"
        binding.restaurantOpeningHours.text = "Opening Hours: ${place.openingHours ?: "N/A"}"

        setupRecyclerView()
        loadExperiences(place.id)

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addExperienceFab.setOnClickListener {
            val action = RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToAddExperienceFragment(place.id)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        experienceAdapter = ExperienceAdapter(emptyList())
        binding.experiencesRecyclerView.apply {
            adapter = experienceAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadExperiences(restaurantId: String) {
        binding.loadingSpinner.visibility = View.VISIBLE
        lifecycleScope.launch {
            val experiences = firebaseExperienceService.getExperiencesByRestaurantId(restaurantId)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}