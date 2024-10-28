package com.example.dicodingevent.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.ui.bookmark.BookmarkViewModel
import com.example.dicodingevent.ui.favorite.FavoriteViewModel
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private lateinit var bookmarkViewModel: BookmarkViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), sharedPref)

        val finishedViewModel: FinishedViewModel by viewModels { factory }
        bookmarkViewModel = ViewModelProvider(this, factory)[BookmarkViewModel::class.java]
        favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        setupRecyclerView()
        observeViewModel(finishedViewModel)

        finishedViewModel.fetchFinishedEvents()

        return binding.root
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

        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedEvents.adapter = eventAdapter
    }

    private fun observeViewModel(finishedViewModel: FinishedViewModel) {
        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvFinishedEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No finished events available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
