package me.nicolasabello.unabshop

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import me.nicolasabello.unabshop.ui.theme.validateEmail
import me.nicolasabello.unabshop.ui.theme.validatePassword

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onClickRegister: () -> Unit = {},
    onSuccessfulLogin: () -> Unit = {}
) {
    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity

    var inputEmail by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔸 Logo UNAB restaurado
            Image(
                painter = painterResource(id = R.drawable.img_icon_unab),
                contentDescription = "Logo UNAB",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 16.dp)
            )

            // 🔸 Título
            Text(
                text = "Iniciar Sesión",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9900)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔸 Campo de correo
            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF666666)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    if (emailError.isNotEmpty()) Text(emailError, color = Color.Red)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔸 Campo de contraseña
            OutlinedTextField(
                value = inputPassword,
                onValueChange = { inputPassword = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Contraseña",
                        tint = Color(0xFF666666)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9900),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                ),
                supportingText = {
                    if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔸 Error de login
            if (loginError.isNotEmpty()) {
                Text(loginError, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }

            // 🔸 Botón de iniciar sesión
            Button(
                onClick = {
                    val emailValidation = validateEmail(inputEmail)
                    val passwordValidation = validatePassword(inputPassword)

                    val isValidEmail = emailValidation.first
                    val isValidPassword = passwordValidation.first
                    emailError = emailValidation.second
                    passwordError = passwordValidation.second

                    if (isValidEmail && isValidPassword) {
                        auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    onSuccessfulLogin()
                                } else {
                                    loginError = when (task.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrecta"
                                        is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo"
                                        else -> "Error al iniciar sesión. Intenta de nuevo"
                                    }
                                }
                            }
                    } else {
                        loginError = "Por favor, completa todos los campos correctamente"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
            ) {
                Text("Iniciar Sesión", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔸 Botón de registro
            TextButton(onClick = onClickRegister) {
                Text("¿No tienes una cuenta? Regístrate", color = Color(0xFFFF9900))
            }
        }
    }
}
