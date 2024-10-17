package com.example.dicodingevent.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.ui.event.FinishedViewModel

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

        finishedViewModel = ViewModelProvider(this).get(FinishedViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter()
        binding.rvFinishedEvents.adapter = eventAdapter
    }

    private fun observeViewModel() {
        finishedViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvFinishedEvents.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvFinishedEvents.visibility = View.VISIBLE
            }
        })

        finishedViewModel.finishedEvents.observe(viewLifecycleOwner, Observer { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No finished events available", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
