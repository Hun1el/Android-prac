/**
 * Экран каталога товаров
 *
 * @author Солоников Антон
 * @date 17.12.2025
 */

package com.example.androidpracapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidpracapp.R
import com.example.androidpracapp.data.services.Product
import com.example.androidpracapp.ui.components.BackButton
import com.example.androidpracapp.ui.components.ProductCard
import com.example.androidpracapp.ui.components.ProductCardData
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.Text
import com.example.androidpracapp.ui.viewModel.CatalogViewModel
import com.example.androidpracapp.ui.viewModel.FavoriteViewModel

@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val favorites by favoriteViewModel.favorites.collectAsState()
    val favoriteIds = remember(favorites) { favorites.map { it.id }.toSet() }

    LaunchedEffect(Unit) {
        favoriteViewModel.loadFavorites()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp).padding(top = 60.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        BackButton(onClick = {
                            onBackClick()
                        },
                            backgroundColor = Block)
                    }

                    Text(
                        text = selectedCategory?.title ?: stringResource(R.string.category),
                        style = MaterialTheme.typography.titleLarge,
                        color = Text,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { index ->
                        val cat = categories[index]
                        val isSelected = selectedCategory?.id == cat.id

                        Box(
                            modifier = Modifier.height(40.dp).background(
                                color = if (isSelected) {
                                    Accent
                                } else {
                                    Block
                                },
                                shape = RoundedCornerShape(8.dp)
                            ).padding(horizontal = 20.dp).clickable { viewModel.selectCategory(cat) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.title,
                                color = if (isSelected) Block else Text,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).background(Background)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products.size) { index ->
                        val product = products[index]
                        val isFavorite = favoriteIds.contains(product.id)

                        ProductCard(
                            data = ProductCardData(
                                imageRes = R.drawable.air_max,
                                label = if (product.is_best_seller == true) {
                                    "BEST SELLER"
                                } else {
                                    ""
                                },
                                title = product.title,
                                price = "₽${product.cost.toInt()}",
                                isFavorite = isFavorite,
                                isInCart = false,
                                onFavoriteClick = {
                                    if (isFavorite) {
                                        favoriteViewModel.removeFromFavorites(product)
                                    } else {
                                        favoriteViewModel.addToFavorites(product)
                                    }
                                }
                            ),
                            modifier = Modifier.clickable { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}