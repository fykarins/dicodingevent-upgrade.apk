package com.example.dicodingevent.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingevent.R
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiService
import com.example.dicodingevent.data.source.EventRepository
import com.example.dicodingevent.utils.AppExecutors
import com.example.dicodingevent.utils.EventAdapter
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: EventAdapter
    private val repository by lazy {
        EventRepository.getInstance(
            apiService = ApiService.create(),
            appExecutors = AppExecutors(),
            eventDao = EventDao.getInstance()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        adapter = EventAdapter { eventItem ->
            viewModel.toggleBookmarkStatus(eventItem)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { users ->
            val items = arrayListOf<ListEventsItem>()
            users.map {
                val item = ListEventsItem(
                    id = it.id,
                    name = it.name,
                    imageLogo = it.mediaCover,
                    active = it.active,
                    beginTime = it.beginTime,
                    cityName = it.cityName,
                    description = it.description,
                    endTime = it.endTime,
                    imageUrl = it.imageUrl,
                    isBookmarked = it.isBookmarked,
                    link = it.link,
                    mediaCover = it.mediaCover,
                    ownerName = it.ownerName,
                    quota = it.quota,
                    registrants = it.registrants,
                    summary = it.summary,
                    category = it.category
                )
                items.add(item)
            }
            adapter.submitList(items)
        }
    }
}