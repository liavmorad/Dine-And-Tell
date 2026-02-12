package com.example.dine_and_tell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.adapter.FooterAdapter
import com.example.dine_and_tell.adapter.RestaurantsAdapter
import com.example.dine_and_tell.databinding.FragmentExplorationBinding
import com.example.dine_and_tell.model.Place
import com.example.dine_and_tell.repository.PlacesRepository
import com.example.dine_and_tell.viewmodel.ApiResult
import com.example.dine_and_tell.viewmodel.ExplorationViewModel
import com.example.dine_and_tell.viewmodel.ExplorationViewModelFactory

class ExplorationFragment : Fragment(), RestaurantsAdapter.OnItemClickListener {
    private var _binding: FragmentExplorationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ExplorationViewModel
    private lateinit var restaurantsAdapter: RestaurantsAdapter
    private lateinit var footerAdapter: FooterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val placesRepository = PlacesRepository()
        val viewModelFactory = ExplorationViewModelFactory(placesRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ExplorationViewModel::class.java]

        viewModel.places.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    restaurantsAdapter.setData(result.data)
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoadingMore.observe(viewLifecycleOwner) { isLoadingMore ->
            if (isLoadingMore) {
                footerAdapter.setState(FooterAdapter.FooterState.Loading)
            } else {
                footerAdapter.setState(FooterAdapter.FooterState.Done)
            }
        }

        // Example call to get places.
        val filter = "place:51e8ea76261664414059036594d31e0b4040f00101f9015e18150000000000c00206920317d7aad79cd6bed790d791d799d791e28093d799d7a4d795"
        viewModel.getPlaces(filter)
    }

    private fun setupRecyclerView() {
        restaurantsAdapter = RestaurantsAdapter(mutableListOf(), this)
        footerAdapter = FooterAdapter { viewModel.loadMorePlaces("place:51e8ea76261664414059036594d31e0b4040f00101f9015e18150000000000c00206920317d7aad79cd6bed790d791d799d791e28093d799d7a4d795") }
        val concatAdapter = ConcatAdapter(restaurantsAdapter, footerAdapter)

        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == concatAdapter.itemCount - 1 && footerAdapter.itemCount > 0) 2 else 1
            }
        }

        binding.restaurantsRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = concatAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItemPosition == totalItemCount - 1 && viewModel.isLoadingMore.value == false) {
                        viewModel.loadMorePlaces("place:51e8ea76261664414059036594d31e0b4040f00101f9015e18150000000000c00206920317d7aad79cd6bed790d791d799d791e28093d799d7a4d795")
                    }
                }
            })
        }
    }

    override fun onItemClick(place: Place) {
        Toast.makeText(context, "Clicked on: ${place.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}