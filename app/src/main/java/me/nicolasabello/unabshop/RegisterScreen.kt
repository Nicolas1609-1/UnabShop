package me.nicolasabello.unabshop

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onClickBack: () -> Unit = {}, onSuccesfullRegister: () -> Unit = {}) {

    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity

    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var inputPasswordConfirmation by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordConfirmationError by remember { mutableStateOf("") }

    var registerError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_icon_unab),
                contentDescription = "Usuario",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Registro de Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9900)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, "Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    if (nameError.isNotEmpty()) Text(nameError, color = Color.Red)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    if (emailError.isNotEmpty()) Text(emailError, color = Color.Red)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputPassword,
                onValueChange = { inputPassword = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputPasswordConfirmation,
                onValueChange = { inputPasswordConfirmation = it },
                label = { Text("Confirmar Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, "Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    if (passwordConfirmationError.isNotEmpty())
                        Text(passwordConfirmationError, color = Color.Red)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (registerError.isNotEmpty()) {
                Text(registerError, color = Color.Red)
            }

            Button(
                onClick = {
                    val isValidName = validateName(inputName).first
                    val isValidEmail = validateEmail(inputEmail).first
                    val isValidPassword = validatePassword(inputPassword).first
                    val isValidPasswordConfirmation =
                        validateConfirmPassword(inputPassword, inputPasswordConfirmation).first

                    nameError = validateName(inputName).second
                    emailError = validateEmail(inputEmail).second
                    passwordError = validatePassword(inputPassword).second
                    passwordConfirmationError =
                        validateConfirmPassword(inputPassword, inputPasswordConfirmation).second

                    if (isValidName && isValidEmail && isValidPassword && isValidPasswordConfirmation) {
                        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    onSuccesfullRegister()
                                } else {
                                    registerError = when (task.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Correo inválido"
                                        is FirebaseAuthUserCollisionException -> "Correo ya registrado"
                                        else -> "Error al registrarse"
                                    }
                                }
                            }
                    } else {
                        registerError = "Verifica los campos e intenta de nuevo"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
            ) {
                Text("Registrarse", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

fun validateName(name: String): Pair<Boolean, String> {
    return if (name.isBlank()) Pair(false, "El nombre no puede estar vacío") else Pair(true, "")
}

fun validateEmail(email: String): Pair<Boolean, String> {
    return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        Pair(false, "Correo electrónico no válido")
    } else Pair(true, "")
}

fun validatePassword(password: String): Pair<Boolean, String> {
    return if (password.length < 6) Pair(false, "La contraseña debe tener al menos 6 caracteres")
    else Pair(true, "")
}

fun validateConfirmPassword(password: String, confirm: String): Pair<Boolean, String> {
    return if (password != confirm) Pair(false, "Las contraseñas no coinciden")
    else Pair(true, "")
}
