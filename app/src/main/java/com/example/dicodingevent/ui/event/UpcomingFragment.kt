package com.example.dicodingevent.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.databinding.FragmentUpcomingBinding
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        val upcomingViewModel: UpcomingViewModel by viewModels { factory }

        Log.d("UpcomingFragment", "Setting up RecyclerView and ViewModel")
        setupRecyclerView(upcomingViewModel)
        observeViewModel(upcomingViewModel)

        upcomingViewModel.fetchUpcomingEvents()
    }


    private fun setupRecyclerView(upcomingViewModel: UpcomingViewModel) {

        eventAdapter = EventAdapter { event ->
            if (event.isBookmarked) {
                upcomingViewModel.deleteEvent(event)
            } else {
                upcomingViewModel.saveEvent(event)
            }
        }
        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvents.adapter = eventAdapter
    }

    private fun observeViewModel(upcomingViewModel: UpcomingViewModel) {
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
}