/**
 * Компонент кнопки назад
 *
 * @author Солоников Антон
 * @date 15.12.2025
 */

package com.example.androidpracapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.theme.Background

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Background
) {
    Box(
        modifier = modifier.size(44.dp).background(
                color = backgroundColor,
                shape = CircleShape
            ).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Back",
            modifier = Modifier.size(24.dp),
            tint = com.example.androidpracapp.ui.theme.Text
        )
    }
}

@Preview
@Composable
private fun BackButtonPreview() {
    BackButton(onClick = {})
}