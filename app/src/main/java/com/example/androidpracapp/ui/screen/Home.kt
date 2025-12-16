/**
 * Главный экран
 *
 * @author Солоников Антон
 * @date 16.12.2025
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.components.BottomNavigation
import com.example.androidpracapp.ui.components.BottomNavItem
import com.example.androidpracapp.ui.components.ProductCard
import com.example.androidpracapp.ui.components.ProductCardData
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.AppTypography
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProductClick: (ProductCardData) -> Unit = {}
) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }

    val categories = listOf("Все", "Outdoor", "Tennis", "Running")
    val products = List(6) {
        ProductCardData(
            imageRes = R.drawable.air_max,
            label = "BEST SELLER",
            title = "Nike Air Max",
            price = "₽752.00",
//            isFavorite = it % 2 == 0,
//            isInCart = it % 3 == 0
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
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                onFabClick = {  },
                fabIconRes = R.drawable.shoping,
                modifier = Modifier.background(Background)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).background(Background)
        ) {
            // Верхняя панель и поиск
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(5.dp))

                // Заголовок и иконка меню
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.main),
                        style = AppTypography.displayMedium,
                        color = Text,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Поиск и Фильтр
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search),
                                color = Hint
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.marker_search),
                                contentDescription = "Search",
                                tint = Hint,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(14.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Block,
                            unfocusedContainerColor = Block,
                            disabledContainerColor = Block,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Box(
                        modifier = Modifier.size(52.dp).clickable { }.background(
                            shape = CircleShape,
                            color = Accent
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sliders),
                            contentDescription = "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Основной скроллящийся контент
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 24.dp,
                    bottom = 20.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Категории
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Text(
                            text = stringResource(R.string.category),
                            style = MaterialTheme.typography.titleMedium,
                            color = Text
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories.size) { index ->
                                val isSelected = selectedCategory == index
                                Box(
                                    modifier = Modifier.height(40.dp).width(108.dp).background(
                                            color = if (isSelected) {
                                                Accent
                                            } else {
                                                Block
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ).clickable { selectedCategory = index },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = categories[index],
                                        color = if (isSelected) {
                                            Block
                                        } else {
                                            Text
                                        },
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Популярное
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.popular),
                                style = MaterialTheme.typography.titleMedium,
                                color = Text
                            )

                            Text(
                                text = stringResource(R.string.all),
                                style = MaterialTheme.typography.labelMedium,
                                color = Accent,
                                modifier = Modifier.clickable { }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Сетка товаров
                items(products) { product ->
                    ProductCard(
                        data = product,
                        modifier = Modifier.clickable { onProductClick(product) }
                    )
                }

                // Акции
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.stock),
                                style = MaterialTheme.typography.titleMedium,
                                color = Text,
                            )

                            Text(
                                text = stringResource(R.string.all),
                                style = MaterialTheme.typography.labelMedium,
                                color = Accent,
                                modifier = Modifier.clickable { }
                            )
                        }

                        // Картинка акции
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sale),
                                contentDescription = "Sale",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(0.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}