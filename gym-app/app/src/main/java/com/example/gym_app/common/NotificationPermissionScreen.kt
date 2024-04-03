package com.example.gym_app.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun NotificationPermissionScreen() {
    val context = LocalContext.current
    val permissionGranted = remember { mutableStateOf(false) }

    // Prepare the launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted.value = isGranted
    }

    // Check the permission status
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionGranted.value = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    } else {
        // Assume permission is granted on devices below Android 13
        permissionGranted.value = true
    }

    Column {
        if (!permissionGranted.value) {
            Text(text = "The app requires permission to send notifications.")
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }) {
                Text("Grant Permission")
            }
        } else {
            Text("Notification permission granted. Thank you!")
        }
    }
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        // Notification permission is not required to be checked before Android 13 (API level 33)
        true
    }
}