package com.example.gym_app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.R
import com.example.gym_app.ui.theme.GymappTheme
import com.example.gym_app.viewModels.UserViewModel

@Composable
fun CreateAccountScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
  println("Recomposing screen...")
  Column(
      modifier =
          modifier
              .fillMaxSize()
              .background(
                  brush =
                      Brush.horizontalGradient(
                          colors =
                              listOf(
                                  Color(0xFF00d4ff),
                                  Color(0xFF0051bf),
                              ))),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text(
            text = "Create your account!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .padding(start = 20.dp, end = 20.dp, top = 80.dp),
            lineHeight = 30.sp)
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
                    .clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
                    .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              val useState by userViewModel.userState.collectAsStateWithLifecycle()
              Spacer(modifier = Modifier.weight(1f))

              TextField(
                  value = useState.firstName,
                  label = "First name",
                  onValueChange = { userViewModel.updateState(firstName = it) })
              TextField(
                  value = useState.lastName,
                  label = "Last name",
                  onValueChange = { userViewModel.updateState(lastName = it) })
              TextField(
                  value = useState.email,
                  label = "Last name",
                  onValueChange = { userViewModel.updateState(email = it) })
              PasswordTextField(
                  value = useState.password,
                  onValueChange = { userViewModel.updateState(password = it) })
            val userState by userViewModel.userState.collectAsState()

            PasswordConfirmationTextField(
                value = userState.confirmPassword,
                onValueChange = { newPassword ->
                    userViewModel.confirmPasswordDelayed({ password, confirmPassword ->
                        password == confirmPassword
                    }, newPassword)
                },
                error = if (userState.isError) "Passwords do not match" else ""
            )
              Button(onClick = { /*TODO*/}) { Text(text = "Create", fontSize = 6.em) }
              Spacer(modifier = Modifier.weight(1f))
            }
      }
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
) {
  OutlinedTextField(
      modifier = modifier.padding(bottom = 32.dp),
      value = value,
      label = { Text(text = label) },
      onValueChange = onValueChange,
  )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  OutlinedTextField(
      modifier = modifier.padding(bottom = 32.dp),
      value = value,
      onValueChange = onValueChange,
      visualTransformation =
          if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      label = { Text(text = "Password") },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          val iconId = if (passwordVisible) R.drawable.visibility_off else R.drawable.visibility
          val description = if (passwordVisible) "Hide password" else "Show password"
          Icon(painter = painterResource(id = iconId), contentDescription = description)
        }
      },
  )
}

@Composable
fun PasswordConfirmationTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    error: String,
) {
  var passwordVisible by rememberSaveable { mutableStateOf(false) }
  OutlinedTextField(
      modifier = modifier.padding(bottom = 32.dp),
      value = value,
      onValueChange = onValueChange,
      label = { Text(text = "Confirm password") },
      isError = error.isNotEmpty(),
      visualTransformation =
          if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          val iconId = if (passwordVisible) R.drawable.visibility_off else R.drawable.visibility
          val description = if (passwordVisible) "Hide password" else "Show password"
          Icon(painter = painterResource(id = iconId), contentDescription = description)
        }
      },
      supportingText = {
        if (error.isNotEmpty()) {
          Text(error)
        }
      })
}

@Composable
@Preview
fun CreateAccountScreenPreview() {
  GymappTheme { Surface { CreateAccountScreen(navController = rememberNavController()) } }
}
