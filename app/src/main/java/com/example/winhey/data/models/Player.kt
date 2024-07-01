package com.example.winhey.data.models

data class Player(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var accountBalance: Double = 0.0,
    var totalLost: Double = 0.0,
    var totalWon: Double = 0.0,
    var gameCount: Int = 0,
    var lossMargin: Double = 400.0,
    var winThreshold: Int = 30,
    var isBlocked: Boolean = false
)