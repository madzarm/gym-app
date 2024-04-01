package com.example.gym_app.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.gymapp.library.response.ExceptionResult
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

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

fun uriToBase64(context: Context, uri: Uri, maxWidth: Int = 250, maxHeight: Int = 250): String {
    val bitmap = if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }

    val scaleFactor = max(
        1.0f,
        max(bitmap.width / maxWidth.toFloat(), bitmap.height / maxHeight.toFloat())
    )

    val resizedBitmap = Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width / scaleFactor).toInt(),
        (bitmap.height / scaleFactor).toInt(),
        true
    )

    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    val imageBytes = outputStream.toByteArray()

    return Base64.encodeToString(imageBytes, Base64.DEFAULT)
}

fun extractDateAndTime(dateTimeStr: String?): Pair<String?, String?> {
    val dateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val date = dateTime.format(dateFormatter)
    val time = dateTime.format(timeFormatter)

    return date to time
}

fun formatDuration(durationStr: String?): String? {
    if (durationStr == null) return null

    val durationInt = durationStr.toInt()
    val hours = durationInt / 60
    val minutes = durationInt % 60

    return when {
        hours > 0 -> "${hours}h, ${minutes}m"
        else -> "${minutes}m"
    }
}