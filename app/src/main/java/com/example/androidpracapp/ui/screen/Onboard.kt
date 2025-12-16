/**
 * Экран приветствия
 *
 * @author Солоников Антон
 * @date 16.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.components.PrimaryButton
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.GradientBoardDark
import com.example.androidpracapp.ui.theme.GradientBoardLight
import com.example.androidpracapp.ui.theme.SubTextDark
import com.example.androidpracapp.ui.theme.Text

@Composable
fun OnboardScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {}
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Accent,
            GradientBoardLight,
            GradientBoardDark
        )
    )

    Column(
        modifier = modifier.fillMaxSize().background(gradientBrush),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.displaySmall,
            color = Block,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(120.dp))

        Image(
            painter = painterResource(id = R.drawable.onboard1),
            contentDescription = "Shoes",
            modifier = Modifier.fillMaxWidth().height(320.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(width = 45.dp, height = 8.dp).background(Block, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 30.dp, 8.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 30.dp, 8.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.height(136.dp))

        PrimaryButton(
            text = stringResource(R.string.start),
            onClick = { onStartClick() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 50.dp),
            style = MaterialTheme.typography.labelMedium,
            textColor = Text,
            backgroundColor = Block
        )

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Preview
@Composable
private fun OnboardScreenPreview() {
    OnboardScreen()
}