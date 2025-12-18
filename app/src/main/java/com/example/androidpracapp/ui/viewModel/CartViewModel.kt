package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUIItem(
    val id: String,
    val product: Product,
    val count: Int
)

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartService = RetrofitInstance.cartManagementService
    private val catalogService = RetrofitInstance.catalogManagementService

    private val _cartItems = MutableStateFlow<List<CartUIItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal = _subtotal.asStateFlow()

    private val _deliveryCost = MutableStateFlow(60.20)
    val deliveryCost = _deliveryCost.asStateFlow()

    private val _totalCost = MutableStateFlow(0.0)
    val totalCost = _totalCost.asStateFlow()

    init {
        loadCart()
    }

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        return sharedPrefs.getString("userId", null)
    }

    fun loadCart() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = getUserIdFromPrefs()
                if (userId == null) {
                    _error.value = "Пользователь не авторизован"
                    return@launch
                }

                val cartDeferred = async { cartService.getCartItems(userId = "eq.$userId") }
                val productsDeferred = async { catalogService.getProducts() }

                val cartResponse = cartDeferred.await()
                val productsResponse = productsDeferred.await()

                if (cartResponse.isSuccessful && productsResponse.isSuccessful) {
                    val cartEntries = cartResponse.body() ?: emptyList()
                    val allProducts = productsResponse.body() ?: emptyList()

                    android.util.Log.d("CartViewModel", "Cart entries: ${cartEntries.size}")
                    android.util.Log.d("CartViewModel", "All products: ${allProducts.size}")

                    if (cartEntries.isEmpty()) {
                        _cartItems.value = emptyList()
                    } else {
                        val mappedItems = cartEntries.mapNotNull { entry ->
                            val product = allProducts.find { it.id == entry.product_id }
                            android.util.Log.d("CartViewModel", "Entry: ${entry.product_id}, Found: ${product?.title}")
                            if (product != null) {
                                CartUIItem(
                                    id = entry.id,
                                    product = product,
                                    count = entry.count ?: 1
                                )
                            } else {
                                null
                            }
                        }
                        _cartItems.value = mappedItems
                    }
                    calculateSummary()
                } else {
                    _error.value = "Ошибка сервера: ${cartResponse.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.localizedMessage}"
                android.util.Log.e("CartViewModel", "Error loading cart", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun increaseQuantity(item: CartUIItem) {
        viewModelScope.launch {
            try {
                val newCount = item.count + 1
                val response = cartService.updateCartItem(
                    id = "eq.${item.id}",
                    body = mapOf("count" to newCount)
                )

                if (response.isSuccessful) {
                    _cartItems.value = _cartItems.value.map {
                        if (it.id == item.id) it.copy(count = newCount) else it
                    }
                    calculateSummary()
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    fun decreaseQuantity(item: CartUIItem) {
        viewModelScope.launch {
            try {
                if (item.count > 1) {
                    val newCount = item.count - 1
                    val response = cartService.updateCartItem(
                        id = "eq.${item.id}",
                        body = mapOf("count" to newCount)
                    )

                    if (response.isSuccessful) {
                        _cartItems.value = _cartItems.value.map {
                            if (it.id == item.id) it.copy(count = newCount) else it
                        }
                        calculateSummary()
                    }
                } else {
                    deleteItem(item)
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    fun deleteItem(item: CartUIItem) {
        viewModelScope.launch {
            try {
                val response = cartService.deleteCartItem(id = "eq.${item.id}")
                if (response.isSuccessful) {
                    _cartItems.value = _cartItems.value.filter { it.id != item.id }
                    calculateSummary()
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    private fun calculateSummary() {
        val items = _cartItems.value
        val sum = items.sumOf { (it.product.cost ?: 0.0) * it.count }
        _subtotal.value = sum
        _totalCost.value = sum + _deliveryCost.value
    }
}