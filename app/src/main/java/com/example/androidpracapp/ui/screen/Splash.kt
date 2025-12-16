/**
 * Экран при старте
 *
 * @author Солоников Антон
 * @date 16.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.theme.Accent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashComplete: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(3000)
        onSplashComplete()
    }

    Column(
        modifier = modifier.fillMaxSize().background(Accent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.size(135.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}