/**
 * Компонент карточек товаров
 *
 * @author Солоников Антон
 * @date 16.12.2025
 */

package com.example.androidpracapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Background
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.Hint
import com.example.androidpracapp.ui.theme.Red
import com.example.androidpracapp.ui.theme.Text

data class ProductCardData(
    val imageRes: Int,
    val label: String,
    val title: String,
    val price: String,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val onFavoriteClick: () -> Unit = {},
    val onAddClick: () -> Unit = {}
)

@Composable
fun ProductCard(
    data: ProductCardData,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember(data.isFavorite) { mutableStateOf(data.isFavorite) }
    var isInCart by remember(data.isInCart) { mutableStateOf(data.isInCart) }

    Box(
        modifier = modifier.size(180.dp).shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            clip = false
        ).background(
            color = Block,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Column(
            modifier = Modifier.size(180.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth().weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = data.imageRes),
                        contentDescription = data.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(top = 10.dp).size(28.dp).clip(CircleShape)
                            .background(
                                color = Background,
                                shape = CircleShape
                            ).clickable {
                                isFavorite = !isFavorite
                                data.onFavoriteClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isFavorite) {
                                    R.drawable.favorite_fill
                                } else {
                                    R.drawable.favorite
                                }
                            ),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(14.dp),
                            tint = if (isFavorite) {
                                Red
                            } else {
                                Text
                            }
                        )
                    }
                }

                Text(
                    text = data.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Hint,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }


            Row(
                modifier = Modifier.height(30.dp).padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.price,
                    style = MaterialTheme.typography.labelSmall,
                    color = Text,
                    modifier = Modifier.padding(top = 1.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.fillMaxHeight().size(30.dp).background(
                        color = Accent,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 12.dp,
                            topEnd = 0.dp
                        )
                    ).clickable {
                        isInCart = !isInCart
                        data.onAddClick()
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isInCart) {
                                R.drawable.cart
                            } else {
                                R.drawable.add
                            }
                        ),
                        contentDescription = if (isInCart) {
                            "In Cart"
                        } else {
                            "Add"
                        },
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    Row(
        modifier = Modifier.background(Background).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductCard(
                data = ProductCardData(
                    imageRes = R.drawable.air_max,
                    label = "BEST SELLER",
                    title = "Nike Air Max",
                    price = "₽752.00",
                    isFavorite = false,
                    isInCart = false
                )
            )

            ProductCard(
                data = ProductCardData(
                    imageRes = R.drawable.air_max,
                    label = "BEST SELLER",
                    title = "Nike Air Max",
                    price = "₽752.00",
                    isFavorite = false,
                    isInCart = true
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductCard(
                data = ProductCardData(
                    imageRes = R.drawable.air_max,
                    label = "BEST SELLER",
                    title = "Nike Air Max",
                    price = "₽752.00",
                    isFavorite = true,
                    isInCart = false
                )
            )

            ProductCard(
                data = ProductCardData(
                    imageRes = R.drawable.air_max,
                    label = "BEST SELLER",
                    title = "Nike Air Max",
                    price = "₽752.00",
                    isFavorite = true,
                    isInCart = true
                )
            )
        }
    }
}