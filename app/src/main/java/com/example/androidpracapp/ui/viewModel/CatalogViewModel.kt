/**
 * ViewModel каталога
 *
 * @author Солоников Антон
 * @date 17.12.2025
 */

package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.R
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.Category
import com.example.androidpracapp.data.services.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // Отфильтрованные товар
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Категории
                val catResponse = RetrofitInstance.catalogManagementService.getCategories()
                if (catResponse.isSuccessful) {
                    val allString = getApplication<Application>().getString(R.string.all)

                    val allCategories = mutableListOf(Category("all", allString))
                    catResponse.body()?.let { allCategories.addAll(it) }
                    _categories.value = allCategories
                }

                // Товары
                val prodResponse = RetrofitInstance.catalogManagementService.getProducts()
                if (prodResponse.isSuccessful) {
                    val prods = prodResponse.body() ?: emptyList()
                    _products.value = prods
                    _filteredProducts.value = prods
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
}