package org.gymapp.backend.security.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class FcmConfiguration {

    @Value("\${firebase.apiKey_path}")
    private val apiKeyPath: String? = null

    @PostConstruct
    fun initialize() {
        try {
            val serviceAccount = ClassPathResource(apiKeyPath ?: "./").inputStream

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}