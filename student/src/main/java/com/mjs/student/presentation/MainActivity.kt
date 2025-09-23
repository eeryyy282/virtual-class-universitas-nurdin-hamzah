package com.mjs.student.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.core.view.size
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mjs.student.R
import com.mjs.student.databinding.ActivityMainBinding
import com.mjs.student.di.studentModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(studentModule)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_home) {
                if (navController.currentDestination?.id != R.id.navigation_home) {
                    navController.popBackStack(R.id.navigation_home, false)
                }
                true
            } else {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menu = navView.menu
            for (i in 0 until menu.size) {
                val menuItem = menu[i]
                if (menuItem.itemId == destination.id) {
                    menuItem.isChecked = true
                    break
                }
            }
        }

        checkDarkMode()
    }

    private fun checkDarkMode() {
        mainActivityViewModel.getThemeSetting.observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
