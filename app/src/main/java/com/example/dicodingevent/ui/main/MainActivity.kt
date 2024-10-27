package com.example.dicodingevent.ui.main

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityMainBinding
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.ui.bookmark.BookmarkFragment
import com.example.dicodingevent.ui.event.FinishedFragment
import com.example.dicodingevent.ui.event.HomeFragment
import com.example.dicodingevent.ui.event.UpcomingFragment
import com.example.dicodingevent.ui.favorite.FavoriteFragment
import com.example.dicodingevent.ui.setting.SettingFragment
import com.example.dicodingevent.utils.DataStoreManager
import com.example.dicodingevent.utils.DataStoreViewModel
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun saveSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { settings ->
            settings[key] = value
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataStoreManager = DataStoreManager(this)
        val dataStoreViewModelFactory = DataStoreViewModel.Factory(dataStoreManager)
        dataStoreViewModel = ViewModelProvider(this, dataStoreViewModelFactory)[DataStoreViewModel::class.java]

        val pref = SettingPreferences.getInstance(this.dataStore)
        val factory = ViewModelFactory.getInstance(this, pref)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val bottomNavigation: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment? = when (item.itemId) {
                R.id.navigation_upcoming -> UpcomingFragment()
                R.id.navigation_finished -> FinishedFragment()
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_favorite -> FavoriteFragment()
                R.id.navigation_bookmark -> BookmarkFragment()
                else -> null
            }
            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        lifecycleScope.launch {
            pref.isReminderEnabled().collect { isReminderEnabled ->
                if (isReminderEnabled) {
                    Injection.setupDailyReminder(this@MainActivity)
                }
            }
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        setSupportActionBar(binding.toolbar)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                loadFragment(SettingFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}
