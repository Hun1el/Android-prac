package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.PaymentCard
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

    private val _cardNumber = MutableStateFlow("")
    val cardNumber = _cardNumber.asStateFlow()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal = _subtotal.asStateFlow()

    private val _delivery = MutableStateFlow(60.20)
    val delivery = _delivery.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total = _total.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError = _validationError.asStateFlow()

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

    fun checkEmail(email: String): String? {
        val regex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        return when {
            email.isBlank() -> "Email не может быть пустым"
            !regex.matches(email) -> "Email должен быть валидным (login@domain.ru)"
            else -> null
        }
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

                    val calculatedSubtotal = cartEntries.sumOf { entry ->
                        val product = allProducts.find { it.id == entry.product_id }
                        val price = product?.cost ?: 0.0
                        val count = entry.count ?: 1
                        price * count
                    }

                    _subtotal.value = calculatedSubtotal
                    _total.value = calculatedSubtotal + _delivery.value
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Error calculating: ${e.message}")
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
                    updateDataFromProfile(response.body()!![0])
                } else {
                    loadLocalData()
                }
            } catch (e: Exception) {
                loadLocalData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateDataFromProfile(profile: UserProfile) {
        val email = getEmailFromPrefs() ?: ""
        _contactInfo.value = ContactInfo(phone = profile.phone ?: "", email = email)
        _address.value = Address(fullAddress = profile.address ?: "")
    }

    private fun loadLocalData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        _contactInfo.value = ContactInfo(
            phone = sharedPrefs.getString("userPhone", "") ?: "",
            email = getEmailFromPrefs() ?: ""
        )
        _address.value = Address(fullAddress = sharedPrefs.getString("userAddress", "") ?: "")
    }

    fun updateContactInfo(phone: String, email: String) {
        _contactInfo.value = ContactInfo(phone = phone, email = email)
        _validationError.value = null
    }

    fun updateAddress(fullAddress: String) {
        _address.value = Address(fullAddress = fullAddress)
    }

    fun updateCardNumber(number: String) {
        _cardNumber.value = number
    }

    fun placeOrder(onSuccess: () -> Unit) {
        val emailError = checkEmail(_contactInfo.value.email)
        if (emailError != null) {
            _validationError.value = emailError
            return
        }

        if (_cardNumber.value.isBlank()) {
            _validationError.value = "Укажите номер карты"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = getUserIdFromPrefs() ?: return@launch

                val orderRequest = CreateOrderRequest(
                    user_id = userId,
                    total_amount = _total.value,
                    delivery_address = _address.value.toDisplayString(),
                    phone = _contactInfo.value.phone,
                    email = _contactInfo.value.email,
                    payment_method = _cardNumber.value,
                    items = emptyList()
                )

                val response = RetrofitInstance.orderService.createOrder(orderRequest)
                if (response.isSuccessful) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("Checkout", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}