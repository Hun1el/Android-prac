package com.example.androidpracapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Hint

@Composable
fun MessageDialog(
    title: String,
    description: String,
    onOk: () -> Unit = {},
    onCancel: (() -> Unit)? = null,
    icon: Painter? = null,
    showButtons: Boolean = true
) {
    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier.fillMaxWidth(1f).clickable { if (!showButtons) onOk() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier.size(44.dp).background(
                                color = Accent,
                                shape = CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Hint,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (showButtons && (onCancel != null || onOk != null)) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (onCancel != null) {
                                Button(
                                    onClick = onCancel,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD8D8D8),
                                        contentColor = Color(0xFF707B81)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Отмена",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            Button(
                                onClick = onOk,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Accent,
                                    contentColor = Background
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "OK",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MessageDialogPreview() {
    MessageDialog(
        title = "Title",
        description = "[Description]",
        onOk = { },
        onCancel = { }
    )
}