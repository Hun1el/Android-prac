package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
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
        Log.d("Orders", "loadOrders: Начинаем загрузку...")
        val context = getApplication<Application>().applicationContext

        val prefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val userId = prefs.getString("userId", null)

        Log.d("Orders", "loadOrders: Получен userId из SharedPreferences: '$userId'")

        if (userId == null) {
            val errorMsg = "Пользователь не найден (не залогинен)"
            Log.e("Orders", "loadOrders Error: $errorMsg")
            _uiState.update { it.copy(error = errorMsg) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d("Orders", "loadOrders: Запрос к репозиторию для userId=$userId")

            val result = repository.getOrdersHistory(userId)

            if (result.isSuccess) {
                val orders = result.getOrDefault(emptyList())
                Log.d("Orders", "loadOrders Success: Получено ${orders.size} заказов")

                if (orders.isNotEmpty()) {
                    Log.d("Orders", "First order raw: ${orders[0]}")
                }

                val sections = mapOrdersToSections(orders)
                Log.d("Orders", "loadOrders: Данные сгруппированы в ${sections.size} секций")

                _uiState.update { it.copy(isLoading = false, sections = sections) }
            } else {
                val exception = result.exceptionOrNull()
                val errorMsg = exception?.message ?: "Неизвестная ошибка"
                Log.e("Orders", "loadOrders Failure: $errorMsg", exception)

                _uiState.update {
                    it.copy(isLoading = false, error = errorMsg)
                }
            }
        }
    }

    fun repeatOrder(orderId: Long) {
        Log.d("Orders", "repeatOrder: Clicked for order $orderId")
        viewModelScope.launch {

        }
    }

    fun cancelOrder(orderId: Long) {
        Log.d("Orders", "cancelOrder: Clicked for order $orderId")
        viewModelScope.launch {
            Log.d("Orders", "cancelOrder: Refreshing list...")
            loadOrders()
        }
    }

    private fun mapOrdersToSections(orders: List<OrderWithItems>): Map<String, List<OrderUiItem>> {
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

                val timeText = if (isToday && minutesDiff < 60) {
                    "$minutesDiff" + "" + R.string.min_ago
                } else {
                    created.format(timeFormatter)
                }

                val firstItem = order.items.firstOrNull()
                if (firstItem == null) {
                    Log.w("Orders", "Order ${order.id} has no items!")
                }

                OrderUiItem(
                    id = order.id,
                    title = firstItem?.title ?: (R.string.order1.toString() + "" + "№${order.id}"),
                    price = firstItem?.coast ?: 0.0,
                    delivery = (order.delivery_coast ?: 0L).toDouble(),
                    timeText = timeText,
                    productId = firstItem?.product_id,
                    rawDate = created
                )
            } catch (e: Exception) {
                Log.e("Orders", "Error mapping order ${order.id}: ${e.message}")
                null
            }
        }.sortedByDescending { it.rawDate }

        return uiItems.groupBy { item ->
            val date = item.rawDate.toLocalDate()
            when {
                date == now.toLocalDate() -> R.string.recent.toString()
                date == now.minusDays(1).toLocalDate() -> R.string.yesterday.toString()
                else -> date.format(dateFormatter)
            }
        }
    }
}
