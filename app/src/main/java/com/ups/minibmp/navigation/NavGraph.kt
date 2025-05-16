package com.ups.minibmp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ups.minibmp.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth" // Flujo inicial de autenticación
    ) {
        // Flujo de Autenticación (tu lógica actual)
        authGraph(navController)

        // Flujo Principal de la App
        mainAppGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "login",
        route = "auth"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { needsTokenSetup ->
                    if (needsTokenSetup) {
                        navController.navigate("tokenSetup") {
                            popUpTo("login") { saveState = true }
                        }
                    } else {
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                },
                onTokenLoginClick = {
                    navController.navigate("tokenLogin") {
                        popUpTo("login") { saveState = true }
                    }
                }
            )
        }

        composable("tokenLogin") {
            TokenLoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("tokenSetup") {
            TokenSetupScreen(
                onSetupComplete = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.mainAppGraph(navController: NavHostController) {
    navigation(
        startDestination = "accounts",
        route = "main"
    ) {
        composable("accounts") {
            MainAppScreen(
                currentScreen = "accounts",
                onAccountsClick = { /* Ya estamos aquí */ },
                onTransactionsClick = {
                    navController.navigate("transactions") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTransferClick = {
                    navController.navigate("transfer") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                AccountsScreen()
            }
        }

        composable("transactions") {
            MainAppScreen(
                currentScreen = "transactions",
                onAccountsClick = {
                    navController.navigate("accounts") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTransactionsClick = { /* Ya estamos aquí */ },
                onTransferClick = {
                    navController.navigate("transfer") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                TransactionHistoryScreen()
            }
        }

        composable("transfer") {
            MainAppScreen(
                currentScreen = "transfer",
                onAccountsClick = {
                    navController.navigate("accounts") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTransactionsClick = {
                    navController.navigate("transactions") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTransferClick = { /* Ya estamos aquí */ }
            ) {
                TransferScreen()
            }
        }
    }
}