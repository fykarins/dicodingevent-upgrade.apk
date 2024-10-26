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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate binding dari layout fragment_home.xml
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        homeViewModel.getHeadlineEvent()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val eventEntity = mapListEventsItemToEventEntity(event)
            if (event.isBookmarked) {
                homeViewModel.deleteEvent(eventEntity)
            } else {
                homeViewModel.saveEvent(eventEntity)
            }
        }
        binding.recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = eventAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.events.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val eventData = result.data
                    val mappedEventData = mapEventEntityToListEventsItem(eventData)
                    eventAdapter.submitList(mappedEventData)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun mapListEventsItemToEventEntity(listEventsItem: ListEventsItem): EventEntity {
        return EventEntity(
            id = listEventsItem.id,
            name = listEventsItem.name,
            description = listEventsItem.description,
            imageLogo = listEventsItem.imageLogo,
            cityName = listEventsItem.cityName,
            endTime = listEventsItem.endTime,
            beginTime = listEventsItem.beginTime,
            category = listEventsItem.category,
            imageUrl = listEventsItem.imageUrl,
            link = listEventsItem.link,
            mediaCover = listEventsItem.mediaCover,
            ownerName = listEventsItem.ownerName,
            quota = listEventsItem.quota,
            registrants = listEventsItem.registrants,
            summary = listEventsItem.summary,
            active = listEventsItem.active,
            isBookmarked = listEventsItem.isBookmarked
        )
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