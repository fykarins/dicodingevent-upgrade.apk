package com.example.dicodingevent.ui.bookmark

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentBookmarkBinding
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory

private val Context.dataStore by preferencesDataStore(name = "settings")

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookmarkViewModel: BookmarkViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE

        val settingPreferences = SettingPreferences.getInstance(requireContext())
        val factory = ViewModelFactory.getInstance(requireContext(), settingPreferences)

        bookmarkViewModel = ViewModelProvider(this, factory)[BookmarkViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

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
            }
        )

        binding.bookmarkRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bookmarkRecyclerView.adapter = eventAdapter
    }

    private fun observeViewModel() {
        bookmarkViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("BookmarkFragment", "Loading state: $isLoading")
            binding.bookmarkRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        bookmarkViewModel.bookmarkedEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitEvents(events)
            } else {
                Toast.makeText(requireContext(), "No bookmarked events available", Toast.LENGTH_SHORT).show()
            }
        }

        bookmarkViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                bookmarkViewModel.clearErrorMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
