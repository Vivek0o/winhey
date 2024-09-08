package com.example.winhey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.winhey.data.models.Resource
import com.example.winhey.data.repository.AvatarRepository
import kotlinx.coroutines.launch

class AvatarViewModel(private val repository: AvatarRepository) : ViewModel() {

    // LiveData for multiple avatars
    private val _avatar1 = MutableLiveData<Resource<String>>()
    val userProfile: LiveData<Resource<String>> get() = _avatar1

    private val _avatar2 = MutableLiveData<Resource<String>>()
    val winnerFirst: LiveData<Resource<String>> get() = _avatar2

    private val _avatar3 = MutableLiveData<Resource<String>>()
    val winnnerSecond: LiveData<Resource<String>> get() = _avatar3

    private val _avatar4 = MutableLiveData<Resource<String>>()
    val winnerThird: LiveData<Resource<String>> get() = _avatar4

    init {
        // Fetch only if LiveData is not yet populated
        fetchAvatar(1, (1..1000).random()) // Avatar 1
        fetchAvatar(2, (1..1000).random()) // Avatar 2
        fetchAvatar(3, (1..1000).random()) // Avatar 3
        fetchAvatar(4, (1..1000).random()) // Avatar 4
    }

    // Generic function to fetch avatar by ID and position
    private fun fetchAvatar(position: Int, randomId: Int) {
        viewModelScope.launch {
            val liveData = getLiveDataForPosition(position)
            if (liveData?.value is Resource.Success) {
                // Avatar is already loaded, skip fetching
                return@launch
            }

            liveData?.value = Resource.Loading()

            // Check for cached avatar
            val cachedAvatar = getCachedAvatar(position)
            if (cachedAvatar != null) {
                liveData?.value = Resource.Success(cachedAvatar)
                return@launch
            }

            // Fetch new avatar from the network
            val result = repository.fetchRandomAvatar(randomId)
            liveData?.value = if (result.isSuccess) {
                val path = result.getOrNull()
                if (path != null) {
                    Resource.Success(path)
                } else {
                    Resource.Failure("Avatar path is null")
                }
            } else {
                Resource.Failure(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    // Helper function to get LiveData for a specific position
    private fun getLiveDataForPosition(position: Int): MutableLiveData<Resource<String>>? {
        return when (position) {
            1 -> _avatar1
            2 -> _avatar2
            3 -> _avatar3
            4 -> _avatar4
            else -> null
        }
    }

    // Function to retrieve cached avatars based on position
    private fun getCachedAvatar(position: Int): String? {
        val cacheDir = repository.context.cacheDir
        val files = cacheDir.listFiles { _, name -> name.startsWith("avatar_$position") && name.endsWith(".png") }
        return files?.firstOrNull()?.absolutePath
    }
}


class AvatarViewModelFactory(private val repository: AvatarRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvatarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AvatarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

