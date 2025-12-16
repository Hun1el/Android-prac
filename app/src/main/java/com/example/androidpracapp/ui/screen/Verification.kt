/**
 * Экран OTP
 *
 * @author Солоников Антон
 * @date 15.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.components.MessageDialog
import com.example.androidpracapp.ui.components.OTPInput
import com.example.androidpracapp.ui.components.ResendCodeButton
import com.example.androidpracapp.ui.theme.SubTextDark
import com.example.androidpracapp.ui.viewModel.SignInViewModel

// Экран OTP
@Composable
fun VerificationScreen(
    modifier: Modifier = Modifier,
    email: String = "",
    viewModel: SignInViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    var otpCode by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.signInError.collectAsState().value
    val isSuccess = viewModel.signInSuccess.collectAsState().value
    val context = LocalContext.current

    // Диалог ошибки
    if (showErrorDialog && errorMessage != null) {
        MessageDialog(
            title = "Ошибка",
            description = errorMessage,
            onOk = {
                showErrorDialog = false
                viewModel.clearError()
            }
        )
    }

    // Обработка успеха
    if (isSuccess) {
        onSuccess()
    }

    // Обработка ошибки верификации
    LaunchedEffect(errorMessage) {
        if (errorMessage != null && otpCode.length == 6) {
            isError = true
            otpCode = ""
            showErrorDialog = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp).padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(0.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(onClick = { onBackClick() })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.otp),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.check_email2),
            color = SubTextDark,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(id = R.string.otp_code),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        OTPInput(
            onOtpChange = { code ->
                if (!isError) {
                    otpCode = code
                }
            },
            isError = isError
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResendCodeButton(
            email = email,
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun VerificationScreenPreview() {
    VerificationScreen()
}