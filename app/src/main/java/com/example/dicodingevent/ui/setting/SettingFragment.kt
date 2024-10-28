package com.example.dicodingevent.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dicodingevent.databinding.PreferenceBinding
import com.example.dicodingevent.utils.ReminderWorker
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private var _binding: PreferenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreferenceBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        binding.switchThemeToggle.isChecked = sharedPref.getBoolean("dark_mode", false)
        binding.switchDailyReminder.isChecked = sharedPref.getBoolean("daily_reminder", false)

        binding.switchThemeToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("daily_reminder", isChecked).apply()
            if (isChecked) {
                activateDailyReminder()
            } else {
                deactivateDailyReminder()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (binding.switchDailyReminder.isChecked) {
            activateDailyReminder()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun activateDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun deactivateDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
    }
}
