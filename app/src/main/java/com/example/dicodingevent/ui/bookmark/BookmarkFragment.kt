package com.example.dicodingevent.ui.bookmark

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.databinding.FragmentBookmarkBinding
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore

private val Context.dataStore by preferencesDataStore(name = "settings")

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookmarkViewModel: BookmarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        val dataStore = requireContext().dataStore
        val settingPreferences = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), settingPreferences)

        bookmarkViewModel = ViewModelProvider(this, factory)[BookmarkViewModel::class.java]

        bookmarkViewModel.getBookmarkedEvents().observe(viewLifecycleOwner) { /* Observe your data */ }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}