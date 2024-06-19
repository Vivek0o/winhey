package com.example.winhey.data.models

data class GameState(
    val player: String = "",
    val isPlaying: Boolean = false,
    val result: ResultType = ResultType.NONE,
    val statusMessage: String = "",
    val amount: Double = 0.0
)
