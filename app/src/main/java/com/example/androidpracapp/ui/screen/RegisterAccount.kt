package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.components.PrimaryButton
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.SubTextDark
import com.example.androidpracapp.ui.theme.Text

@Composable
fun RegisterAccountScreen(
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp).padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.sign_up1),
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
            text = stringResource(id = R.string.your_name),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            placeholder = { Text("xyz@gmail.com") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = Background,
                focusedLabelColor = Accent
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            label = { Text(stringResource(id = R.string.email)) },
            placeholder = { Text("xyz@gmail.com") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = Background,
                focusedLabelColor = Accent
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            label = { Text(stringResource(id = R.string.password)) },
            placeholder = { Text("xyz@gmail.com") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = Background,
                focusedLabelColor = Accent
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.policy_check),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Accent
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                color = Hint,
                text = stringResource(id = R.string.personal_data),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { agreedToTerms = !agreedToTerms }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(id = R.string.sign_up2),
            onClick = { },
            enabled = agreedToTerms
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { },
            modifier = Modifier.padding(bottom = 50.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Hint)) {
                        append("Есть аккаунт? ")
                    }
                    withStyle(style = SpanStyle(color = Text)) {
                        append("Войти")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun RegisterAccountScreenPreview() {
    RegisterAccountScreen()
}
