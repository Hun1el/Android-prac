package com.example.androidpracapp.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContactInfo(
    val phone: String = "",
    val email: String = ""
)

data class Address(
    val country: String = "",
    val city: String = "",
    val street: String = "",
    val house: String = "",
    val apartment: String = ""
) {
    fun toDisplayString(): String {
        return listOfNotNull(
            country.takeIf { it.isNotEmpty() },
            city.takeIf { it.isNotEmpty() },
            street.takeIf { it.isNotEmpty() },
            house.takeIf { it.isNotEmpty() },
            apartment.takeIf { it.isNotEmpty() }
        ).joinToString(", ")
    }
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
        calculateCart()
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

        val addressParts = parseAddress(profile.address)
        _address.value = Address(
            country = addressParts["country"] ?: "",
            city = addressParts["city"] ?: "",
            street = addressParts["street"] ?: "",
            house = addressParts["house"] ?: "",
            apartment = addressParts["apartment"] ?: ""
        )
    }

    private fun parseAddress(addressString: String?): Map<String, String> {
        if (addressString.isNullOrEmpty()) return emptyMap()

        val parts = addressString.split(",").map { it.trim() }
        return mapOf(
            "country" to (parts.getOrNull(0) ?: ""),
            "city" to (parts.getOrNull(1) ?: ""),
            "street" to (parts.getOrNull(2) ?: ""),
            "house" to (parts.getOrNull(3) ?: ""),
            "apartment" to (parts.getOrNull(4) ?: "")
        )
    }

    private fun loadLocalData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        val phone = sharedPrefs.getString("userPhone", "") ?: ""
        val email = getEmailFromPrefs() ?: ""

        _contactInfo.value = ContactInfo(phone = phone, email = email)

        val country = sharedPrefs.getString("userCountry", "") ?: ""
        val city = sharedPrefs.getString("userCity", "") ?: ""
        val street = sharedPrefs.getString("userStreet", "") ?: ""
        val house = sharedPrefs.getString("userHouse", "") ?: ""
        val apartment = sharedPrefs.getString("userApartment", "") ?: ""

        _address.value = Address(
            country = country,
            city = city,
            street = street,
            house = house,
            apartment = apartment
        )
    }

    private fun calculateCart() {
        _subtotal.value = 2000.0
        _total.value = _subtotal.value + _delivery.value
    }

    fun updateContactInfo(phone: String, email: String) {
        _contactInfo.value = ContactInfo(phone = phone, email = email)
        saveLocalData()
    }

    fun updateAddress(address: Address) {
        _address.value = address
        saveLocalData()
    }

    fun updatePaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun refreshCheckoutData() {
        loadCheckoutData()
    }

    private fun saveLocalData() {
        val context = getApplication<Application>()
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

        sharedPrefs.edit().apply {
            putString("userPhone", _contactInfo.value.phone)
            putString("userCountry", _address.value.country)
            putString("userCity", _address.value.city)
            putString("userStreet", _address.value.street)
            putString("userHouse", _address.value.house)
            putString("userApartment", _address.value.apartment)
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