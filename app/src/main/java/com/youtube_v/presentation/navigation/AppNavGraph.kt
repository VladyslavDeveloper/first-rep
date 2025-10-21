package com.youtube_v.presentation.navigation


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.youtube_v.presentation.screens.WebViewScreen
import com.youtube_v.presentation.vm.WebViewVM


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "webViewScreen"
    ) {

        // MainMenu Screen
        composable("webViewScreen") {
            val viewModel: WebViewVM = viewModel()

            WebViewScreen(
                viewModel = viewModel,
                onBack = {}
            )
        }
    }
}
