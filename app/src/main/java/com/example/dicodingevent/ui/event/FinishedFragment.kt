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
import com.example.dicodingevent.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private lateinit var finishedViewModel: FinishedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val binding = _binding
            ?: throw IllegalStateException("View binding failed")

        finishedViewModel = ViewModelProvider(this)[FinishedViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter()
        binding.rvFinishedEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvFinishedEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No finished events available", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}