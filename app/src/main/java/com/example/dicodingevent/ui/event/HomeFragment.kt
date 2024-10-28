package com.example.dicodingevent.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.data.source.Result
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.ui.bookmark.BookmarkViewModel
import com.example.dicodingevent.ui.favorite.FavoriteViewModel
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.dataStore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        ViewModelFactory.getInstance(requireContext(), sharedPref)
    }
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
            onBookmarkClick = { event ->
                if (event.isBookmarked) {
                    bookmarkViewModel.deleteEvent(event)
                } else {
                    bookmarkViewModel.saveEvent(event)
                }
            },
            onFavoriteClick = { event ->
                if (event.isFavorite) {
                    favoriteViewModel.deleteFavoriteEvent(event)
                } else {
                    favoriteViewModel.addFavoriteEvent(event)
                }
            }
        )

        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = eventAdapter
    }

    private fun observeViewModel() {
        homeViewModel.getHeadlineEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val eventData = mapEventEntityToListEventsItem(result.data)
                    eventAdapter.submitList(eventData)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun mapEventEntityToListEventsItem(eventEntities: List<EventEntity>): List<ListEventsItem> {
        return eventEntities.map { eventEntity ->
            ListEventsItem(
                id = eventEntity.id,
                name = eventEntity.name,
                description = eventEntity.description,
                imageLogo = eventEntity.imageLogo,
                cityName = eventEntity.cityName,
                endTime = eventEntity.endTime,
                beginTime = eventEntity.beginTime,
                category = eventEntity.category,
                imageUrl = eventEntity.imageUrl,
                link = eventEntity.link,
                mediaCover = eventEntity.mediaCover,
                ownerName = eventEntity.ownerName,
                quota = eventEntity.quota,
                registrants = eventEntity.registrants,
                summary = eventEntity.summary,
                active = eventEntity.active,
                isBookmarked = eventEntity.isBookmarked
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
