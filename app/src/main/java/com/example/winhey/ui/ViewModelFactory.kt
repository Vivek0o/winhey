package com.example.winhey.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.winhey.ui.viewmodel.AdminViewModel
import com.example.winhey.ui.viewmodel.PlayerMoneyViewModel
import com.example.winhey.ui.viewmodel.PlayerViewModel

class AdminViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PlayerViewModelFactory(
    private val application: Application,
    private val currentPlayerId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(application, currentPlayerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PlayerMoneyViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerMoneyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerMoneyViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}