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
        Log.d("Favorite", "getUserIdFromPrefs: $id")
        return id
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = getUserIdFromPrefs()
                if (userId != null) {
                    Log.d("Favorite", "Запрос избранного для userId: $userId")

                    val response = RetrofitInstance.favoriteManagementService.getFavorites(userId = "eq.$userId")

                    Log.d("Favorite", "Код ответа: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        Log.d("Favorite", "Тело: ${body.size}")

                        val mappedProducts = body.mapNotNull { it.products }
                        Log.d("Favorite", "${mappedProducts.size}")

                        _favorites.value = mappedProducts
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("Favorite", "Ошибка сервера: ${response.code()} Body: $errorBody")
                    }
                } else {
                    Log.e("Favorite", "UserId null")
                }
            } catch (e: Exception) {
                Log.e("Favorite", "Ошибка при загрузке: ${e.message}", e)
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
                    Log.e("Favorite", "Ошибка добавления: ${response.code()}")
                }
            } catch (e: Exception) {
                loadFavorites()
                Log.e("Favorite", "Ошибка добавления: ${e.message}")
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
                    Log.e("Favorite", "Ошибка удаления: ${response.code()}")
                }
            } catch (e: Exception) {
                loadFavorites()
                Log.e("Favorite", "Ошибка удаления: ${e.message}")
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val entry = com.example.androidpracapp.data.services.CreateCartEntryRequest(
                    user_id = userId,
                    product_id = product.id,
                    count = 1
                )

                val response = RetrofitInstance.cartManagementService.addToCart(entry)

                if (response.isSuccessful) {
                    val currentList = _favorites.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        currentList[index] = currentList[index].copy(isInCart = true)
                        _favorites.value = currentList
                    }
                } else {
                    Log.e("Favorite", "Ошибка добавления в корзину: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Favorite", "Ошибка добавления в корзину: ${e.message}")
            }
        }
    }
}