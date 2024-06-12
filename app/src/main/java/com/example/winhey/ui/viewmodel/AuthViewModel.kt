package com.example.winhey.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.winhey.data.models.AuthState
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Resource
import com.example.winhey.data.models.UserType
import com.example.winhey.data.remote.firebase.FirebaseHelper
import com.example.winhey.utils.WinHeyUtil.determineUserType
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : BaseViewModel(application) {

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    private val _authState = MutableLiveData<Resource<AuthState>>(Resource.Loading(AuthState()))
    val authState: LiveData<Resource<AuthState>> get() = _authState

    init {
        initializeAuthState()
    }

    private fun initializeAuthState() {
        try {
            _currentUser.value = FirebaseHelper.auth.currentUser
            _authState.value = _currentUser.value?.let {
                Resource.Success(AuthState(true, determineUserType(it.email)))
            } ?: Resource.Success(AuthState())
        } catch (e: Exception) {
            _authState.value = Resource.Failure(e.message ?: "Unknown error", AuthState())
        }
    }

    fun login(email: String, password: String) {
        val type = determineUserType(email)
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.login(email, password,
                    object : FirebaseHelper.FirebaseCallback<FirebaseUser?> {
                        override fun onSuccess(result: FirebaseUser?) {
                            _currentUser.value = result
                            _authState.value = Resource.Success(AuthState(true, type))
                        }

                        override fun onFailure(error: String) {
                            _authState.value = (Resource.Failure(error, AuthState()))
                        }
                    })
            },
            failureAction = {
                _authState.value = (Resource.Failure(Constants.NO_INTERNET_ERROR, AuthState()))
            }
        )
    }

    fun logout() {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.logout(object : FirebaseHelper.FirebaseCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        _currentUser.value = null
                        _authState.value = Resource.Success(AuthState(false, UserType.NONE))
                    }

                    override fun onFailure(error: String) {
                        _authState.value = Resource.Failure(
                            error,
                            AuthState(true, determineUserType(_currentUser.value?.email))
                        )
                    }
                })
            },

            failureAction = {
                _authState.value = Resource.Failure(
                    Constants.NO_INTERNET_ERROR,
                    AuthState(true, determineUserType(_currentUser.value?.email))
                )
            }
        )
    }
}
