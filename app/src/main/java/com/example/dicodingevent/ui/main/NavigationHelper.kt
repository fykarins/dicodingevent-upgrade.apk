package com.example.dicodingevent.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.dicodingevent.R
import com.example.dicodingevent.ui.bookmark.BookmarkFragment
import com.example.dicodingevent.ui.event.FinishedFragment
import com.example.dicodingevent.ui.event.HomeFragment
import com.example.dicodingevent.ui.event.UpcomingFragment
import com.example.dicodingevent.ui.favorite.FavoriteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationHelper(private val fragmentManager: FragmentManager) {

    fun setupNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
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
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
