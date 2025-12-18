/**
 * ViewModel деталей продукта
 *
 * @author Солоников Антон
 * @date 18.12.2025
 */

package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.NewFavorite
import com.example.androidpracapp.data.services.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ProductDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogService = RetrofitInstance.catalogManagementService
    private val favoriteService = RetrofitInstance.favoriteManagementService

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        return sharedPrefs.getString("userId", null)
    }

    fun loadData() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userId = getUserIdFromPrefs()

                val productsDeferred = async { catalogService.getProducts() }
                val categoriesDeferred = async { catalogService.getCategories() }

                val favoritesDeferred = if (userId != null) {
                    async { favoriteService.getFavorites(userId = "eq.$userId") }
                } else null

                val productsResponse = productsDeferred.await()
                val categoriesResponse = categoriesDeferred.await()
                val favoritesResponse = favoritesDeferred?.await()

                if (productsResponse.isSuccessful && categoriesResponse.isSuccessful) {
                    val rawProducts = productsResponse.body() ?: emptyList()
                    val categories = categoriesResponse.body() ?: emptyList()

                    val favoriteIds = if (favoritesResponse?.isSuccessful == true) {
                        favoritesResponse.body()?.mapNotNull { it.products?.id }?.toSet() ?: emptySet()
                    } else {
                        emptySet()
                    }

                    if (rawProducts.isEmpty()) {
                        _error.value = "Список товаров пуст"
                    } else {
                        val mappedProducts = rawProducts.map { product ->
                            val catTitle = categories.find { it.id == product.category_id }?.title
                            val isFav = favoriteIds.contains(product.id)

                            product.copy(categoryName = catTitle, isFavorite = isFav)
                        }
                        _products.value = mappedProducts
                    }
                } else {
                    _error.value = "Ошибка сервера: ${productsResponse.code()}"
                }
            } catch (e: IOException) {
                _error.value = "Ошибка сети. Проверьте интернет."
            } catch (e: Exception) {
                _error.value = "Произошла ошибка: ${e.localizedMessage}"
                Log.e("Detail", "Load error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
        if (_products.value.isEmpty()) {
            return
        }

        val wasFavorite = product.isFavorite
        _products.value = _products.value.map {
            if (it.id == product.id) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }

        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs()
                if (userId == null) {
                    revertFavoriteState(product)

                    return@launch
                }

                val response = if (!wasFavorite) {
                    favoriteService.addFavorite(NewFavorite(user_id = userId, product_id = product.id))
                } else {
                    favoriteService.deleteFavorite(userId = "eq.$userId", productId = "eq.${product.id}")
                }

                if (!response.isSuccessful) {
                    Log.e("Detail", "fav failed: ${response.code()}")
                    revertFavoriteState(product)
                }
            } catch (e: Exception) {
                Log.e("Detail", "fav error", e)
                revertFavoriteState(product)
            }
        }
    }

    private fun revertFavoriteState(product: Product) {
        _products.value = _products.value.map {
            if (it.id == product.id) {
                it.copy(isFavorite = product.isFavorite)
            } else {
                it
            }
        }
    }
}