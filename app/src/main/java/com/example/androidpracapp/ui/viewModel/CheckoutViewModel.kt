package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.CreateOrderRequest
import com.example.androidpracapp.data.services.UserProfile
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    // --- НОВОЕ: Поле валидации формы ---
    val isFormValid = combine(_contactInfo, _address, _cardNumber) { contact, addr, card ->
        val isEmailValid = checkEmail(contact.email) == null
        val isPhoneValid = contact.phone.isNotBlank()
        val isAddressValid = addr.fullAddress.isNotBlank()
        val isCardValid = card.isNotBlank() && card.length >= 16 // Простая проверка длины

        isEmailValid && isPhoneValid && isAddressValid && isCardValid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    // ----------------------------------

    init {
        Log.d("Checkout", "CheckoutViewModel initialized")
        loadCheckoutData()
        loadCartAndCalculate()
    }

    private fun getUserIdFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("userId", null)
        Log.d("Checkout", "getUserIdFromPrefs: userId=$userId")
        return userId
    }

    private fun getEmailFromPrefs(): String? {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val email = sharedPrefs.getString("userEmail", "")
        Log.d("Checkout", "getEmailFromPrefs: email=$email")
        return email
    }

    fun checkEmail(email: String): String? {
        val regex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        val error = when {
            email.isBlank() -> "Email не может быть пустым"
            !regex.matches(email) -> "Email должен быть валидным (login@domain.ru)"
            else -> null
        }
        return error
    }

    private fun loadCartAndCalculate() {
        val userId = getUserIdFromPrefs() ?: return

        Log.d("Checkout", "loadCartAndCalculate: starting for userId=$userId")
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
                Log.e("Checkout", "loadCartAndCalculate: error=${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadCheckoutData() {
        val userId = getUserIdFromPrefs() ?: return

        Log.d("Checkout", "loadCheckoutData: starting for userId=$userId")
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
        val phone = sharedPrefs.getString("userPhone", "") ?: ""
        val email = getEmailFromPrefs() ?: ""
        val address = sharedPrefs.getString("userAddress", "") ?: ""

        _contactInfo.value = ContactInfo(phone = phone, email = email)
        _address.value = Address(fullAddress = address)
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
        if (!isFormValid.value) return // Доп. защита

        viewModelScope.launch {
            _isLoading.value = true
            _validationError.value = null
            try {
                val userId = getUserIdFromPrefs() ?: run {
                    _validationError.value = "Ошибка: пользователь не найден"
                    _isLoading.value = false
                    return@launch
                }

                val orderRequest = CreateOrderRequest(
                    user_id = userId,
                    email = _contactInfo.value.email,
                    phone = _contactInfo.value.phone,
                    address = _address.value.fullAddress,
                    payment_id = null,
                    delivery_coast = _delivery.value.toLong()
                )

                val response = RetrofitInstance.orderManagementService.createOrder(orderRequest)

                if (response.isSuccessful) {
                    clearCart(userId)
                    _cardNumber.value = ""
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _validationError.value = "Ошибка: ${response.code()}"
                }
            } catch (e: Exception) {
                _validationError.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun clearCart(userId: String) {
        viewModelScope.launch {
            try {
                cartService.clearCart(userId = "eq.$userId")
            } catch (e: Exception) {
                Log.e("Checkout", "clearCart: error", e)
            }
        }
    }

    fun refreshCheckoutData() {
        loadCheckoutData()
        loadCartAndCalculate()
    }
}