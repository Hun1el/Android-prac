package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.UserProfile
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContactInfo(
    val phone: String = "",
    val email: String = ""
)

data class Address(
    val fullAddress: String = ""
) {
    fun toDisplayString(): String = fullAddress
}

data class CreateOrderRequest(
    val user_id: String,
    val total_amount: Double,
    val delivery_address: String,
    val phone: String,
    val email: String,
    val payment_method: String,
    val items: List<Map<String, Any>>
)

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val cartService = RetrofitInstance.cartManagementService
    private val catalogService = RetrofitInstance.catalogManagementService

    private val _contactInfo = MutableStateFlow(ContactInfo())
    val contactInfo = _contactInfo.asStateFlow()

    private val _address = MutableStateFlow(Address())
    val address = _address.asStateFlow()

    private val _paymentMethod = MutableStateFlow("Добавить")
    val paymentMethod = _paymentMethod.asStateFlow()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal = _subtotal.asStateFlow()

    private val _delivery = MutableStateFlow(60.20)
    val delivery = _delivery.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total = _total.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadCheckoutData()
        loadCartAndCalculate()
    }

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        return sharedPrefs.getString("userId", null)
    }

    private fun getEmailFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        return sharedPrefs.getString("userEmail", "")
    }

    private fun loadCartAndCalculate() {
        val userId = getUserIdFromPrefs() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cartDeferred = async { cartService.getCartItems(userId = "eq.$userId") }
                val productsDeferred = async { catalogService.getProducts() }

                val cartResponse = cartDeferred.await()
                val productsResponse = productsDeferred.await()

                if (cartResponse.isSuccessful && productsResponse.isSuccessful) {
                    val cartEntries = cartResponse.body() ?: emptyList()
                    val allProducts = productsResponse.body() ?: emptyList()

                    // Считаем сумму
                    val calculatedSubtotal = cartEntries.sumOf { entry ->
                        val product = allProducts.find { it.id == entry.product_id }
                        val price = product?.cost ?: 0.0
                        val count = entry.count ?: 1
                        price * count
                    }

                    _subtotal.value = calculatedSubtotal
                    _total.value = calculatedSubtotal + _delivery.value
                } else {
                    Log.e("CheckoutViewModel", "Ошибка загрузки корзины для расчета")
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Ошибка расчета суммы: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadCheckoutData() {
        val userId = getUserIdFromPrefs() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.profileManagementService.getUserProfile("eq.$userId")

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val profile = response.body()!![0]
                    updateDataFromProfile(profile)
                } else {
                    loadLocalData()
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Ошибка загрузки профиля: ${e.message}")
                loadLocalData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateDataFromProfile(profile: UserProfile) {
        val email = getEmailFromPrefs() ?: ""

        _contactInfo.value = ContactInfo(
            phone = profile.phone ?: "",
            email = email
        )

        _address.value = Address(
            fullAddress = profile.address ?: ""
        )
    }

    private fun loadLocalData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        val phone = sharedPrefs.getString("userPhone", "") ?: ""
        val email = getEmailFromPrefs() ?: ""

        _contactInfo.value = ContactInfo(phone = phone, email = email)

        val fullAddress = sharedPrefs.getString("userAddress", "") ?: ""

        _address.value = Address(fullAddress = fullAddress)
    }

    fun updateContactInfo(phone: String, email: String) {
        _contactInfo.value = ContactInfo(phone = phone, email = email)
        saveLocalData()
    }

    fun updateAddress(fullAddress: String) {
        _address.value = Address(fullAddress = fullAddress)
        saveLocalData()
    }

    fun updatePaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun refreshCheckoutData() {
        loadCheckoutData()
        loadCartAndCalculate()
    }

    private fun saveLocalData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        sharedPrefs.edit().apply {
            putString("userPhone", _contactInfo.value.phone)
            putString("userAddress", _address.value.fullAddress)
            apply()
        }
    }

    fun placeOrder(
        phone: String,
        email: String,
        address: Address,
        paymentMethod: String,
        total: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val orderRequest = CreateOrderRequest(
                    user_id = userId,
                    total_amount = total,
                    delivery_address = address.toDisplayString(),
                    phone = phone,
                    email = email,
                    payment_method = paymentMethod,
                    items = emptyList()
                )

                val response = RetrofitInstance.orderService.createOrder(orderRequest)

                if (response.isSuccessful) {
                    Log.d("Checkout", "Заказ создан успешно")
                    onSuccess()
                } else {
                    Log.e("Checkout", "Ошибка создания заказа: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Checkout", "Ошибка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}