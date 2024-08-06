package com.example.winhey.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.winhey.data.models.Common
import com.example.winhey.data.models.Constants
import com.example.winhey.data.models.Resource
import com.example.winhey.data.remote.firebase.FirebaseHelper

class MainViewModel(application: Application) : BaseViewModel(application) {

    private val _common = MutableLiveData<Resource<Common>>()
    val common: LiveData<Resource<Common>>
        get() = _common

    init {
        fetchCommonData()
    }

    fun fetchCommonData() {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.fetchCommonData(object : FirebaseHelper.FirebaseCallback<Common> {

                    override fun onSuccess(result: Common) {
                        _common.value = Resource.Success(result)
                    }

                    override fun onFailure(error: String) {
                        _common.value = Resource.Failure(error)
                    }
                })
            },
            failureAction = {
                _common.value = Resource.Failure(Constants.NO_INTERNET_ERROR)
            }
        )
    }

    fun updateCommonData(common: Common,) {
        checkInternetAndPerformAction(
            action = {
                FirebaseHelper.updateCommonData(
                    common,
                    object : FirebaseHelper.FirebaseCallback<Boolean> {

                        override fun onSuccess(result: Boolean) {
                            _common.value = Resource.Success(common)
                        }

                        override fun onFailure(error: String) {
                            _common.value = Resource.Failure(error)
                        }
                    })
            },
            failureAction = {
                _common.value = Resource.Failure(Constants.NO_INTERNET_ERROR)
            }
        )
    }
}