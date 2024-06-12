package com.example.winhey

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.winhey.ui.view.fragment.MoneyBottomSheetFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView


class MainActivity : AppCompatActivity() {

    private var selectedCard: MaterialCardView? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor =  ContextCompat.getColor(this, R.color.AppBarColor)
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
    }

    private fun rotateCard(cardView1: MaterialCardView, cardView2: MaterialCardView) {
        val animator1 = ObjectAnimator.ofFloat(cardView1, "rotationY", 0f, 360f)
        val animator2 = ObjectAnimator.ofFloat(cardView2, "rotationY", 0f, 360f)
        animator1.duration = 1000
        animator2.duration = 1000
        animator1.repeatCount = 3
        animator2.repeatCount = 3

        cardView1.setOnClickListener {
            selectedCard?.let {
                it.strokeColor = Color.TRANSPARENT
                it.strokeWidth = 0
            }

            selectedCard = cardView1
            cardView1.strokeColor = Color.parseColor("#060700")
            cardView1.strokeWidth = 4
        }

        cardView2.setOnClickListener {
            selectedCard?.let {
                it.strokeColor = Color.TRANSPARENT
                it.strokeWidth = 0
            }

            selectedCard = cardView2
            cardView2.strokeColor = Color.parseColor("#060700")
            cardView2.strokeWidth = 4
        }

//        animator1.addListener(object: Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator) {
//                binding.imageView1.setBackgroundResource(R.drawable.background_rotate_image)
//                binding.imageView2.setBackgroundResource(R.drawable.background_rotate_image)
//            }
//
//            override fun onAnimationEnd(animation: Animator) {
//                binding.imageView1.setBackgroundResource(R.drawable.background_dhoni)
//                binding.imageView2.setBackgroundResource(R.drawable.background_kohli)
//            }
//
//            override fun onAnimationCancel(animation: Animator) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onAnimationRepeat(animation: Animator) {
//            }
//
//        })

        animator1.start()
        animator2.start()
    }
}