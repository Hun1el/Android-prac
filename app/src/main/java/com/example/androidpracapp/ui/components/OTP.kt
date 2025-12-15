package com.example.androidpracapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Red
import com.example.androidpracapp.ui.theme.Text

@Composable
fun OTPInput(
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var otpCode by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            OTPField(
                value = otpCode.getOrNull(index)?.toString() ?: "",
                isSelected = selectedIndex == index,
                onValueChange = { newValue ->

                    if (newValue.isEmpty()) {
                        otpCode = otpCode.removeRange(index, index + 1)
                    } else if (newValue.all { it.isDigit() }) {
                        val newCode = otpCode.toMutableList()

                        if (index < newCode.size) {
                            newCode[index] = newValue[0]
                        } else {
                            newCode.add(newValue[0])
                        }
                        otpCode = newCode.joinToString("").take(6)

                        if (index < 5 && newValue.isNotEmpty()) {
                            selectedIndex = index + 1
                        }
                    }
                    onOtpChange(otpCode)
                },
                onFocus = { selectedIndex = index }
            )
        }
    }
}

@Composable
fun OTPField(
    value: String,
    isSelected: Boolean,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(46.dp).height(99.dp).background(
                color = Color(0xFFF7F7F9),
                shape = RoundedCornerShape(12.dp)
            ).border(
                width = 2.dp,
                color = if (isSelected) {
                    Red
                } else {
                    Background
                },
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Text
            ),
            modifier = Modifier.align(Alignment.Center)
        )

        if (value.isEmpty()) {
            Text(
                text = " ",
                fontSize = 16.sp,
                color = Text,
                textAlign = TextAlign.Center
            )
        }
    }
}
