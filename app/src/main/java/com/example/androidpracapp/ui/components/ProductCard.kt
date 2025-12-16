package com.example.androidpracapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.theme.Accent
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
    val onFavoriteClick: () -> Unit = {},
    val onAddClick: () -> Unit = {}
)

@Composable
fun ProductCard(
    data: ProductCardData,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(data.isFavorite) }

    Box(
        modifier = modifier.fillMaxWidth().shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ).background(
                color = Block,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(10.dp).height(150.dp).clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = data.imageRes),
                    contentDescription = data.title,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )

                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp).size(32.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(6.dp)
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
                        modifier = Modifier.size(18.dp),
                        tint = if (isFavorite) Red else {
                            Text
                        }
                    )
                }
            }

            Text(
                text = data.label,
                style = MaterialTheme.typography.labelSmall,
                color = Accent
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.title,
                style = MaterialTheme.typography.bodySmall,
                color = Hint
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.price,
                    style = MaterialTheme.typography.labelMedium,
                    color = Text
                )

                Box(
                    modifier = Modifier.size(40.dp).background(
                            color = Accent,
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        ).clickable { data.onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cart),
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(20.dp),
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
    Column(
        modifier = Modifier.background(Color(0xFFF7F7F9)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProductCard(
            data = ProductCardData(
                imageRes = R.drawable.air_max,
                label = "BEST SELLER",
                title = "Nike Air Max",
                price = "₽752.00",
                isFavorite = false
            )
        )

        ProductCard(
            data = ProductCardData(
                imageRes = R.drawable.air_max,
                label = "BEST SELLER",
                title = "Nike Air Max",
                price = "₽752.00",
                isFavorite = true
            )
        )
    }
}