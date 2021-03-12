package com.friends.runchamp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.friends.runchamp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation_view.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_page -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_startFragment)
                    true
                }
                R.id.statistics_page -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_statisticsFragment)
                    true
                }
                else -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_settingsFragment)
                    true
                }
            }
        }
    }
}