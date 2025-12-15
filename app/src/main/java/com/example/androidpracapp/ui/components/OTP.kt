/**
 * Компонент OTP
 *
 * @author Солоников Антон
 * @date 15.12.2025
 */

package com.example.androidpracapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
    val focusRequesters = remember { List(6) { FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

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
                    if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                        if (newValue.isEmpty()) {
                            otpCode = otpCode.take(index)

                            if (index > 0) {
                                selectedIndex = index - 1
                                focusRequesters[index - 1].requestFocus()
                            }
                        } else {
                            val newCode = otpCode.toMutableList()

                            while (newCode.size < index) {
                                newCode.add('0')
                            }

                            if (index < newCode.size) {
                                newCode[index] = newValue[0]
                            } else {
                                newCode.add(newValue[0])
                            }
                            otpCode = newCode.joinToString("")

                            if (index < 5) {
                                selectedIndex = index + 1
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                        onOtpChange(otpCode)
                    }
                },
                onFocus = { selectedIndex = index },
                focusRequester = focusRequesters[index]
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
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(46.dp).height(99.dp).background(
                color = Background,
                shape = RoundedCornerShape(12.dp)
            ).border(
                width = 2.dp,
                color = if (isSelected) Red else Background,
                shape = RoundedCornerShape(12.dp)
            ).clickable { onFocus() },
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
                color = Text
            ),
            modifier = Modifier.focusRequester(focusRequester)
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