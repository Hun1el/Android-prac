/**
 * ViewModel профиля
 *
 * @author Солоников Антон
 * @date 17.12.2025
 */

package com.example.androidpracapp.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.R
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.UserProfile
import com.example.androidpracapp.data.services.UserProfileCreate
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
            val error = "Пользователь не авторизован (userId is null)"
            Log.e("ProfileViewModel", error)
            _errorMessage.value = error
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Log.d("ProfileViewModel", "Начинаем загрузку профиля для userId: $userId")

            try {
                val response = RetrofitInstance.profileManagementService.getUserProfile("eq.$userId")
                Log.d("ProfileViewModel", "Ответ сервера (getUserProfile): код=${response.code()}, message=${response.message()}")

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    Log.d("ProfileViewModel", "Профиль успешно загружен: ${response.body()!![0]}")
                    _profileState.value = response.body()!![0]
                } else {
                    Log.w("ProfileViewModel", "Профиль не найден или пустой ответ. Создаем заглушку. Body: ${response.body()}")
                    _profileState.value = UserProfile(
                        id = "",
                        user_id = userId,
                        firstname = "",
                        lastname = "",
                        address = "",
                        phone = "",
                        photo = null
                    )

                    if (!response.isSuccessful) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProfileViewModel", "Ошибка сервера при загрузке: $errorBody")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Ошибка при загрузке профиля: ${e.message}", e)
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
            Log.d("ProfileViewModel", "Начинаем сохранение профиля...")

            try {
                val updateData = UserProfileUpdate(firstname, lastname, address, phone, photoBase64 ?: _profileState.value?.photo)
                val responsePatch = RetrofitInstance.profileManagementService.updateUserProfile("eq.$userId", updateData)

                if (responsePatch.isSuccessful && !responsePatch.body().isNullOrEmpty()) {
                    Log.d("ProfileViewModel", "Профиль обновлен (PATCH)")
                    _profileState.value = responsePatch.body()!![0]
                    _isEditing.value = false
                }
                else if (responsePatch.isSuccessful || responsePatch.code() == 404) {
                    Log.d("ProfileViewModel", "Запись не найдена, создаем новую (POST)...")

                    val createData = UserProfileCreate(
                        user_id = userId,
                        firstname = firstname,
                        lastname = lastname,
                        address = address,
                        phone = phone,
                        photo = photoBase64
                    )
                    val responsePost = RetrofitInstance.profileManagementService.createUserProfile(createData)

                    if (responsePost.isSuccessful && !responsePost.body().isNullOrEmpty()) {
                        Log.d("ProfileViewModel", "Профиль создан (POST)")
                        _profileState.value = responsePost.body()!![0]
                        _isEditing.value = false
                    } else {
                        val err = responsePost.errorBody()?.string()
                        Log.e("ProfileViewModel", "Ошибка создания: $err")
                        _errorMessage.value = "Ошибка создания профиля"
                    }
                }
                else {
                    _errorMessage.value = "Ошибка сохранения: ${responsePatch.code()}"
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
        Log.d("ProfileViewModel", "Режим редактирования переключен: ${_isEditing.value}")
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun processImageUri(context: Context, uri: Uri): String? {
        Log.d("ProfileViewModel", "Обработка изображения: $uri")
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("ProfileViewModel", "Изображение успешно конвертировано в Base64. Длина строки: ${base64.length}")
            base64
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Ошибка обработки изображения: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
}