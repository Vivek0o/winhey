package com.example.winhey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.winhey.data.models.Resource
import com.example.winhey.data.repository.AvatarRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class AvatarViewModel(private val repository: AvatarRepository) : ViewModel() {

    private val _avatar = MutableLiveData<Resource<String>>()
    val avatar: LiveData<Resource<String>> get() = _avatar

    fun fetchAvatar(randomId: Int) {
        viewModelScope.launch {
            _avatar.value = Resource.Loading()

            // Check if there's a cached avatar
            val cachedAvatar = getCachedAvatar()
            if (cachedAvatar != null) {
                _avatar.value = Resource.Success(cachedAvatar)
                return@launch
            }

            // Fetch new avatar from the network
            val result = repository.fetchRandomAvatar(randomId)
            _avatar.value = if (result.isSuccess) {
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


    fun fetchAvatarForPerformer(randomId: Int) {
        viewModelScope.launch {
            _avatar.value = Resource.Loading()

            // Fetch new avatar from the network
            val result = repository.fetchRandomAvatar(randomId)
            _avatar.value = if (result.isSuccess) {
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

    private fun getCachedAvatar(): String? {
        val cacheDir = repository.context.cacheDir
        val files = cacheDir.listFiles { _, name -> name.endsWith(".png") }
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

