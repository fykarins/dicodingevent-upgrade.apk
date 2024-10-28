package com.example.dicodingevent.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentEventBinding
import com.example.dicodingevent.data.source.Result
import com.example.dicodingevent.ui.bookmark.BookmarkViewModel
import com.example.dicodingevent.ui.favorite.FavoriteViewModel
import kotlinx.coroutines.launch

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabName: String
    private lateinit var bookmarkViewModel: BookmarkViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)

        val dataStore = requireContext().dataStore
        val settingPreferences = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), settingPreferences)

        bookmarkViewModel = ViewModelProvider(this, factory)[BookmarkViewModel::class.java]
        favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        return binding.root
    }

    private fun convertEventEntityToListEventsItem(eventEntities: List<EventEntity>): List<ListEventsItem> {
        return eventEntities.map { eventEntity ->
            ListEventsItem(
                id = eventEntity.id,
                name = eventEntity.name,
                description = eventEntity.description,
                imageLogo = eventEntity.imageLogo,
                ownerName = eventEntity.ownerName,
                cityName = eventEntity.cityName,
                quota = eventEntity.quota,
                registrants = eventEntity.registrants,
                beginTime = eventEntity.beginTime,
                endTime = eventEntity.endTime,
                link = eventEntity.link,
                mediaCover = eventEntity.mediaCover,
                summary = eventEntity.summary,
                category = eventEntity.category,
                imageUrl = eventEntity.imageUrl,
                active = eventEntity.active,
                isBookmarked = eventEntity.isBookmarked,
                isFavorite = eventEntity.isFavorite
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabName = arguments?.getString(ARG_TAB) ?: ""

        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        val viewModel: EventViewModel by viewModels { factory }

        eventAdapter = EventAdapter(
            onBookmarkClick = { event ->
                if (event.isBookmarked) {
                    bookmarkViewModel.deleteEvent(event)
                    Toast.makeText(context, "Event removed from bookmarks", Toast.LENGTH_SHORT).show()
                } else {
                    bookmarkViewModel.saveEvent(event)
                    Toast.makeText(context, "Event added to bookmarks", Toast.LENGTH_SHORT).show()
                }
            },
            onFavoriteClick = { event ->
                if (event.isFavorite) {
                    favoriteViewModel.deleteFavoriteEvent(event)
                    Toast.makeText(context, "Event removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    favoriteViewModel.addFavoriteEvent(event)
                    Toast.makeText(context, "Event added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        )

        if (tabName == TAB_EVENT) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getHeadlineEvents().collect { result ->
                    when (result) {
                        is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val eventData = result.data
                            val convertedEventData = convertEventEntityToListEventsItem(eventData)
                            eventAdapter.submitEvents(convertedEventData)
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
        }

        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = eventAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TAB = "arg_tab"
        const val TAB_EVENT = "event"
    }
}
