package com.example.gym_app.common

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.gymapp.library.response.ExceptionResult
import retrofit2.HttpException

fun base64StringToImageBitmap(base64String: String): ImageBitmap {
    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()
}

fun readErrorMessage(e: HttpException): String {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter = moshi.adapter(ExceptionResult::class.java)
    var errorMessage: String
    try {
        errorMessage = e.response()?.errorBody()?.string()?.let { json ->
            jsonAdapter.fromJson(json)?.message ?: "An unknown error occurred"
        } ?: "An unknown error occurred"
    } catch (jsonException: Exception) {
        errorMessage = "An error occurred while parsing the error message"
    }
    return errorMessage;
}