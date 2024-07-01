package com.example.winhey.data.models

data class GameState(
    val player: Player? = null,
    val isPlaying: Boolean = false,
    val result: ResultType = ResultType.NONE,
    val amount: Double = 0.0,
    var lossMargin: Double = 400.0,
    var winThreshold: Int = 30,
    var gameCount: Int = 0
)
