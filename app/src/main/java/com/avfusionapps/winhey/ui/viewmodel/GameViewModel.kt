package com.avfusionapps.winhey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avfusionapps.winhey.data.models.GameState
import com.avfusionapps.winhey.data.models.Player

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
            gameCount = _gameState.value!!.gameCount
        )
    }

    fun completeGame() {
        _gameState.value = GameState()
    }
}
