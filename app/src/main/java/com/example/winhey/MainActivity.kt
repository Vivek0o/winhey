package com.example.winhey

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.winhey.data.models.UserType
import com.example.winhey.utils.WinHeyUtil
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.AppBarColor)
        bottomNavigation()
    }

    private fun bottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = ColorStateList.valueOf(resources.getColor(R.color.Black))
        navView.itemTextColor = ColorStateList.valueOf(resources.getColor(R.color.Black))
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            if (destination.id == R.id.playerFragment || destination.id == R.id.referralFragment || destination.id == R.id.profileFragment
            ) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id != R.id.playerFragment && WinHeyUtil.userType == UserType.PLAYER) {
                    navController.navigate(R.id.playerFragment)
                } else {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
        })
    }
}