package com.example.dicodingevent.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentFavoriteBinding
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val sharedPref = SettingPreferences.getInstance(requireContext())
        val factory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
            onFavoriteClick = { event ->
                val eventEntity = mapToEventEntity(event)
                if (event.isFavorite) {
                    favoriteViewModel.deleteFavoriteEvent(eventEntity)
                } else {
                    favoriteViewModel.addFavoriteEvent(eventEntity)
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = eventAdapter
    }

    private fun observeViewModel() {
        favoriteViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                val eventItems = events.map { eventEntity ->
                    ListEventsItem(
                        id = eventEntity.id,
                        name = eventEntity.name,
                        imageLogo = eventEntity.mediaCover,
                        active = eventEntity.active,
                        beginTime = eventEntity.beginTime,
                        cityName = eventEntity.cityName,
                        description = eventEntity.description,
                        endTime = eventEntity.endTime,
                        imageUrl = eventEntity.imageUrl,
                        isFavorite = true,
                        link = eventEntity.link,
                        mediaCover = eventEntity.mediaCover,
                        ownerName = eventEntity.ownerName,
                        quota = eventEntity.quota,
                        registrants = eventEntity.registrants,
                        summary = eventEntity.summary,
                        category = eventEntity.category
                    )
                }
                eventAdapter.submitList(eventItems)
            } else {
                eventAdapter.submitList(emptyList())
                Toast.makeText(requireContext(), "No favorite events available", Toast.LENGTH_SHORT).show()
            }
        }

        favoriteViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun mapToEventEntity(eventItem: ListEventsItem): EventEntity {
        return EventEntity(
            id = eventItem.id,
            name = eventItem.name,
            mediaCover = eventItem.mediaCover,
            active = eventItem.active,
            beginTime = eventItem.beginTime,
            cityName = eventItem.cityName,
            description = eventItem.description,
            endTime = eventItem.endTime,
            imageUrl = eventItem.imageUrl,
            link = eventItem.link,
            ownerName = eventItem.ownerName,
            quota = eventItem.quota,
            registrants = eventItem.registrants,
            summary = eventItem.summary,
            imageLogo = eventItem.imageLogo,
            category = eventItem.category,
            isFavorite = eventItem.isFavorite
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
