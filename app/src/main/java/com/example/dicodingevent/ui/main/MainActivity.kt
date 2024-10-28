package com.example.dicodingevent.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityMainBinding
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.ui.event.HomeFragment
import com.example.dicodingevent.ui.setting.SettingFragment
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.DataStoreViewModel
import com.example.dicodingevent.utils.PermissionHelper
import com.example.dicodingevent.utils.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var navigationHelper: NavigationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SettingPreferences instance
        val settingPreferences = SettingPreferences.getInstance(this)

        // Set up ViewModel with DataStoreViewModelFactory
        val dataStoreViewModelFactory = DataStoreViewModel.Factory(settingPreferences)
        dataStoreViewModel = ViewModelProvider(this, dataStoreViewModelFactory)[DataStoreViewModel::class.java]

        // Set up MainViewModel with ViewModelFactory
        val factory = ViewModelFactory.getInstance(this, settingPreferences)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Initialize navigation and permission helpers
        navigationHelper = NavigationHelper(supportFragmentManager)
        navigationHelper.setupNavigation(binding.navView)

        permissionHelper = PermissionHelper(this)
        permissionHelper.requestNotificationPermission()

        if (savedInstanceState == null) {
            navigationHelper.loadFragment(HomeFragment())
        }

        // Collect theme setting from SettingPreferences
        lifecycleScope.launch {
            dataStoreViewModel.darkMode.observe(this@MainActivity) { isDarkTheme ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        // Collect daily reminder setting from SettingPreferences
        lifecycleScope.launch {
            dataStoreViewModel.dailyReminder.observe(this@MainActivity) { isReminderEnabled ->
                if (isReminderEnabled) {
                    Injection.setupDailyReminder(this@MainActivity)
                }
            }
        }

        setSupportActionBar(binding.toolbar)
        setupSearchView()
    }

    private fun setupSearchView() {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.handlePermissionResult(requestCode, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navigationHelper.loadFragment(SettingFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
