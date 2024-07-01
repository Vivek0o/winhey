package com.example.winhey.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.data.remote.firebase.FirebaseHelper

class PlayerViewModel(
    application: Application,
    private val currentPlayerId: String
) : BaseViewModel(application) {
    private val _currentPlayer = MutableLiveData<Resource<Player>>()
    val currentPlayer: LiveData<Resource<Player>>
        get() = _currentPlayer

    init {
        fetchCurrentPlayer()
    }

    fun fetchCurrentPlayer() {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.fetchCurrentPlayerData(playerId = currentPlayerId, object :
                    FirebaseHelper.FirebaseCallback<Player> {
                    override fun onSuccess(result: Player) {
                        _currentPlayer.value = Resource.Success(result)
                    }

                    override fun onFailure(error: String) {
                        _currentPlayer.value = Resource.Failure(error)
                    }
                })
            },
            failureAction = {
                _currentPlayer.value = Resource.Failure(Constants.NO_INTERNET_ERROR)
            }
        )
    }

    fun updateCurrentPLayer(player: Player) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.updatePlayer(player, object : FirebaseHelper.FirebaseCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        _currentPlayer.value = Resource.Success(player)
                    }

                    override fun onFailure(error: String) {
                        _currentPlayer.value = Resource.Failure(message = error, data = player)
                        Toast.makeText(getApplication(), "Could not update Player data try again: $error", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            failureAction = {
                _currentPlayer.value = Resource.Failure(message = Constants.NO_INTERNET_ERROR, data = player)
                Toast.makeText(getApplication(), "Could not update Player data try again", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
