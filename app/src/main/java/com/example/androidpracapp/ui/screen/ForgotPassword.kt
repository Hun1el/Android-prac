/**
 * Экран авторизации
 *
 * @author Солоников Антон
 * @date 15.12.2025
 */
/**
 * Экран восстановления пароля
 *
 * @author Солоников Антон
 * @date 15.12.2025
 */


package com.example.androidpracapp.ui.screen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.androidpracapp.ui.components.PrimaryButton
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.SubTextDark
import com.example.androidpracapp.ui.viewModel.SignInViewModel

// Экран восстановления пароля
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onNavigateToOTP: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.signInError.collectAsState().value
    val isSuccess = viewModel.signInSuccess.collectAsState().value
    val context = LocalContext.current

    // Функция для проверки email
    fun checkEmail(email: String): String? {
        val regex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")

        return when {
            email.isBlank() -> "Email не может быть пустым"
            !regex.matches(email) -> "Email должен соответствовать формату: yourmaillogin@domain.ru"
            else -> null
        }
    }

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
    LaunchedEffect(isSuccess) {
        if (isSuccess && email.isNotEmpty()) {
            onNavigateToOTP(email)
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showErrorDialog = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp).padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(onClick = { onBackClick() })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.forgot_password),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.zero),
            color = SubTextDark,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(38.dp))

        Text(
            text = stringResource(id = R.string.email),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    "xyz@gmail.com",
                    color = Hint,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Background,
                unfocusedBorderColor = Background,
                focusedLabelColor = Accent
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = stringResource(R.string.send),
            onClick = {
                val error = checkEmail(email)
                if (error != null) {
                    viewModel.signInError.value = error
                } else {
                    viewModel.sendPasswordResetCode(email, context)
                }
            },
            enabled = !isLoading && email.isNotEmpty(),
            style = MaterialTheme.typography.labelMedium,
            textColor = Background
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}