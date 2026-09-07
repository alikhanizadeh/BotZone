package com.example.botzone.SplashScreen


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.botzone.MainActivity
import com.example.botzone.R
import kotlinx.coroutines.delay



@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
){
    val context = LocalContext.current
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White

    val composition1 by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation1))
    val animState1 =  animateLottieCompositionAsState(composition = composition1,
        iterations = 1)

    // بررسی داشتن توکن برای ورود
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> onNavigateToHome()
            false -> onNavigateToLogin()
            null -> { /* در حال بررسی... */ }
        }
    }




    LaunchedEffect(animState1.progress) {
        if (animState1.progress == 1f) {
            delay(500)
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // انیمیشن اول
        LottieAnimation(
            composition = composition1,
            progress = { animState1.progress },
            modifier = Modifier.size(450.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingSplash() {

}