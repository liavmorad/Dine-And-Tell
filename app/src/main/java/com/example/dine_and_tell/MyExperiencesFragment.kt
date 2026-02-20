package com.example.dine_and_tell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dine_and_tell.databinding.FragmentMyExperiencesBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dine_and_tell.MyExperiencesFragmentDirections
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dine_and_tell.adapter.ExperienceAdapter

import com.example.dine_and_tell.viewmodel.ExperienceViewModel
import com.example.dine_and_tell.viewmodel.UserViewModel

class MyExperiencesFragment : Fragment() {
    private var _binding: FragmentMyExperiencesBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding accessed before onCreateView or after onDestroyView")
    
    private val experienceViewModel: ExperienceViewModel by viewModels {
        val database = com.example.dine_and_tell.database.AppDatabase.getDatabase(requireContext())
        val repository = com.example.dine_and_tell.repository.ExperienceRepository.getInstance(database.experienceDao())
        com.example.dine_and_tell.viewmodel.ExperienceViewModelFactory(repository)
    }
    private val userViewModel: UserViewModel by viewModels()
    
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

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                experienceViewModel.getExperiencesByUserId(it.id)
            }
        }

        experienceViewModel.experiences.observe(viewLifecycleOwner) { experiences ->
            experienceAdapter.updateData(experiences)
        }

        experienceViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        experienceAdapter = ExperienceAdapter(emptyList()) { experience ->
            val action = MyExperiencesFragmentDirections.actionMyExperiencesFragmentToAddExperienceFragment(
                restaurantId = null,
                restaurantName = null,
                experience = experience
            )
            findNavController().navigate(action)
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
