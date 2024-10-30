package com.example.dicodingevent.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentFavoriteBinding
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE


        // Initialize ViewModel
        val sharedPref = SettingPreferences.getInstance(requireContext())
        val factory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        favoriteViewModel.fetchFavoriteEvents()

        return binding.root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
            onFavoriteClick = { event ->
                if (event.isFavorite) {
                    favoriteViewModel.deleteFavoriteEvent(event)
                } else {
                    favoriteViewModel.addFavoriteEvent(event)
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = eventAdapter
    }

    private fun observeViewModel() {
        favoriteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("FavoriteFragment", "Loading state: $isLoading")
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        favoriteViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
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
                        isFavorite = true,  // Ensure events are marked as favorites
                        link = eventEntity.link,
                        mediaCover = eventEntity.mediaCover,
                        ownerName = eventEntity.ownerName,
                        quota = eventEntity.quota,
                        registrants = eventEntity.registrants,
                        summary = eventEntity.summary,
                        category = eventEntity.category
                    )
                }
                eventAdapter.submitList(eventItems)
            } else {
                Toast.makeText(requireContext(), "No favorite events available", Toast.LENGTH_SHORT).show()
            }
        }

        favoriteViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
