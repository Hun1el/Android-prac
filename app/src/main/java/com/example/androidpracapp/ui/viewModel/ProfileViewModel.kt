package com.example.androidpracapp.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.UserProfile
import com.example.androidpracapp.data.services.UserProfileUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProfileViewModel : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    fun loadProfile(context: Context) {
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("userId", null)
        val userEmail = sharedPrefs.getString("userEmail", "")

        if (userId == null) {
            _errorMessage.value = "Пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.userManagementService.getUserProfile("eq.$userId")
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    _profileState.value = response.body()!![0]
                } else {
                    _profileState.value = UserProfile(
                        id = "",
                        user_id = userId,
                        firstname = "Имя",
                        lastname = "Фамилия",
                        address = "Адрес",
                        phone = "+7...",
                        photo = null
                    )
                    if (!response.isSuccessful) {
                        _errorMessage.value = "Ошибка загрузки: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(context: Context, firstname: String, lastname: String, address: String, phone: String, photoBase64: String?) {
        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("userId", null) ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val updateData = UserProfileUpdate(
                    firstname = firstname,
                    lastname = lastname,
                    address = address,
                    phone = phone,
                    photo = photoBase64 ?: _profileState.value?.photo
                )
                val response = RetrofitInstance.userManagementService.updateUserProfile("eq.$userId", updateData)

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    _profileState.value = response.body()!![0]
                    _isEditing.value = false
                } else {
                    _errorMessage.value = "Ошибка сохранения: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun processImageUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}