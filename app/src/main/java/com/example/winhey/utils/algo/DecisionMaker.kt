package com.example.winhey.utils.algo

import com.example.winhey.data.models.GameState
import com.example.winhey.data.models.ResultType
import kotlin.random.Random

object DecisionMaker {
    var lossMargin: Double = 400.0
    var winThreshold: Int = 30

    fun makeDecision(game: GameState): Boolean {
        val gameAmount = game.amount
        val answer = generateBooleanWithProbability()
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

    private fun generateBooleanWithProbability(): Boolean {
        val randomNumber = Random.nextInt(100)
        return randomNumber < winThreshold
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
