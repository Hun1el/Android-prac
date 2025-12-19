/**
 * Экран корзины
 *
 * @author Солоников Антон
 * @date 18.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
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
import com.example.androidpracapp.ui.viewModel.CartUIItem
import com.example.androidpracapp.ui.viewModel.CartViewModel

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val deliveryCost by viewModel.deliveryCost.collectAsState()
    val totalCost by viewModel.totalCost.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val totalItemsCount = cartItems.size

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    subtotal = subtotal,
                    delivery = deliveryCost,
                    total = totalCost,
                    onCheckout = onCheckoutClick
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues).fillMaxSize().background(Background)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp).padding(horizontal = 20.dp)
            ) {
                BackButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                    backgroundColor = Block
                )

                Text(
                    text = stringResource(id = R.string.bucket),
                    color = Text,
                    style = AppTypography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            } else if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shoping),
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_cart1),
                            style = AppTypography.headlineSmall,
                            color = Text
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_cart2),
                            style = AppTypography.bodySmall,
                            color = Hint
                        )
                    }
                }
            } else {
                Text(
                    text = "$totalItemsCount" + " "+ stringResource(R.string.cart),
                    style = AppTypography.bodySmall,
                    color = Text,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    items(items = cartItems, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { viewModel.increaseQuantity(item) },
                            onDecrease = { viewModel.decreaseQuantity(item) },
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartUIItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        MessageDialog(
            title = "Удалить товар?",
            description = "Вы уверены, что хотите удалить ${item.product.title} из корзины?",
            onOk = {
                showDeleteDialog = false
                onDelete()
            },
            onCancel = {
                showDeleteDialog = false
            },
            okButtonText = "Да",
            cancelButtonText = "Нет",
            cancelButtonColor = Accent,
            okButtonColor = Red
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().height(140.dp).background(Block, RoundedCornerShape(16.dp)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)).background(Background),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.air_max),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.product.title,
                style = AppTypography.bodySmall,
                color = Text,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "₽${item.product.cost?.toInt() ?: 0}",
                style = AppTypography.labelMedium,
                color = Text
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Delete",
                    tint = Red,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier.size(28.dp).background(
                        if (item.count > 1) {
                            Accent
                        } else {
                            Hint
                        },
                        RoundedCornerShape(6.dp)
                    ).clickable(enabled = item.count > 1) {
                        if (item.count > 1) onDecrease()
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.minus),
                        contentDescription = "Decrease",
                        tint = Block,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = item.count.toString(),
                    style = AppTypography.bodySmall,
                    color = Text
                )

                Box(
                    modifier = Modifier.size(28.dp).background(Accent, RoundedCornerShape(6.dp)).clickable { onIncrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = "Increase",
                        tint = Block,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CartBottomBar(
    subtotal: Double,
    delivery: Double,
    total: Double,
    onCheckout: () -> Unit
) {
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

        Spacer(
            modifier = Modifier.fillMaxWidth().height(1.dp).background(Background).padding(vertical = 16.dp)
        )

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
            text = stringResource(R.string.checkout),
            onClick = onCheckout,
            height = 50.dp,
            backgroundColor = Accent,
            textColor = Block,
            style = AppTypography.labelMedium
        )
    }
}