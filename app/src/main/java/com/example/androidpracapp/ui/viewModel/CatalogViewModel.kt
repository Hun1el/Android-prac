package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.R
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.Category
import com.example.androidpracapp.data.services.CreateCartEntryRequest
import com.example.androidpracapp.data.services.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _cartProductIds = MutableStateFlow<Set<String>>(emptySet())

    private val cartService = RetrofitInstance.cartManagementService

    init {
        loadData()
        loadCartProductIds()
    }

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        return sharedPrefs.getString("userId", null)
    }

    fun loadCartProductIds() {
        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs() ?: return@launch
                val response = cartService.getCartItems(userId = "eq.$userId")
                if (response.isSuccessful) {
                    val cartIds = response.body()?.map { it.product_id }?.toSet() ?: emptySet()
                    _cartProductIds.value = cartIds
                    updateProductsCartStatus()
                }
            } catch (e: Exception) {
                _cartProductIds.value = emptySet()
            }
        }
    }

    private fun updateProductsCartStatus() {
        _products.value = _products.value.map { product ->
            product.copy(isInCart = _cartProductIds.value.contains(product.id))
        }
        _filteredProducts.value = _filteredProducts.value.map { product ->
            product.copy(isInCart = _cartProductIds.value.contains(product.id))
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val catResponse = RetrofitInstance.catalogManagementService.getCategories()
                if (catResponse.isSuccessful) {
                    val allString = getApplication<Application>().getString(R.string.all)

                    val allCategories = mutableListOf(Category("all", allString))
                    catResponse.body()?.let { allCategories.addAll(it) }
                    _categories.value = allCategories
                }

                val prodResponse = RetrofitInstance.catalogManagementService.getProducts()
                if (prodResponse.isSuccessful) {
                    val prods = prodResponse.body() ?: emptyList()
                    _products.value = prods
                    _filteredProducts.value = prods.map { product ->
                        product.copy(isInCart = _cartProductIds.value.contains(product.id))
                    }
                }
            } catch (e: Exception) {
                Log.e("Catalog", "Ошибка загрузки данных", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetCategory() {
        _selectedCategory.value = null
        _filteredProducts.value = _products.value
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        if (category.id == "all") {
            _filteredProducts.value = _products.value
        } else {
            _filteredProducts.value = _products.value.filter { it.category_id == category.id }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            val cat = _selectedCategory.value
            if (cat == null || cat.id == "all") {
                _filteredProducts.value = _products.value
            } else {
                _filteredProducts.value = _products.value.filter { it.category_id == cat.id }
            }
        } else {
            _filteredProducts.value = _products.value.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val entry = CreateCartEntryRequest(
                    user_id = userId,
                    product_id = product.id,
                    count = 1
                )

                val response = cartService.addToCart(entry)

                if (response.isSuccessful) {
                    _cartProductIds.value = _cartProductIds.value + product.id
                    updateProductsCartStatus()
                }
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error adding to cart", e)
            }
        }
    }

    fun refreshCartStatus() {
        loadCartProductIds()
    }
}