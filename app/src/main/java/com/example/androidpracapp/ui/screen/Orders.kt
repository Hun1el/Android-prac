package com.example.androidpracapp.ui.screens

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
import androidx.compose.ui.graphics.Color
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
import com.example.androidpracapp.ui.theme.Red
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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.order),
                        style = AppTypography.headlineSmall
                    )
                },
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize().background(Background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = "Ошибка: ${state.error}",
                    color = Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.sections.forEach { (header, items) ->
                        item {
                            Text(
                                text = header,
                                style = AppTypography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(items, key = { it.id }) { order ->
                            OrderSwipeItem(
                                order = order,
                                onRepeat = { viewModel.repeatOrder(order.id) },
                                onCancel = { viewModel.cancelOrder(order.id) },
                                onClick = {  }
                            )
                        }
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
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(color)
            )
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Block)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(60.dp).background(Color.Gray, RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("№${order.id}", style = AppTypography.labelMedium)
                    Text(order.title, style = AppTypography.labelMedium)
                    Text("₽${order.price}", style = AppTypography.labelMedium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = order.timeText,
                        color = Color.Gray,
                        style = AppTypography.labelMedium
                    )
                }
            }
        }
    }
}