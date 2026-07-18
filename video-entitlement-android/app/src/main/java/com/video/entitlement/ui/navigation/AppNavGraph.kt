package com.video.entitlement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.video.entitlement.data.repository.EntitleRepository
import com.video.entitlement.ui.screen.*

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val PLATFORM_DETAIL = "platform/{platformCode}"
    const val MY_ENTITLEMENTS = "my_entitlements"
    const val REDEEM = "redeem"
    const val PLAYER = "player/{platformCode}/{contentKey}"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val repo = EntitleRepository()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(repo = repo, onLoginSuccess = {
                navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                repo = repo,
                onPlatformClick = { code -> navController.navigate("platform/$code") },
                onMyEntitlements = { navController.navigate(Routes.MY_ENTITLEMENTS) },
                onRedeem = { navController.navigate(Routes.REDEEM) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable(Routes.PLATFORM_DETAIL) { backStackEntry ->
            val platformCode = backStackEntry.arguments?.getString("platformCode") ?: ""
            PlatformDetailScreen(
                platformCode = platformCode,
                repo = repo,
                onPlay = { code, key ->
                    navController.navigate("player/$code/$key")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MY_ENTITLEMENTS) {
            MyEntitlementsScreen(repo = repo, onBack = { navController.popBackStack() })
        }
        composable(Routes.REDEEM) {
            RedeemScreen(repo = repo, onBack = { navController.popBackStack() })
        }
        composable(Routes.PLAYER + "/{platformCode}/{contentKey}") { backStackEntry ->
            val platformCode = backStackEntry.arguments?.getString("platformCode") ?: ""
            val contentKey = backStackEntry.arguments?.getString("contentKey") ?: ""
            PlayerScreen(
                platformCode = platformCode,
                contentKey = contentKey,
                repo = repo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
