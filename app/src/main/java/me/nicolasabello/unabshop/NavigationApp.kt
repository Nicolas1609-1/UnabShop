package me.nicolasabello.unabshop

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import me.nicolasabello.unabshop.ui.HomeScreen
import me.nicolasabello.unabshop.LoginScreen
import me.nicolasabello.unabshop.RegisterScreen

@Composable
fun NavigationApp() {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    // Si ya hay usuario autenticado → home
    val startDestination = if (currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        // Pantalla de Login
        composable("login") {
            LoginScreen(
                onClickRegister = {
                    navController.navigate("register")
                },
                onSuccessfulLogin = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable("register") {
            RegisterScreen(
                onClickBack = {
                    navController.popBackStack()
                },
                onSuccesfullRegister = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Inicio (Home)
        composable("home") {
            HomeScreen(
                onClickLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
