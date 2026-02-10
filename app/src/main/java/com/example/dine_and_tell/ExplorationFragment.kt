package com.example.dine_and_tell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dine_and_tell.databinding.FragmentExplorationBinding
import com.example.dine_and_tell.repository.PlacesRepository
import com.example.dine_and_tell.viewmodel.ApiResult
import com.example.dine_and_tell.viewmodel.ExplorationViewModel
import com.example.dine_and_tell.viewmodel.ExplorationViewModelFactory

class ExplorationFragment : Fragment() {
    private var _binding: FragmentExplorationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ExplorationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placesRepository = PlacesRepository()
        val viewModelFactory = ExplorationViewModelFactory(placesRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ExplorationViewModel::class.java]

        viewModel.places.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    // Show loading indicator
                }
                is ApiResult.Success -> {
                    // Update UI with the list of places
                    val places = result.data
                    // For example, display the first place's name in a toast
                    Toast.makeText(requireContext(), "First place: ${places.firstOrNull()?.name}", Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Error -> {
                    // Show error message
                    Toast.makeText(requireContext(), "Error: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Example call to get places.
        val filter = "place:51e8ea76261664414059036594d31e0b4040f00101f9015e18150000000000c00206920317d7aad79cd6bed790d791d799d791e28093d799d7a4d795"
        val limit = 20
        viewModel.getPlaces(filter, limit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}