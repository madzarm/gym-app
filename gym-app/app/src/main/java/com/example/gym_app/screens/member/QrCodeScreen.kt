package com.example.gym_app.screens.member

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.gym_app.common.TokenManager
import com.example.gym_app.common.createQRCodeContent
import com.example.gym_app.common.generateQRCode
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.SharedViewModel

@Composable
fun QeCodeScreen(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val jwt = TokenManager.getAccessToken(context) ?: ""
    val gymId = sharedViewModel.selectedGymUser.value?.gym?.id ?: ""
    val qrContent = createQRCodeContent(jwt, gymId)
    val qrCodeBitmap = generateQRCode(qrContent)

    CustomBackground(title = "Qr code") {
        QRCodeImage(qrCodeBitmap!!)
    }
}

@Composable
fun QRCodeImage(qrCodeBitmap: Bitmap) {
    Image(bitmap = qrCodeBitmap.asImageBitmap(), contentDescription = "QR Code")
}