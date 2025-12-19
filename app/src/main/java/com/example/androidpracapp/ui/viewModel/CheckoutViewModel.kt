package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContactInfo(
    val phone: String = "",
    val email: String = ""
)

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

    private val _contactInfo = MutableStateFlow(ContactInfo())
    val contactInfo = _contactInfo.asStateFlow()

    private val _address = MutableStateFlow("")
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

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    init {
        loadUserData()
        calculateCart()
    }

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        return sharedPrefs.getString("userId", null)
    }

    private fun loadUserData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        val phone = sharedPrefs.getString("userPhone", "") ?: ""
        val email = sharedPrefs.getString("userEmail", "") ?: ""
        val address = sharedPrefs.getString("userAddress", "") ?: ""

        _contactInfo.value = ContactInfo(phone = phone, email = email)
        _address.value = address
    }

    private fun calculateCart() {
        _subtotal.value = 2000.0
        _total.value = _subtotal.value + _delivery.value
    }

    fun updateContactInfo(phone: String, email: String) {
        _contactInfo.value = ContactInfo(phone = phone, email = email)
        saveUserData()
    }

    fun updateAddress(newAddress: String) {
        _address.value = newAddress
        saveUserData()
    }

    fun updatePaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun updateLocation(location: LatLng) {
        _selectedLocation.value = location
        _address.value = "Координаты: ${location.latitude}, ${location.longitude}"
        saveUserData()
    }

    private fun saveUserData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        sharedPrefs.edit().apply {
            putString("userPhone", _contactInfo.value.phone)
            putString("userEmail", _contactInfo.value.email)
            putString("userAddress", _address.value)
            apply()
        }
    }

    fun placeOrder(
        phone: String,
        email: String,
        address: String,
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
                    delivery_address = address,
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
