package com.example.gym_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.gym_app.ui.theme.GymappTheme
import com.example.gym_app.viewModels.AuthViewModel
import org.gymapp.library.request.CreateUserRequest

class MainActivity : ComponentActivity() {

  private lateinit var account: Auth0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    account = Auth0("Ro2WqbNVwQMZIIYVNVX5POPqHK0EIcGH", "dev-jj2awpllib7dacna.us.auth0.com")

    val authViewModel: AuthViewModel by viewModels()

    setContent {
      GymappTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          GymApp(onLoginWithAuthClicked = { loginWithBrowser(authViewModel) })
        }
      }
    }
  }

  private fun loginWithBrowser(authViewModel: AuthViewModel) {
    WebAuthProvider.login(account)
        .withScheme("demo")
        .withAudience("https://dev-jj2awpllib7dacna.us.auth0.com/api/v2/")
        .withScope("openid profile email read:current_user update:current_user_metadata")
        .start(
            this,
            object : Callback<Credentials, AuthenticationException> {
              override fun onFailure(exception: AuthenticationException) {}

              override fun onSuccess(credentials: Credentials) {
                val accessToken = credentials.accessToken
                val idToken = credentials.idToken
                authViewModel.setToken(accessToken)

                println("access token is: ${authViewModel.accessToken.value}")
                println("id token is: $idToken")

                val jwt = JWT(idToken)

                val userId = jwt.getClaim("sub").asString()?.split("|")?.get(1).orEmpty()
                val email = jwt.getClaim("email").asString().orEmpty()
                val firstName = jwt.getClaim("given_name").asString().orEmpty()
                val lastName = jwt.getClaim("family_name").asString().orEmpty()
                val profilePicUrl = jwt.getClaim("picture").asString().orEmpty()

                val createUserRequest =
                    CreateUserRequest(
                        id = userId,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        profilePicUrl = profilePicUrl)

                authViewModel.createUser(createUserRequest)
              }
            })
  }
}
