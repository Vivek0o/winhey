package com.example.winhey.data.repository

import android.content.Context
import com.example.winhey.data.remote.api.AvatarApiService
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class AvatarRepository(private val apiService: AvatarApiService, val context: Context) {

    suspend fun fetchRandomAvatar(): Result<String> {
        return try {
            val randomId = (1..1000).random().toString()
            val response = apiService.getAvatar(randomId)
            if (response.isSuccessful) {
                val file = saveImageLocally(response.body()!!, randomId)
                Result.success(file.absolutePath)
            } else {
                Result.failure(Throwable(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveImageLocally(body: ResponseBody, id: String): File {
        val file = File(context.cacheDir, "$id.png")
        val inputStream = body.byteStream()
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}