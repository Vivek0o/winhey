package com.example.winhey.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Player
import com.example.winhey.data.models.Resource
import com.example.winhey.data.remote.firebase.FirebaseHelper
import com.google.firebase.auth.FirebaseUser

class AdminViewModel(application: Application) : BaseViewModel(application) {

    private val _players = MutableLiveData<Resource<List<Player>?>>()
    val players: LiveData<Resource<List<Player>?>>
        get() = _players

    init {
        fetchAllPlayers()
    }

    fun fetchAllPlayers() {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.fetchAllPlayers(object :
                    FirebaseHelper.FirebaseCallback<List<Player>> {
                    override fun onSuccess(result: List<Player>) {
                        _players.value = Resource.Success(result)
                    }

                    override fun onFailure(error: String) {
                        _players.value = Resource.Failure(error, getCurrentPlayerListLocally())
                    }
                })
            },
            failureAction = {
                _players.value = Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentPlayerListLocally())
            }
        )
    }

    fun createPlayer(
        email: String,
        password: String,
        name: String = "",
        initialAmount: Double = 0.0
    ) {

        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.signUp(
                    email,
                    password,
                    object : FirebaseHelper.FirebaseCallback<FirebaseUser?> {
                        override fun onSuccess(result: FirebaseUser?) {
                            result?.let {
                                addPlayer(
                                    Player(
                                        id = it.uid,
                                        email = email,
                                        name = name,
                                        accountBalance = initialAmount
                                    )
                                )
                            }
                        }

                        override fun onFailure(error: String) {
                            Toast.makeText(
                                getApplication(),
                                "Could not create player Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            },
            failureAction = {
                _players.value = Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentPlayerListLocally())
            }
        )
    }

    fun addPlayer(player: Player) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.addPlayer(player, object : FirebaseHelper.FirebaseCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        val currentPlayers = getCurrentPlayerListLocally()
                        currentPlayers.add(player)
                        _players.value = Resource.Success(currentPlayers)
                    }

                    override fun onFailure(error: String) {
                        _players.value =
                            Resource.Failure(message = error, data = getCurrentPlayerListLocally())
                        Toast.makeText(
                            getApplication(),
                            "$error : Player created but could not add in database",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            },
            failureAction =  {
                _players.value = Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentPlayerListLocally())
            }
        )
    }

    fun updatePlayer(player: Player) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.updatePlayer(player, object : FirebaseHelper.FirebaseCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        val currentPlayers = getCurrentPlayerListLocally()
                        val index = currentPlayers.indexOfFirst { it.id == player.id }

                        if (index != -1) {
                            currentPlayers[index] = player
                            _players.value = Resource.Success(currentPlayers)
                            Toast.makeText(
                                getApplication(),
                                "Updated...",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            _players.value = Resource.Failure(
                                message = "Player could not found locally",
                                data = getCurrentPlayerListLocally()
                            )
                            Toast.makeText(
                                getApplication(),
                                "Player could not found locally",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(error: String) {
                        _players.value =
                            Resource.Failure(message = error, data = getCurrentPlayerListLocally())
                        Toast.makeText(
                            getApplication(),
                            "$error: Could not update player",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })
            },
            failureAction = {
                _players.value = Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentPlayerListLocally())
            }
        )

    }

    fun deletePlayer(playerId: String) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.deletePlayer(playerId, object : FirebaseHelper.FirebaseCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        val currentPlayers = getCurrentPlayerListLocally()
                        currentPlayers.removeAll { it.id == playerId }
                        _players.value = Resource.Success(currentPlayers)
                        Toast.makeText(
                            getApplication(),
                            "Deleted...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(error: String) {
                        _players.value =
                            Resource.Failure(message = error, data = getCurrentPlayerListLocally())
                        Toast.makeText(
                            getApplication(),
                            "$error: Could not delete player",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            },
            failureAction = {
                _players.value = Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentPlayerListLocally())
            }
        )

    }

    fun blockPlayer(player: Player) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.blockPlayer(player.id, object : FirebaseHelper.FirebaseCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        val currentPlayers = getCurrentPlayerListLocally()
                        val index = currentPlayers.indexOfFirst { it.id == player.id }
                        if (index != -1) {
                            currentPlayers[index].isBlocked = true
                            _players.value = Resource.Success(currentPlayers)
                            Toast.makeText(
                                getApplication(),
                                "Blocked...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                getApplication(),
                                "Player could not found locally",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(error: String) {
                        Toast.makeText(
                            getApplication(),
                            "$error: Could not block player",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            },
            failureAction = {
                _players.value = Resource.Failure(Constants.NO_INTERNET_ERROR, getCurrentPlayerListLocally())
            }
        )
    }

    private fun getCurrentPlayerListLocally() =
        if (_players.value != null && _players.value is Resource.Success) {
            (_players.value as Resource.Success<List<Player>>).data.toMutableList()
        } else mutableListOf()
}
