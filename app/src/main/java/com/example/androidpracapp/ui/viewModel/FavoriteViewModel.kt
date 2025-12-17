package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

        return sharedPrefs.getString("userId", null)
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = getUserIdFromPrefs()

                if (userId != null) {
                    val response = RetrofitInstance.favoriteManagementService.getFavorites(
                        userId = "eq.$userId"
                    )

                    if (response.isSuccessful && response.body() != null) {
                        val wrappers = response.body()!!
                        _favorites.value = wrappers.mapNotNull { it.products }
                    } else {
                        Log.e("Favorite", "Ошибка сервера: ${response.code()} ${response.errorBody()?.string()}")
                    }
                } else {
                    Log.e("Favorite", "UserId is null")
                }

            } catch (e: Exception) {
                Log.e("Favorite", "Ошибка сети: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val response = RetrofitInstance.favoriteManagementService.deleteFavorite(
                    userId = "eq.$userId",
                    productId = "eq.${product.id}"
                )

                if (response.isSuccessful) {
                    val currentList = _favorites.value.toMutableList()
                    currentList.remove(product)
                    _favorites.value = currentList
                } else {
                    Log.e("Favorite", "Не удалось удалить: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("Favorite", "Ошибка при удалении: ${e.message}")
            }
        }
    }
}