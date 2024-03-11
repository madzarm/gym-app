package com.example.gym_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gym_app.ui.theme.GymappTheme


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GymappTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          GymApp()
        }
      }
    }
  }
}



//@Composable
//fun SignUpScreen(navController: androidx.navigation.NavController, apiService: ApiService) {
//  val username = rememberSaveable { mutableStateOf("") }
//  val password = rememberSaveable { mutableStateOf("") }
//  var message by remember { mutableStateOf<String?>("") }
//
//  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//    Text(text = "Sign Up")
//    OutlinedTextField(
//      value = username.value,
//      onValueChange = { username.value = it },
//      label = { Text("Username") }
//    )
//    OutlinedTextField(
//      value = password.value,
//      onValueChange = { password.value = it },
//      label = { Text("Password") },
//      visualTransformation = PasswordVisualTransformation(),
//      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//    )
//    Button(onClick = {
//      message = null // Reset message
//      CoroutineScope(Dispatchers.IO).launch {
//        val response = apiService.register(CreateAccountRequest(username.value, password.value, "ADMIN_ROLE"))
//        withContext(Dispatchers.Main) {
//          if (response.isSuccessful && response.code() == 201) {
//            navController.navigate("signIn")
//          } else {
//            message = response.errorBody()?.let { errorBody ->
//              // Assuming error response has a 'message' field
//              val errorResponse = Gson().fromJson(errorBody.charStream(), ExceptionResult::class.java)
//              errorResponse.message
//            } ?: "An unknown error occurred"
//          }
//        }
//      }
//    }) { Text(text = "Sign Up") }
//
//    if (message != null) {
//      Text(text = message!!)
//    }
//    Spacer(modifier = Modifier.height(8.dp))
//    Button(onClick = { navController.navigate("signUpInSelection") }) { Text(text = "Go back") }
//  }
//}
