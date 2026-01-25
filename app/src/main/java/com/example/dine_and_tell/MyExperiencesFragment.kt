package com.example.dine_and_tell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dine_and_tell.databinding.FragmentMyExperiencesBinding

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dine_and_tell.adapter.ExperienceAdapter
import com.example.dine_and_tell.viewmodel.ExperienceViewModel

class MyExperiencesFragment : Fragment() {
    private var _binding: FragmentMyExperiencesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExperienceViewModel by viewModels()
    private lateinit var experienceAdapter: ExperienceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyExperiencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.experiences.observe(viewLifecycleOwner) { experiences ->
            experienceAdapter.updateData(experiences)
        }

        // Hardcoded user ID for demonstration
        viewModel.getExperiencesByUserId("user123")
    }

    private fun setupRecyclerView() {
        experienceAdapter = ExperienceAdapter(emptyList()) { experienceId ->
            viewModel.deleteExperience(experienceId)
            // Refresh the list after deletion
            viewModel.getExperiencesByUserId("user123")
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = experienceAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}