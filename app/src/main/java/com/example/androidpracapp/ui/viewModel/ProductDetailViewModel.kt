package com.example.androidpracapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private val catalogService = RetrofitInstance.catalogManagementService

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val productsDeferred = async { catalogService.getProducts() }
                val categoriesDeferred = async { catalogService.getCategories() }

                val productsResponse = productsDeferred.await()
                val categoriesResponse = categoriesDeferred.await()

                if (productsResponse.isSuccessful && categoriesResponse.isSuccessful) {
                    val rawProducts = productsResponse.body() ?: emptyList()
                    val categories = categoriesResponse.body() ?: emptyList()

                    val mappedProducts = rawProducts.map { product ->
                        val catTitle = categories.find { it.id == product.category_id }?.title
                        product.copy(categoryName = catTitle)
                    }

                    _products.value = mappedProducts
                } else {
                    _error.value = "Ошибка загрузки данных"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка соединения: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
        _products.value = _products.value.map {
            if (it.id == product.id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}