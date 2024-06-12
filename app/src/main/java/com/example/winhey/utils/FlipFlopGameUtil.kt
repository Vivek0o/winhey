package com.example.winhey.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.widget.ImageView
import com.example.winhey.R
import com.google.android.material.card.MaterialCardView

class FlipFlopGameUtil {

    private var selectedCard: MaterialCardView? = null

    fun rotateCard(
        cardView1: MaterialCardView,
        cardView2: MaterialCardView,
        imageView1: ImageView,
        imageView2: ImageView
    ) {
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

        animator1.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                imageView1.setBackgroundResource(R.drawable.background_rotate_image)
                imageView2.setBackgroundResource(R.drawable.background_rotate_image)
            }

            override fun onAnimationEnd(animation: Animator) {
                imageView1.setBackgroundResource(R.drawable.background_dhoni)
                imageView2.setBackgroundResource(R.drawable.background_kohli)
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

    fun changeCardBackground(imageView1: ImageView, imageView2: ImageView) {
        imageView1.setBackgroundResource(R.drawable.background_removebg_preview)
        imageView2.setBackgroundResource(R.drawable.background_removebg_preview)
    }

}