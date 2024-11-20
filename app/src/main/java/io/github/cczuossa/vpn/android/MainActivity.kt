package io.github.cczuossa.vpn.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.page.AccountPage
import io.github.cczuossa.vpn.android.page.HomePage
import io.github.cczuossa.vpn.android.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                // 导航页面
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomePage(navController)
                    }
                    composable("account") {
                        AccountPage(navController)
                    }
                    composable("apps") {

                    }
                    composable("settings") {

                    }
                    composable("about") {

                    }
                }

            }
        }
    }
}
