package com.example.androidpracapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.data.services.Product
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.components.MessageDialog // Импорт вашего компонента
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.AppTypography
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.FavDetails
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.Red
import com.example.androidpracapp.ui.theme.Text
import com.example.androidpracapp.ui.viewModel.ProductDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    startProductId: String,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        if (products.isEmpty() && !isLoading && error == null) {
            viewModel.loadData()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sneaker Shop",
                        style = AppTypography.headlineSmall,
                        color = Text
                    )
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(start = 20.dp),
                        backgroundColor = Block
                    )
                },
                actions = {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.background(Block, CircleShape).size(44.dp).padding(end = 20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.shoping),
                            contentDescription = "Cart",
                            tint = Text,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = {
            if (products.isNotEmpty()) {

            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                error != null -> {
                    MessageDialog(
                        title = "Ошибка",
                        description = error ?: "Неизвестная ошибка",
                        icon = painterResource(id = R.drawable.email),
                        onOk = { viewModel.loadData() },
                        showButtons = true
                    )
                }

                products.isNotEmpty() -> {
                    ProductContent(
                        products = products,
                        startProductId = startProductId,
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductContent(
    products: List<Product>,
    startProductId: String,
    onFavoriteToggle: (Product) -> Unit
) {
    if (products.isEmpty()) {
        return
    }

    val initialIndex = remember(products) {
        val index = products.indexOfFirst { it.id == startProductId }
        if (index >= 0) {
            index
        } else {
            0
        }
    }

    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { products.size })
    val scope = rememberCoroutineScope()

    val currentProduct = products[pagerState.currentPage]

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Название товара
        Text(
            text = currentProduct.title,
            style = AppTypography.headlineLarge
        )

        // Категория
        Text(
            text = currentProduct.categoryName ?: "Shoes",
            style = AppTypography.titleLarge,
            color = Hint,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Цена
        Text(
            text = "₽${currentProduct.cost}",
            style = AppTypography.titleMedium,
            color = Text,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Картинка свайпа
        Box(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.podium),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(60.dp).offset(y = (-20).dp),
                contentScale = ContentScale.FillWidth
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().height(230.dp).padding(bottom = 20.dp), // Отступ снизу, чтобы "стоять" на подиуме
                contentPadding = PaddingValues(horizontal = 4.dp),
                verticalAlignment = Alignment.Bottom
            ) { page ->
                val item = products[page]
                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.air_max_big),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1.5f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ThumbnailsRow(
            products = products,
            selectedIndex = pagerState.currentPage,
            onSelect = { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )

        Spacer(modifier = Modifier.height(70.dp))

        // Описание
        ExpandableText(text = currentProduct.description)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка избранное
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(FavDetails).clickable { onFavoriteToggle(currentProduct) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (currentProduct.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = "Fav",
                    tint = if (currentProduct.isFavorite) {
                        Red
                    } else {
                        Text
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Кнопка в корзину
            Button(
                onClick = {  },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shoping),
                    contentDescription = null,
                    tint = Block,
                    modifier = Modifier.size(20.dp).padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.to_cart),
                    color = Block,
                    style = AppTypography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ThumbnailsRow(
    products: List<Product>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(products) { index, _ ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Block).border(
                    width = if (isSelected) {
                        2.dp
                    } else {
                        0.dp
                    },
                    color = if (isSelected) {
                        Accent
                    } else {
                        Transparent
                    },
                    shape = RoundedCornerShape(16.dp)
                ).clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.air_max),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp).fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun ExpandableText(text: String) {
    key(text) {
        var isExpanded by remember { mutableStateOf(false) }
        var isOverflowing by remember { mutableStateOf(false) }

        Column(modifier = Modifier.animateContentSize()) {
            Text(
                text = text,
                style = AppTypography.labelMedium,
                color = Hint,
                maxLines = if (isExpanded) {
                    Int.MAX_VALUE
                } else {
                    3
                },
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                        isOverflowing = true
                    }
                }
            )

            if (isOverflowing || isExpanded) {
                Text(
                    text = if (isExpanded) {
                        stringResource(R.string.desc_less)
                    } else {
                        stringResource(R.string.more)
                    },
                    color = Accent,
                    style = AppTypography.labelLarge,
                    modifier = Modifier.padding(top = 4.dp).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isExpanded = !isExpanded }.align(Alignment.End)
                )
            }
        }
    }
}