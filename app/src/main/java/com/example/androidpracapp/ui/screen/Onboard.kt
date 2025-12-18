/**
 * Экран приветствия
 *
 * @author Солоников Антон
 * @date 16.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.androidpracapp.ui.theme.AppTypography
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.GradientBoardDark
import com.example.androidpracapp.ui.theme.GradientBoardLight
import com.example.androidpracapp.ui.theme.SubTextDark
import com.example.androidpracapp.ui.theme.SubTextLight
import com.example.androidpracapp.ui.theme.Text
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardPagerScreen(
    modifier: Modifier = Modifier,
    onOnboardComplete: () -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        count = 3,
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page -> val isSelected = pagerState.currentPage == page
        when (page) {
            0 -> OnboardScreen1(
                isSelected = isSelected,
                onStartClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
            1 -> OnboardScreen2(
                isSelected = isSelected,
                onNextClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
            2 -> OnboardScreen3(
                isSelected = isSelected,
                onStartClick = onOnboardComplete
            )
        }
    }
}

@Composable
fun OnboardScreen1(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onStartClick: () -> Unit = {}
) {
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) {
            1f
        } else {
            0f
        },
        animationSpec = tween(900)
    )

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Accent,
            GradientBoardLight,
            GradientBoardDark
        )
    )

    Column(
        modifier = modifier.fillMaxSize().background(gradientBrush).alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(75.dp))

        Text(
            text = stringResource(R.string.welcome),
            style = AppTypography.displaySmall,
            color = Block,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.onboard1),
            contentDescription = "Shoes",
            modifier = Modifier.fillMaxWidth().height(320.dp).offset(x = 10.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(width = 45.dp, height = 6.dp).background(Block, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 30.dp, 6.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 30.dp, 6.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(R.string.start),
            onClick = { onStartClick() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            style = AppTypography.labelMedium,
            textColor = Text,
            backgroundColor = Block
        )

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
fun OnboardScreen2(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onNextClick: () -> Unit = {}
) {
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) {
            1f
        } else {
            0f
        },
        animationSpec = tween(900)
    )

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Accent,
            GradientBoardLight,
            GradientBoardDark
        )
    )

    Column(
        modifier = modifier.fillMaxSize().background(gradientBrush).alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(75.dp))

        Image(
            painter = painterResource(id = R.drawable.onboard2),
            contentDescription = "Shoes",
            modifier = Modifier.fillMaxWidth().height(320.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            text = stringResource(R.string.journey),
            style = AppTypography.displayLarge,
            color = Block,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.collection),
            style = AppTypography.bodySmall,
            color = SubTextLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(width = 30.dp, height = 6.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 45.dp, 6.dp).background(Block, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 30.dp, 6.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(R.string.next),
            onClick = { onNextClick() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            style = AppTypography.labelMedium,
            textColor = Text,
            backgroundColor = Block
        )

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
fun OnboardScreen3(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onStartClick: () -> Unit = {}
) {
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) {
            1f
        } else {
            0f
        },
        animationSpec = tween(900)
    )

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Accent,
            GradientBoardLight,
            GradientBoardDark
        )
    )

    Column(
        modifier = modifier.fillMaxSize().background(gradientBrush).alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(75.dp))

        Image(
            painter = painterResource(id = R.drawable.onboard3),
            contentDescription = "Shoes",
            modifier = Modifier.fillMaxWidth().height(320.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            text = stringResource(R.string.strength),
            style = AppTypography.displayLarge,
            color = Block,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.plant),
            style = AppTypography.bodySmall,
            color = SubTextLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(52.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(width = 30.dp, height = 6.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 30.dp, 6.dp).background(SubTextDark, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier.size(width = 45.dp, 6.dp).background(Block, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(R.string.next),
            onClick = { onStartClick() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            style = AppTypography.labelMedium,
            textColor = Text,
            backgroundColor = Block
        )

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Preview
@Composable
private fun OnboardPagerScreenPreview() {
    OnboardPagerScreen()
}