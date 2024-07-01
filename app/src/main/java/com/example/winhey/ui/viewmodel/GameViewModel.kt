package com.example.winhey.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.winhey.data.models.GameState
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.ResultType

class GameViewModel : ViewModel() {

    private val _gameState = MutableLiveData<GameState>().apply {
        value = GameState()
    }

    val gameState: LiveData<GameState> get() = _gameState

    fun joinGame(player: Player, amount: Double) {
        _gameState.value = _gameState.value?.copy(
            player = player,
            isPlaying = true,
            amount = amount,
            gameCount = _gameState.value!!.gameCount + 1
        )
    }

    fun completeGame() {
        _gameState.value = GameState()
    }
}
