package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.R
import com.example.androidpracapp.data.repository.OrderRepository
import com.example.androidpracapp.domain.model.OrderWithItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class OrderUiItem(
    val id: Long,
    val title: String,
    val price: Double,
    val delivery: Double,
    val timeText: String,
    val productId: String?,
    val rawDate: OffsetDateTime
)

data class OrdersState(
    val sections: Map<String, List<OrderUiItem>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository()
    private val _uiState = MutableStateFlow(OrdersState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("Orders", "Init: OrdersViewModel created")
        loadOrders()
    }

    fun loadOrders() {
        val context = getApplication<Application>().applicationContext
        val prefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val userId = prefs.getString("userId", null)

        if (userId == null) {
            _uiState.update { it.copy(error = "Пользователь не найден") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getOrdersHistory(userId)

            if (result.isSuccess) {
                val orders = result.getOrDefault(emptyList())
                val sections = mapOrdersToSections(orders)
                _uiState.update { it.copy(isLoading = false, sections = sections) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Ошибка загрузки"
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    fun repeatOrder(orderId: Long) {
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            loadOrders()
        }
    }

    private fun mapOrdersToSections(orders: List<OrderWithItems>): Map<String, List<OrderUiItem>> {
        val context = getApplication<Application>()
        val now = OffsetDateTime.now()
        val zone = ZoneId.systemDefault()
        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val uiItems = orders.mapNotNull { order ->
            try {
                val createdRaw = order.created_at ?: return@mapNotNull null
                val created = OffsetDateTime.parse(createdRaw).atZoneSameInstant(zone).toOffsetDateTime()

                val minutesDiff = ChronoUnit.MINUTES.between(created, now)
                val isToday = created.toLocalDate() == now.toLocalDate()

                val minAgoString = try {
                    context.getString(R.string.min_ago)
                } catch (e: Exception) {
                    "мин назад"
                }

                val timeText = if (isToday && minutesDiff < 60) {
                    "$minutesDiff $minAgoString"
                } else {
                    created.format(timeFormatter)
                }

                val firstItem = order.items.firstOrNull()

                val orderString = try {
                    context.getString(R.string.order1)
                } catch (e: Exception) {
                    "Заказ"
                }

                val title = firstItem?.title ?: orderString

                OrderUiItem(
                    id = order.id,
                    title = title,
                    price = firstItem?.coast ?: 0.0,
                    delivery = (order.delivery_coast ?: 0L).toDouble(),
                    timeText = timeText,
                    productId = firstItem?.product_id,
                    rawDate = created
                )
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.rawDate }

        return uiItems.groupBy { item ->
            val date = item.rawDate.toLocalDate()
            when {
                date == now.toLocalDate() -> try { context.getString(R.string.recent) } catch(e:Exception){ "Недавние" }
                date == now.minusDays(1).toLocalDate() -> try { context.getString(R.string.yesterday) } catch(e:Exception){ "Вчера" }
                else -> date.format(dateFormatter)
            }
        }
    }
}