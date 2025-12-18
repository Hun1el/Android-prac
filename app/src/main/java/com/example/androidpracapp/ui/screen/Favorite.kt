package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.data.services.Product
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.components.BottomNavItem
import com.example.androidpracapp.ui.components.BottomNavigation
import com.example.androidpracapp.ui.components.ProductCard
import com.example.androidpracapp.ui.components.ProductCardData
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.AppTypography
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.Red
import com.example.androidpracapp.ui.theme.Text
import com.example.androidpracapp.ui.viewModel.FavoriteViewModel

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    selectedTabIndex: Int = 1,
    onTabSelected: (Int) -> Unit = {},
    onFabClick: () -> Unit = {}
) {
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    FavoriteScreenContent(
        favorites = favorites,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onFavoriteClick = { product ->
            viewModel.removeFromFavorites(product)
        },
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

@Composable
fun FavoriteScreenContent(
    favorites: List<Product>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: (Product) -> Unit,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 12.dp, bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackButton(
                    onClick = onBackClick,
                    backgroundColor = Block
                )

                Text(
                    text = stringResource(R.string.favorite),
                    style = AppTypography.headlineSmall,
                    color = Text
                )

                Box(
                    modifier = Modifier.size(44.dp).background(color = Block, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.favorite_fill),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Red
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = modifier.fillMaxSize().padding(padding).background(Background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
            } else if (favorites.isEmpty()) {
                Text(
                    text = stringResource(R.string.fav_empty),
                    modifier = Modifier.align(Alignment.Center),
                    color = Hint
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favorites.size) { index ->
                        val product = favorites[index]

                        ProductCard(
                            data = ProductCardData(
                                imageRes = R.drawable.air_max,
                                label = if (product.isBestSeller == true) {
                                    "BEST SELLER"
                                } else {
                                    ""
                                },
                                title = product.title,
                                price = "₽${product.cost.toInt()}",
                                isFavorite = true,
                                isInCart = false,
                                onFavoriteClick = { onFavoriteClick(product) }
                            )
                        )
                    }
                }
            }
        }
    }
}