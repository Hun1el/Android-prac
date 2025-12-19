package com.example.androidpracapp.data.repository

import android.util.Log
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.domain.model.OrderWithItems

class OrderRepository {

    suspend fun getOrdersHistory(userId: String): Result<List<OrderWithItems>> {
        return try {
            val response = RetrofitInstance.ordersManagementService.getOrders(
                userId = "eq.$userId"
            )

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                Log.d("OrderRepo", "Загружено заказов: ${orders.size}")
                Result.success(orders)
            } else {
                val error = response.errorBody()?.string()
                Log.e("OrderRepo", "Ошибка загрузки: $error")
                Result.failure(Exception(error ?: "Ошибка HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("OrderRepo", "Исключение: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun cancelOrder(orderId: Long): Result<Unit> {
        return Result.success(Unit)
    }
}