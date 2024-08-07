package com.example.winhey.utils.algo

import android.util.Log
import com.example.winhey.data.models.GameState
import com.example.winhey.data.models.ResultType
import kotlin.random.Random

object DecisionMaker {
    var lossMargin: Double = 400.0
    var winThreshold: Int = 50
    private var gamesCount: Int = 0

    fun makeDecision(game: GameState): Boolean {
        val gameAmount = game.amount
        this.winThreshold = game.player?.winThreshold!!
        this.lossMargin = game.player.lossMargin
        this.gamesCount = game.player.gameCount
        val answer = if (gamesCount <= 5 && game.player.totalWon < 1000) {
            generateBooleanWithProbability(90)
        } else {
            generateBooleanWithProbability(winThreshold)
        }

        if (answer) {
            updateLossMargin(gameAmount, ResultType.WON)
        } else {
            updateLossMargin(gameAmount, ResultType.LOSS)
        }
        updateWinThreshold()
        return answer
    }

    private fun updateWinThreshold() {
        when (lossMargin) {
            in 3000.0..5000.0 -> winThreshold = 60
            in 2000.0..3000.0 -> winThreshold = 50
            in 1500.0..2000.0 -> winThreshold = 47
            in 1000.0..1500.0 -> winThreshold = 45
            in 500.0..1000.0 -> winThreshold = 42

            in 0.0..500.0 -> winThreshold = 40

            in -300.0..-1.0 -> winThreshold = 30
            in -1000.0..-300.0 -> winThreshold = 20
            in -1500.0..-1000.0 -> winThreshold = 15
            in -2000.0..-1500.0 -> winThreshold = 10
            in -2500.0..-2000.0 -> winThreshold = 5
            else -> winThreshold = if (winThreshold > 0.0) {
                50
            } else 1

        }
    }

    private fun generateBooleanWithProbability(threshold: Int = winThreshold): Boolean {
        val randomNumber = Random.nextInt(100)
        return randomNumber < threshold
    }

    private fun updateLossMargin(amount: Double, type: ResultType) {
        when (type) {
            ResultType.LOSS -> {
                lossMargin += amount
            }

            ResultType.WON -> {
                lossMargin -= amount
            }

            else -> {
            }
        }
    }
}
