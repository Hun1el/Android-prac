package com.example.androidpracapp.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.data.services.LocationService
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.components.MessageDialog
import com.example.androidpracapp.ui.components.PrimaryButton
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.AppTypography
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.Red
import com.example.androidpracapp.ui.theme.Text
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
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val delivery by viewModel.delivery.collectAsState()
    val total by viewModel.total.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }

    var editingPhone by remember { mutableStateOf(false) }
    var editingEmail by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        viewModel.refreshCheckoutData()
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
                viewModel.placeOrder(
                    phone = contactInfo.phone,
                    email = contactInfo.email,
                    address = address,
                    paymentMethod = paymentMethod,
                    total = total,
                    onSuccess = onOrderSuccess
                )
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
                    onClick = { showConfirmDialog = true },
                    height = 50.dp,
                    backgroundColor = Accent,
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
                                viewModel.updateContactInfo(phone = contactInfo.phone, email = tempEmail)
                            }
                            editingEmail = !editingEmail
                        },
                        onValueChange = { tempEmail = it }
                    )
                }

                item {
                    ContactField(
                        label = stringResource(R.string.phone_number),
                        value = if (editingPhone) tempPhone else contactInfo.phone,
                        isEditing = editingPhone,
                        onEditClick = {
                            if (editingPhone) {
                                viewModel.updateContactInfo(phone = tempPhone, email = contactInfo.email)
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
                                val permission = Manifest.permission.ACCESS_FINE_LOCATION
                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
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
                    PaymentMethodSelector(
                        selectedMethod = paymentMethod,
                        onMethodSelected = { viewModel.updatePaymentMethod(it) }
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

@Composable
fun PaymentMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    val methods = listOf("Добавить", "Карта", "Наличные")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        methods.forEach { method ->
            Row(
                modifier = Modifier.fillMaxWidth().background(
                        if (selectedMethod == method) {
                            Accent
                        } else {
                            Block
                        },
                        RoundedCornerShape(8.dp)
                    ).clickable { onMethodSelected(method) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(20.dp).background(
                            if (selectedMethod == method) {
                                Block
                            } else {
                                Background
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedMethod == method) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_mark),
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Text(
                    text = method,
                    style = AppTypography.bodySmall,
                    color = if (selectedMethod == method) {
                        Block
                    } else {
                        Text
                    }
                )
            }
        }
    }
}
