package com.example.winhey.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.example.winhey.R
import com.example.winhey.databinding.FragmentColorPredictionGameBinding
import com.google.android.material.card.MaterialCardView

class FlipFlopGameUtil(
    private val cardView1: MaterialCardView,
    private val cardView2: MaterialCardView,
    private val imageView1: ImageView,
    private val imageView2: ImageView,
    private val binding: FragmentColorPredictionGameBinding
) {

    private var selectedCard: MaterialCardView? = null
    var selected: Boolean = false

    // Store original states
    private val originalStateCard1 = Pair(cardView1.strokeColor, cardView1.strokeWidth)
    private val originalStateCard2 = Pair(cardView2.strokeColor, cardView2.strokeWidth)

    // Store Clicked Item position
    private var tempWon = true


    fun rotateCard() {
        val animator1 = ObjectAnimator.ofFloat(cardView1, "rotationY", 0f, 360f)
        val animator2 = ObjectAnimator.ofFloat(cardView2, "rotationY", 0f, 360f)
        animator1.duration = 1000
        animator2.duration = 1000
        animator1.repeatCount = 3
        animator2.repeatCount = 3

        animator1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                imageView1.setBackgroundResource(R.drawable.background_rotate_image)
                imageView2.setBackgroundResource(R.drawable.background_rotate_image)
            }

            override fun onAnimationEnd(animation: Animator) {
                cardView1.isClickable = false
                cardView2.isClickable = false

                if (tempWon) {
                    imageView1.setBackgroundResource(R.drawable.king_kohli)
                    imageView2.setBackgroundResource(R.drawable.thala)
                    binding.resultWon.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.resultWon.visibility = View.GONE
                    }, 3000)
                } else {
                    imageView1.setBackgroundResource(R.drawable.thala)
                    imageView2.setBackgroundResource(R.drawable.king_kohli)
                    binding.resultLost.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.resultLost.visibility = View.GONE
                    }, 3000)
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                TODO("Not yet implemented")
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        animator1.start()
        animator2.start()
    }

    fun changeCardBackground() {
        cardView1.isClickable = true
        cardView2.isClickable = true
        imageView1.setBackgroundResource(R.drawable.background_removebg_preview)
        imageView2.setBackgroundResource(R.drawable.background_removebg_preview)
    }

    fun handleCardClick() {
        cardView1.setOnClickListener {
            updateCardSelection(cardView1, "#379F7C", 8)
        }

        cardView2.setOnClickListener {
            updateCardSelection(cardView2, "#379F7C", 8)
        }
    }

    private fun updateCardSelection(cardView: MaterialCardView, s: String, i: Int) {
        // Reset previously selected card
        selectedCard?.let {
            resetCardViews(it)
        }

        // Update current selected card
        selectedCard = cardView
        cardView.strokeColor = Color.parseColor(s)
        cardView.strokeWidth = i
        selected = true
    }

    private fun resetCardViews(cardView: MaterialCardView) {
        when (cardView) {
            cardView1 -> {
                cardView1.strokeColor = originalStateCard1.first
                cardView1.strokeWidth = originalStateCard1.second
            }

            cardView2 -> {
                cardView2.strokeColor = originalStateCard2.first
                cardView2.strokeWidth = originalStateCard2.second
            }
        }
    }
}
