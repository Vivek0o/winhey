package com.example.winhey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.winhey.data.models.GameState
import com.example.winhey.data.models.ResultType

class GameViewModel : ViewModel() {

    private val _gameState = MutableLiveData<GameState>().apply {
        value = GameState()
    }

    val gameState: LiveData<GameState> get() = _gameState

    fun joinGame(playerName: String, amount: Double) {
        _gameState.value = _gameState.value?.copy(
            player = playerName,
            isPlaying = true,
            statusMessage = "$playerName has joined the game!",
            amount = amount
        )
    }

    fun playGame() {
        _gameState.value = _gameState.value?.let { currentState ->
            currentState.copy(
                statusMessage = "${currentState.player} scored points!",
                amount = currentState.amount
            )
        }
    }

    fun completeGame(result: ResultType) {
        _gameState.value = _gameState.value?.copy(
            isPlaying = false,
            statusMessage = "${_gameState.value?.player} has ended the game with ${_gameState.value?.player} points!",
            result = result
        )
    }

    fun endGame() {
        _gameState.value = _gameState.value?.copy(
            isPlaying = false,
            statusMessage = "${_gameState.value?.player} has ended the game with ${_gameState.value?.player} points!"
        )
    }
}
