package com.example.dicodingevent.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.dicodingevent.R

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.preference, container, false)

        val themeToggleSwitch = view.findViewById<SwitchCompat>(R.id.switch_theme_toggle)
        val dailyReminderSwitch = view.findViewById<SwitchCompat>(R.id.switch_daily_reminder)

        val sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        themeToggleSwitch.isChecked = sharedPref.getBoolean("dark_mode", false)
        dailyReminderSwitch.isChecked = sharedPref.getBoolean("daily_reminder", false)

        themeToggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("daily_reminder", isChecked).apply()
            if (isChecked) {
                activateDailyReminder()
            } else {
                deactivateDailyReminder()
            }
        }

        return view
    }

    private fun activateDailyReminder() {
    }

    private fun deactivateDailyReminder() {
    }
}
