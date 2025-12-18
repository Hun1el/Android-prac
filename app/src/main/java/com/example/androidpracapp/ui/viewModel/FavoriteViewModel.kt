/**
 * ViewModel избранных товаров
 *
 * @author Солоников Антон
 * @date 17.12.2025
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteItemWrapper(
    val products: Product?
)

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadFavorites()
    }

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val id = sharedPrefs.getString("userId", null)
        Log.d("FavoriteVM", "getUserIdFromPrefs: $id")
        return id
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = getUserIdFromPrefs()
                if (userId != null) {
                    Log.d("FavoriteVM", "Запрос избранного для userId: $userId")

                    val response = RetrofitInstance.favoriteManagementService.getFavorites(userId = "eq.$userId")

                    Log.d("FavoriteVM", "Response Code: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        Log.d("FavoriteVM", "Raw Body Size: ${body.size}")

                        val mappedProducts = body.mapNotNull { it.products }
                        Log.d("FavoriteVM", "Mapped Products Size: ${mappedProducts.size}")

                        _favorites.value = mappedProducts
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("FavoriteVM", "Ошибка сервера: ${response.code()} Body: $errorBody")
                    }
                } else {
                    Log.e("FavoriteVM", "UserId is null, не можем загрузить")
                }
            } catch (e: Exception) {
                Log.e("FavoriteVM", "Exception при загрузке: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val currentList = _favorites.value.toMutableList()
                if (!currentList.any { it.id == product.id }) {
                    currentList.add(product)
                    _favorites.value = currentList
                }

                val response = RetrofitInstance.favoriteManagementService.addFavorite(
                    NewFavorite(user_id = userId, product_id = product.id)
                )

                if (!response.isSuccessful) {
                    loadFavorites()
                    Log.e("FavoriteVM", "Add failed: ${response.code()}")
                }
            } catch (e: Exception) {
                loadFavorites()
                Log.e("FavoriteVM", "Add error: ${e.message}")
            }
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val currentList = _favorites.value.toMutableList()
                currentList.removeAll { it.id == product.id }
                _favorites.value = currentList

                val response = RetrofitInstance.favoriteManagementService.deleteFavorite(
                    userId = "eq.$userId",
                    productId = "eq.${product.id}"
                )

                if (!response.isSuccessful) {
                    loadFavorites()
                    Log.e("FavoriteVM", "Delete failed: ${response.code()}")
                }
            } catch (e: Exception) {
                loadFavorites()
                Log.e("FavoriteVM", "Delete error: ${e.message}")
            }
        }
    }
}