package com.example.dine_and_tell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_and_tell.adapter.FooterAdapter
import com.example.dine_and_tell.adapter.RestaurantsAdapter
import com.example.dine_and_tell.databinding.FragmentExplorationBinding
import com.example.dine_and_tell.model.Place
import com.example.dine_and_tell.repository.PlacesRepository
import com.example.dine_and_tell.viewmodel.ApiResult
import com.example.dine_and_tell.viewmodel.ExplorationViewModel
import com.example.dine_and_tell.viewmodel.ExplorationViewModelFactory
import com.example.dine_and_tell.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class ExplorationFragment : Fragment(), RestaurantsAdapter.OnItemClickListener {
    private var _binding: FragmentExplorationBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding accessed before onCreateView or after onDestroyView")
    private lateinit var viewModel: ExplorationViewModel
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var restaurantsAdapter: RestaurantsAdapter
    private lateinit var footerAdapter: FooterAdapter
    private var currentFilter: String = ""
    
    private val cities = mapOf(
        "Tel Aviv" to "place:51e8ea76261664414059036594d31e0b4040f00101f9015e18150000000000c00206920317d7aad79cd6bed790d791d799d791e28093d799d7a4d795",
        "Haifa" to "place:512491d7b58180414059f475785d9a664040f00101f901702d150000000000c00206920308d797d799d7a4d794",
        "Jerusalem" to "place:511aaac1903a9b414059cdd4a09009cc3f40f00101f901e613150000000000c0020692030ed799d7a8d795d7a9d79cd799d79d",
        "Rishon LeZion" to "place:5120a1e86fa163414059ec31d17b74f93f40f00101f9012117150000000000c00206920315d7a8d790d7a9d795d79f20d79cd7a6d799d795d79f",
        "Ramat Gan" to "place:512964441ff26941405984e0129106094040f00101f9015d18150000000000c0020692030bd7a8d79ed7aa20d792d79f"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val firebaseAuthUser = FirebaseAuth.getInstance().currentUser
        
        firebaseAuthUser?.let { user ->
            userViewModel.fetchUser(user.uid)
        }

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val username = user?.username ?: firebaseAuthUser?.displayName ?: "Guest"

            binding.welcomeText.text = "Hello, $username"
        }

        setupRecyclerView()
        setupSpinner()

        val placesRepository = PlacesRepository()
        val viewModelFactory = ExplorationViewModelFactory(placesRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ExplorationViewModel::class.java]

        viewModel.places.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.overlayView.visibility = View.VISIBLE
                }
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.overlayView.visibility = View.GONE
                    restaurantsAdapter.setData(result.data)
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.overlayView.visibility = View.GONE
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
    }
    
    private fun setupSpinner() {
        val cityNames = cities.keys.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_city, cityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapter

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val cityName = parent.getItemAtPosition(position) as String
                currentFilter = cities[cityName] ?: ""
                viewModel.getPlaces(currentFilter)
                binding.restaurantsRecyclerView.scrollToPosition(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupRecyclerView() {
        restaurantsAdapter = RestaurantsAdapter(mutableListOf(), this)
        footerAdapter = FooterAdapter { viewModel.loadMorePlaces(currentFilter) }
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
                        viewModel.loadMorePlaces(currentFilter)
                    }
                }
            })
        }
    }

    override fun onItemClick(place: Place) {
        val action = ExplorationFragmentDirections.actionExplorationFragmentToRestaurantDetailsFragment(place)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
