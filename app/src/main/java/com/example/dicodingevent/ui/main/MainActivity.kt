package com.example.dicodingevent.ui.main

import android.os.Bundle
import android.os.StrictMode
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityMainBinding
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.ui.event.FinishedFragment
import com.example.dicodingevent.ui.event.HomeFragment
import com.example.dicodingevent.ui.event.UpcomingFragment
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.example.dicodingevent.utils.dataStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel with preferences
        val pref = SettingPreferences.getInstance(application.dataStore)
        val factory = ViewModelFactory.getInstance(this, pref)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Setup theme switch
        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        // Setup Bottom Navigation
        val bottomNavigation: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_upcoming -> UpcomingFragment()
                R.id.navigation_finished -> FinishedFragment()
                R.id.navigation_home -> HomeFragment()
                else -> null
            }
            fragment?.let {
                loadFragment(it)
                true
            } ?: false
        }

        // Default fragment load
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Collect reminder setting
        lifecycleScope.launch {
            pref.isReminderEnabled().collect { isReminderEnabled ->
                if (isReminderEnabled) {
                    Injection.setupDailyReminder(this@MainActivity)
                }
            }
        }

        // StrictMode for detecting potential issues
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        // Set toolbar
        setSupportActionBar(binding.toolbar)

        // SearchView setup
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mainViewModel.searchEvents(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}