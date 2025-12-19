package com.example.androidpracapp.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.data.services.LocationService
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.components.MessageDialog
import com.example.androidpracapp.ui.components.PrimaryButton
import com.example.androidpracapp.ui.theme.*
import com.example.androidpracapp.ui.viewModel.Address
import com.example.androidpracapp.ui.viewModel.CheckoutViewModel
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onOrderSuccess: () -> Unit = {}
) {
    val contactInfo by viewModel.contactInfo.collectAsState()
    val address by viewModel.address.collectAsState()
    val cardNumber by viewModel.cardNumber.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val delivery by viewModel.delivery.collectAsState()
    val total by viewModel.total.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }

    var editingPhone by remember { mutableStateOf(false) }
    var editingEmail by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    var tempPhone by remember { mutableStateOf(contactInfo.phone) }
    var tempEmail by remember { mutableStateOf(contactInfo.email) }
    var tempAddress by remember { mutableStateOf(address) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                val coordinates = locationService.getCurrentLocation()
                if (coordinates != null) {
                    val newAddress = locationService.getAddressFromCoordinates(
                        coordinates.first,
                        coordinates.second
                    )
                    if (newAddress != null) {
                        viewModel.updateAddress(newAddress.fullAddress)
                        tempAddress = newAddress
                    }
                }
            }
        }
    }

    class CardNumberVisualTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val trimmed = if (text.text.length >= 16) text.text.substring(0, 16) else text.text
            val formatted = trimmed.chunked(4).joinToString(" ")
            val annotatedString = AnnotatedString(formatted)
            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset == 0) return 0
                    if (offset <= 4) return offset
                    if (offset <= 8) return offset + 1
                    if (offset <= 12) return offset + 2
                    return offset + 3
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset == 0) return 0
                    if (offset <= 4) return offset
                    if (offset == 5) return 4
                    if (offset <= 9) return offset - 1
                    if (offset == 10) return 8
                    if (offset <= 14) return offset - 2
                    if (offset == 15) return 12
                    return offset - 3
                }
            }
            return TransformedText(annotatedString, offsetMapping)
        }
    }

    LaunchedEffect(contactInfo) {
        tempPhone = contactInfo.phone
        tempEmail = contactInfo.email
    }

    LaunchedEffect(address) {
        tempAddress = address
    }

    if (showConfirmDialog) {
        MessageDialog(
            title = "Подтвердить заказ?",
            description = "Сумма к оплате: ₽${String.format("%.2f", total)}",
            onOk = {
                showConfirmDialog = false
                viewModel.placeOrder(onSuccess = {
                    showSuccessDialog = true
                })
            },
            onCancel = {
                showConfirmDialog = false
            },
            okButtonText = "Да",
            cancelButtonText = "Отмена",
            okButtonColor = Accent,
            cancelButtonColor = Red
        )
    }

    if (showSuccessDialog) {
        MessageDialog(
            title = "Вы успешно\nоформили заказ",
            description = "",
            icon = painterResource(id = R.drawable.celebration),
            onOk = {
                showSuccessDialog = false
                onOrderSuccess()
            },
            showButtons = true,
            okButtonText = "Вернуться к покупкам",
            onCancel = null,
            okButtonColor = Accent
        )
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(Block).padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.sub),
                        color = Hint,
                        style = AppTypography.bodySmall
                    )
                    Text(
                        text = "₽${String.format("%.2f", subtotal)}",
                        color = Text,
                        style = AppTypography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.delivery),
                        color = Hint,
                        style = AppTypography.bodySmall
                    )
                    Text(
                        text = "₽${String.format("%.2f", delivery)}",
                        color = Text,
                        style = AppTypography.bodySmall
                    )
                }

                Divider(modifier = Modifier.fillMaxWidth(), color = Background, thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.total),
                        color = Text,
                        style = AppTypography.bodySmall
                    )
                    Text(
                        text = "₽${String.format("%.2f", total)}",
                        color = Accent,
                        style = AppTypography.bodySmall
                    )
                }

                PrimaryButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        if (isFormValid) {
                            showConfirmDialog = true
                        }
                    },
                    height = 50.dp,
                    backgroundColor = if (isFormValid) {
                        Accent
                    } else {
                        Disable
                    },
                    textColor = Block,
                    style = AppTypography.labelMedium
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else {
            LazyColumn(
                modifier = modifier.padding(paddingValues).fillMaxSize().background(Background),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        BackButton(
                            onClick = onBackClick,
                            modifier = Modifier.align(Alignment.CenterStart),
                            backgroundColor = Block
                        )
                        Text(
                            text = stringResource(R.string.bucket),
                            color = Text,
                            style = AppTypography.headlineSmall,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.contact_info),
                        style = AppTypography.titleMedium,
                        color = Text,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }

                item {
                    Column {
                        ContactField(
                            label = stringResource(R.string.email),
                            value = if (editingEmail) {
                                tempEmail
                            } else {
                                contactInfo.email
                            },
                            isEditing = editingEmail,
                            onEditClick = {
                                if (editingEmail) {
                                    viewModel.updateContactInfo(
                                        contactInfo.phone,
                                        tempEmail
                                    )
                                }
                                editingEmail = !editingEmail
                            },
                            onValueChange = { tempEmail = it }
                        )
                        if (validationError != null) {
                            Text(
                                text = validationError!!,
                                color = Red,
                                style = AppTypography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }

                item {
                    ContactField(
                        label = stringResource(R.string.phone_number),
                        value = if (editingPhone) tempPhone else contactInfo.phone,
                        isEditing = editingPhone,
                        onEditClick = {
                            if (editingPhone) {
                                viewModel.updateContactInfo(
                                    tempPhone,
                                    contactInfo.email
                                )
                            }
                            editingPhone = !editingPhone
                        },
                        onValueChange = { tempPhone = it }
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.address),
                        style = AppTypography.titleMedium,
                        color = Text,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp)).background(Block).clickable {
                                val permission =
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    scope.launch {
                                        val coordinates =
                                            locationService.getCurrentLocation()
                                        if (coordinates != null) {
                                            val newAddress =
                                                locationService.getAddressFromCoordinates(
                                                    coordinates.first,
                                                    coordinates.second
                                                )
                                            if (newAddress != null) {
                                                viewModel.updateAddress(
                                                    newAddress.fullAddress
                                                )
                                                tempAddress = newAddress
                                            }
                                        }
                                    }
                                } else {
                                    locationPermissionLauncher.launch(permission)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.map),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }

                item {
                    if (editingAddress) {
                        OutlinedTextField(
                            value = tempAddress.fullAddress,
                            onValueChange = { tempAddress = Address(fullAddress = it) },
                            label = { Text(stringResource(R.string.address), color = Hint) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Block,
                                cursorColor = Accent,
                                focusedTextColor = Text,
                                unfocusedTextColor = Text
                            ),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        viewModel.updateAddress(tempAddress.fullAddress)
                                        editingAddress = false
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit_mark),
                                        contentDescription = null,
                                        tint = Accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(Block, RoundedCornerShape(8.dp)).padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.address),
                                    style = AppTypography.labelSmall,
                                    color = Hint
                                )
                                Text(
                                    text = address.fullAddress.ifEmpty { stringResource(R.string.home3) },
                                    style = AppTypography.bodySmall,
                                    color = Text,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            IconButton(
                                onClick = { editingAddress = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit),
                                    contentDescription = null,
                                    tint = Hint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.payment_method),
                        style = AppTypography.titleMedium,
                        color = Text,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }.take(16)
                            viewModel.updateCardNumber(filtered)
                        },
                        label = { Text(stringResource(R.string.card_number), color = Hint) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Block,
                            cursorColor = Accent,
                            focusedTextColor = Text,
                            unfocusedTextColor = Text
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("1234 5678 9012 3456", color = Hint) },
                        visualTransformation = CardNumberVisualTransformation(),
                        singleLine = true
                    )
                }


                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ContactField(
    label: String,
    value: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    if (isEditing) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Hint) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = Block,
                cursorColor = Accent,
                focusedTextColor = Text,
                unfocusedTextColor = Text
            ),
            keyboardOptions = if (label.contains("Email", ignoreCase = true)) {
                KeyboardOptions(keyboardType = KeyboardType.Email)
            } else {
                KeyboardOptions(keyboardType = KeyboardType.Phone)
            },
            trailingIcon = {
                IconButton(onClick = onEditClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_mark),
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth().background(Block, RoundedCornerShape(8.dp)).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = AppTypography.labelSmall,
                    color = Hint
                )
                Text(
                    text = value.ifEmpty { stringResource(R.string.home3) },
                    style = AppTypography.bodySmall,
                    color = Text,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = null,
                    tint = Hint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}