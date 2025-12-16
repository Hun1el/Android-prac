/**
 * Компонент навигации
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidpracapp.R
import com.example.androidpracapp.ui.theme.Accent
import com.example.androidpracapp.ui.theme.Block
import com.example.androidpracapp.ui.theme.SubTextDark

data class BottomNavItem(
    val iconRes: Int,
    val label: String,
)

@Composable
fun BottomNavigation(
    items: List<BottomNavItem>,
    onFabClick: () -> Unit,
    fabIconRes: Int? = null,
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    containerColor: Color = Block,
    fabBackgroundColor: Color = Accent,
    iconColor: Color = SubTextDark
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp).background(
                    color = containerColor,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    if (index < items.size) {
                        Box(
                            modifier = Modifier.size(48.dp).clickable { onTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = items[index].iconRes),
                                contentDescription = items[index].label,
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    if (selectedTabIndex == index) Accent else iconColor
                                )
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.size(56.dp))

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    val itemIndex = index + 2
                    if (itemIndex < items.size) {
                        Box(
                            modifier = Modifier.size(48.dp).clickable { onTabSelected(itemIndex) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = items[itemIndex].iconRes),
                                contentDescription = items[itemIndex].label,
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    if (selectedTabIndex == itemIndex) Accent else iconColor
                                )
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.TopCenter).offset(y = (-28).dp).size(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape
                ).background(
                    color = fabBackgroundColor,
                    shape = CircleShape
                ).clickable { onFabClick() },
            contentAlignment = Alignment.Center
        ) {
            if (fabIconRes != null) {
                Image(
                    painter = painterResource(id = fabIconRes),
                    contentDescription = "FAB",
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

@Preview
@Composable
private fun BottomNavigationPreview() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val navItems = listOf(
        BottomNavItem(
            iconRes = R.drawable.home,
            label = "Home"
        ),
        BottomNavItem(
            iconRes = R.drawable.favorite,
            label = "Favorite"
        ),
        BottomNavItem(
            iconRes = R.drawable.orders,
            label = "Shipping"
        ),
        BottomNavItem(
            iconRes = R.drawable.profile,
            label = "Profile"
        )
    )

    Column(
        modifier = Modifier.background(Color.White).padding(bottom = 30.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        BottomNavigation(
            items = navItems,
            fabIconRes = R.drawable.shoping,
            onFabClick = { },
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it }
        )
    }
}
