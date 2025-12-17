package com.example.androidpracapp.ui.screen

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.components.BottomNavigation
import com.example.androidpracapp.ui.components.BottomNavItem
import com.example.androidpracapp.ui.components.MessageDialog
import com.example.androidpracapp.ui.components.PrimaryButton
import com.example.androidpracapp.ui.theme.*
import com.example.androidpracapp.ui.viewModel.ProfileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    selectedTabIndex: Int = 3,
    onTabSelected: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    val profile by viewModel.profileState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    var selectedTab by remember { mutableStateOf(3) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var initialFirstName by remember { mutableStateOf("") }
    var initialLastName by remember { mutableStateOf("") }
    var initialAddress by remember { mutableStateOf("") }
    var initialPhone by remember { mutableStateOf("") }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var newPhotoBase64 by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            newPhotoBase64 = viewModel.processImageUri(context, tempImageUri!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(context)
    }

    LaunchedEffect(profile) {
        profile?.let {
            firstName = it.firstname ?: ""
            lastName = it.lastname ?: ""
            address = it.address ?: ""
            phone = it.phone ?: ""

            initialFirstName = firstName
            initialLastName = lastName
            initialAddress = address
            initialPhone = phone
        }
    }

    if (errorMessage != null) {
        MessageDialog(
            title = "Ошибка",
            description = errorMessage!!,
            onOk = { viewModel.clearError() }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                items = listOf(
                    BottomNavItem(R.drawable.home, "Home"),
                    BottomNavItem(R.drawable.favorite, "Favorite"),
                    BottomNavItem(R.drawable.orders, "Orders"),
                    BottomNavItem(R.drawable.profile, "Profile"),
                ),
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected,
                onFabClick = { },
                fabIconRes = R.drawable.shoping,
                modifier = Modifier.background(Background)
            )
        },
        containerColor = Block
    ) { padding ->
        if (isLoading && profile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else {
            Column(
                modifier = modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEditing) {
                            stringResource(R.string.edit)
                        } else {
                            stringResource(R.string.profile)
                        },
                        style = AppTypography.headlineSmall,
                        color = Text,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    if (!isEditing) {
                        Box(
                            modifier = Modifier.align(Alignment.CenterEnd).size(36.dp).clickable { viewModel.toggleEditMode() }.background(
                                shape = CircleShape,
                                color = Accent
                            ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit),
                                contentDescription = "Edit",
                                modifier = Modifier.size(24.dp),
                                tint = Block
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Аватар
                Box(contentAlignment = Alignment.Center) {
                    val bitmap = remember(profile?.photo, newPhotoBase64) {
                        val base64 = newPhotoBase64 ?: profile?.photo
                        if (base64 != null) {
                            try {
                                val bytes = Base64.decode(base64, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            null
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(96.dp).clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = "Placeholder",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(96.dp).clip(CircleShape)
                        )
                    }

                    if (isEditing) {
                        Box(
                            modifier = Modifier.size(96.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape).clickable {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val displayName = if (firstName.isBlank() && lastName.isBlank()) {
                    stringResource(R.string.fio)
                } else {
                    "${firstName} ${lastName}".trim()
                }

                Text(
                    text = displayName,
                    style = AppTypography.titleSmall,
                    color = Text
                )

                if (isEditing) {
                    Text(
                        text = stringResource(R.string.change_photo),
                        color = Accent,
                        style = AppTypography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp).clickable {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Штрих-код
                if (!isEditing) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(70.dp).background(Background, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(R.string.open),
                                color = Text,
                                style = AppTypography.labelSmall,
                                modifier = Modifier.rotate(-90f).padding(end = 8.dp)
                            )
                            Image(
                                painter = painterResource(R.drawable.barcode),
                                contentDescription = "Barcode",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.weight(1f).height(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Поля
                ProfileField(
                    label = stringResource(R.string.first_name),
                    value = firstName,
                    isEditable = isEditing,
                    showChangeIcon = isEditing && firstName != initialFirstName,
                    onValueChange = { firstName = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileField(
                    label = stringResource(R.string.last_name),
                    value = lastName,
                    isEditable = isEditing,
                    showChangeIcon = isEditing && lastName != initialLastName,
                    onValueChange = { lastName = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileField(
                    label = stringResource(R.string.address),
                    value = address,
                    isEditable = isEditing,
                    showChangeIcon = isEditing && address != initialAddress,
                    onValueChange = { address = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileField(
                    label = stringResource(R.string.phone_number),
                    value = phone,
                    isEditable = isEditing,
                    showChangeIcon = isEditing && phone != initialPhone,
                    onValueChange = { phone = it }
                )

                if (isEditing) {
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton(
                        text = if (isLoading) {
                            stringResource(R.string.load)
                        } else {
                            stringResource(R.string.save)
                        },
                        enabled = !isLoading,
                        textColor = Block,
                        onClick = {
                            viewModel.updateProfile(
                                context, firstName, lastName, address, phone, newPhotoBase64
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditable: Boolean,
    showChangeIcon: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = Text,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = value,
            onValueChange = { if (isEditable) onValueChange(it) },
            readOnly = !isEditable,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Background,
                unfocusedContainerColor = Background,
                disabledContainerColor = Background,
                focusedIndicatorColor = if (isEditable) {
                    Accent
                } else {
                    Transparent
                },
                unfocusedIndicatorColor = Transparent
            ),
            textStyle = AppTypography.labelMedium.copy(
                color = Text
            ),
            trailingIcon = if (showChangeIcon) {
                {
                    Icon(
                        painter = painterResource(R.drawable.edit_mark),
                        contentDescription = "Changed",
                        tint = Accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                null
            }
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
