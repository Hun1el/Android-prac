package com.example.androidpracapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.AppTypography
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.Red
import com.example.androidpracapp.ui.theme.Text
import com.example.androidpracapp.ui.viewModel.OrderUiItem
import com.example.androidpracapp.ui.viewModel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().background(Background).padding(top = 10.dp, bottom = 10.dp)
            ) {
                Box(modifier = Modifier.padding(start = 20.dp).align(Alignment.CenterStart)) {
                    BackButton(
                        onClick = onBackClick,
                        backgroundColor = Block
                    )
                }
                
                Text(
                    text = stringResource(R.string.order),
                    style = AppTypography.headlineSmall,
                    color = Text,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize().background(Background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
            } else if (state.error != null) {
                Text(
                    text = "Ошибка: ${state.error}",
                    color = Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.sections.forEach { (header, items) ->
                        item {
                            Text(
                                text = header,
                                style = AppTypography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = Text
                            )
                        }
                        items(items, key = { it.id }) { order ->
                            OrderSwipeItem(
                                order = order,
                                onRepeat = { viewModel.repeatOrder(order.id) },
                                onCancel = { viewModel.cancelOrder(order.id) },
                                onClick = { }
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSwipeItem(
    order: OrderUiItem,
    onRepeat: () -> Unit,
    onCancel: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onRepeat()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onCancel()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                Accent
            } else {
                Red
            }

            Box(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(color).padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Alignment.CenterStart
                } else {
                    Alignment.CenterEnd
                }
            ) {}
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Block),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(85.dp).clip(RoundedCornerShape(16.dp)).background(Background),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.air_max),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "№${order.id}",
                        style = AppTypography.labelMedium,
                        color = Accent
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = order.title,
                        style = AppTypography.bodyMedium,
                        color = Text
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "₽${String.format("%.2f", order.price)}",
                        style = AppTypography.bodyMedium,
                        color = Text
                    )
                }

                Text(
                    text = order.timeText,
                    style = AppTypography.bodySmall,
                    color = Hint,
                    modifier = Modifier.align(Alignment.Top)
                )
            }
        }
    }
}