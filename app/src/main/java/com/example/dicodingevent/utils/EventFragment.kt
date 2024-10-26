package com.example.dicodingevent.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentEventBinding
import com.example.dicodingevent.data.source.Result

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
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
                isBookmarked = eventEntity.isBookmarked
            )
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabName = arguments?.getString(ARG_TAB) ?: ""

        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        val viewModel: EventViewModel by viewModels { factory }

        val eventAdapter = EventAdapter(onBookmarkClick = { event ->
            Toast.makeText(context, "Bookmark clicked for ${event.name}", Toast.LENGTH_SHORT).show()
        })

        if (tabName == TAB_EVENT) {
            viewModel.getHeadlineEvents().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val eventData = result.data
                            val convertedEventData = convertEventEntityToListEventsItem(eventData)
                            eventAdapter.submitEvents(convertedEventData)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Terjadi kesalahan: ${result.error}",
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
