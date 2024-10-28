package com.example.dicodingevent.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.databinding.FragmentUpcomingBinding
import com.example.dicodingevent.ui.bookmark.BookmarkViewModel
import com.example.dicodingevent.ui.favorite.FavoriteViewModel
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private val bookmarkViewModel: BookmarkViewModel by viewModels {
        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        ViewModelFactory.getInstance(requireContext(), sharedPref)
    }
    private val favoriteViewModel: FavoriteViewModel by viewModels {
        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        ViewModelFactory.getInstance(requireContext(), sharedPref)
    }
    private val upcomingViewModel: UpcomingViewModel by viewModels {
        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        ViewModelFactory.getInstance(requireContext(), sharedPref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        upcomingViewModel.fetchUpcomingEvents()
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
            onFavoriteClick = {}

        )

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvUpcomingEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        upcomingViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No upcoming events available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
