package com.example.gym_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gym_app.ui.theme.GymappTheme
import com.auth0.android.Auth0
import com.auth0.android.provider.WebAuthProvider

class MainActivity : ComponentActivity() {

  private lateinit var account: Auth0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    account = Auth0(
      "Ro2WqbNVwQMZIIYVNVX5POPqHK0EIcGH",
      "dev-jj2awpllib7dacna.us.auth0.com"
    )
    setContent {
      GymappTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          GymApp()
        }
      }
    }
  }
}
