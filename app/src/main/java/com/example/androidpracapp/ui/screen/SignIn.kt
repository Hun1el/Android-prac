/**
 * Экран авторизации
 *
 * @author Солоников Антон
 * @date 15.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.example.androidpracapp.ui.theme.Text
import com.example.androidpracapp.ui.viewModel.SignInViewModel

// Экран авторизации
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = viewModel(),
    onSignUpClick: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.signInError.collectAsState().value
    val isSuccess = viewModel.signInSuccess.collectAsState().value
    val context = LocalContext.current

    // Функция для проверки корректности почты
    fun checkEmail(email: String): String? {
        val regex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")

        return when {
            email.isBlank() -> "Email не может быть пустым"
            !regex.matches(email) -> "Email должен соответствовать формату: yourmaillogin@domain.ru"
            else -> null
        }
    }

    // Функция для проверки пароля
    fun checkPassword(passwordValue: String): String? {
        return when {
            passwordValue.isBlank() -> "Пароль не может быть пустым"
            else -> null
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onSignInSuccess()
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showErrorDialog = true
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

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp).padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(onClick = { onBackClick() })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.hello),
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.enter_data),
            color = SubTextDark,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(54.dp))

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

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(id = R.string.password),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    "••••••••",
                    color = Hint,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible)
                                R.drawable.eye_open
                            else
                                R.drawable.eye_close
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Hint
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Background,
                unfocusedBorderColor = Background,
                focusedLabelColor = Accent
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onForgotPasswordClick() }
            ) {
                Text(
                    text = stringResource(R.string.restore),
                    color = Hint,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (isLoading) {
                "Загрузка..."
            } else {
                "Войти"
            },
            onClick = {
                viewModel.signIn(email, password, context)
            },
            enabled = !isLoading,
            style = MaterialTheme.typography.labelMedium,
            textColor = Background
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(bottom = 50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.create_user).split("? ")[0] + "? ",
                color = Hint,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = stringResource(R.string.create_user).split("? ")[1],
                color = Text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }
    }
}

@Preview
@Composable
private fun SignInScreenPreview() {
    SignInScreen()
}