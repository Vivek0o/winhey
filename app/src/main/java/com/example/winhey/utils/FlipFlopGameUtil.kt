package com.example.winhey.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.example.winhey.R
import com.example.winhey.data.models.GameState
import com.example.winhey.data.models.ResultType
import com.example.winhey.databinding.FragmentColorPredictionGameBinding
import com.example.winhey.ui.view.fragment.AnimationState
import com.example.winhey.ui.view.fragment.GameFragment
import com.example.winhey.ui.viewmodel.GameViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel
import com.example.winhey.utils.algo.DecisionMaker
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.io.IOException

class FlipFlopGameUtil(
    private val cardView1: MaterialCardView,
    private val cardView2: MaterialCardView,
    private val imageView1: ImageView,
    private val imageView2: ImageView,
    private val context: Context,
    private val view: View?,
    private val binding: FragmentColorPredictionGameBinding,
    private val gameFragment: GameFragment
){

    private var selectedCard: MaterialCardView? = null
    private var mediaPlayer: MediaPlayer? = null
    var selected: Boolean = false

    // Store original states
    private val originalStateCard1 = Pair(cardView1.strokeColor, cardView1.strokeWidth)
    private val originalStateCard2 = Pair(cardView2.strokeColor, cardView2.strokeWidth)


    fun rotateCard(
        gameViewModel: GameViewModel,
        playerViewModel: PlayerViewModel,
        playButton: MaterialButton
    ) {
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
                playButton.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animator) {
                playButton.visibility = View.VISIBLE
                cardView1.isClickable = false
                cardView2.isClickable = false

                val decision = gameViewModel.gameState.value?.let {
                    DecisionMaker.makeDecision(it)
                } ?: return
                val gameState = gameViewModel.gameState.value ?: return

                if (decision) {
                    imageView1.setBackgroundResource(R.drawable.king_kohli)
                    imageView2.setBackgroundResource(R.drawable.thala)
                    handleMusic("game_won_audio.mp3")
                    binding.resultWon.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.resultWon.visibility = View.GONE
                    }, 3000)
                    gameViewModel.completeGame()
                    updatePlayer(gameState, ResultType.WON)
                } else {
                    imageView1.setBackgroundResource(R.drawable.thala)
                    imageView2.setBackgroundResource(R.drawable.king_kohli)
                    handleMusic("game_fail_audio.mp3")
                    binding.resultLost.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.resultLost.visibility = View.GONE
                    }, 3000)
                    gameViewModel.completeGame()
                    updatePlayer(gameState, ResultType.LOSS)
                }

                gameFragment.onAnimationEnd()
            }

            private fun updatePlayer(gameState: GameState, type: ResultType) {
                if (type == ResultType.WON) {
                    gameState.player?.let { playerViewModel.updateCurrentPLayer(it.copy(
                        accountBalance = it.accountBalance + gameState.amount,
                        totalWon = it.totalWon + gameState.amount,
                        gameCount = it.gameCount + 1,
                        lossMargin = DecisionMaker.lossMargin,
                        winThreshold = DecisionMaker.winThreshold
                    )) }
                } else {
                    gameState.player?.let { playerViewModel.updateCurrentPLayer(it.copy(
                        totalLost = it.totalLost + gameState.amount,
                        gameCount = it.gameCount + 1,
                        lossMargin = DecisionMaker.lossMargin,
                        winThreshold = DecisionMaker.winThreshold
                    )) }
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

    private fun handleMusic(audio: String) {
        try {
            mediaPlayer = MediaPlayer()
            val assetFileDescriptor = context?.assets?.openFd(audio)
            mediaPlayer?.setDataSource(
                assetFileDescriptor?.fileDescriptor,
                assetFileDescriptor?.startOffset ?: 0,
                assetFileDescriptor?.length ?: 0
            )

            mediaPlayer?.prepare()
            mediaPlayer?.start()
            mediaPlayer?.let {
                view?.postDelayed({
                    it.stop()
                    it.release()
                    mediaPlayer = null
                }, 4000)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
