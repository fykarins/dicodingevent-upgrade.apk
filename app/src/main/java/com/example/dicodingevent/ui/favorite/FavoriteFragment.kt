package com.example.dicodingevent.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentFavoriteBinding
import com.example.dicodingevent.ui.main.MainViewModel
import com.example.dicodingevent.utils.DataStoreManager
import com.example.dicodingevent.utils.DataStoreViewModel
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize DataStore and ViewModel
        val dataStoreManager = DataStoreManager(requireContext())
        val dataStoreViewModelFactory = DataStoreViewModel.Factory(dataStoreManager)
        val dataStoreViewModel = ViewModelProvider(this, dataStoreViewModelFactory)[DataStoreViewModel::class.java]

        // Initialize MainViewModel with preferences
        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        val mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        val viewModel: FavoriteViewModel by viewModels { factory }

        this.viewModel = viewModel

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchFavoriteEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { eventItem ->
            if (eventItem.isFavorite) {
                viewModel.deleteFavoriteEvent(eventItem)
            } else {
                viewModel.addFavoriteEvent(eventItem)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                val eventItems = events.map { eventEntity ->
                    ListEventsItem(
                        id = eventEntity.id,
                        name = eventEntity.name,
                        imageLogo = eventEntity.mediaCover,
                        active = eventEntity.active,
                        beginTime = eventEntity.beginTime,
                        cityName = eventEntity.cityName,
                        description = eventEntity.description,
                        endTime = eventEntity.endTime,
                        imageUrl = eventEntity.imageUrl,
                        isBookmarked = eventEntity.isBookmarked,
                        link = eventEntity.link,
                        mediaCover = eventEntity.mediaCover,
                        ownerName = eventEntity.ownerName,
                        quota = eventEntity.quota,
                        registrants = eventEntity.registrants,
                        summary = eventEntity.summary,
                        category = eventEntity.category
                    )
                }
                adapter.submitList(eventItems)
            } else {
                Toast.makeText(requireContext(), "No favorite events available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
