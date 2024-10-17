package com.example.dicodingevent.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private lateinit var upcomingViewModel: UpcomingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upcomingViewModel = ViewModelProvider(this).get(UpcomingViewModel::class.java)
        setupRecyclerView()
        observeViewModel()
        upcomingViewModel.fetchUpcomingEvents()
    }

    private fun setupRecyclerView() {
        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter()
        binding.rvUpcomingEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        upcomingViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvUpcomingEvents.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvUpcomingEvents.visibility = View.VISIBLE
            }
        })

        upcomingViewModel.upcomingEvents.observe(viewLifecycleOwner, { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No upcoming events available", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
