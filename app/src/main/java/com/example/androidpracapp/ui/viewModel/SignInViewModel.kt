/**
 * ViewModel для управления аутентификацией
 *
 * @author Солоников Антон
 * @date 16.12.2025
 */

package com.example.androidpracapp.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.data.services.ResetPasswordRequest
import com.example.androidpracapp.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    val signInSuccess = MutableStateFlow(false)
    val signInError = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)

    fun signIn(email: String, password: String, context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                Log.d("signIn", "Попытка входа: Email=$email")
                val user = User(email = email, password = password)
                val response = RetrofitInstance.userManagementService.signIn(user)

                Log.d("signIn", "Response Code: ${response.code()}")
                Log.d("signIn", "Response Body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val userId = responseBody.user?.id ?: responseBody.id

                    if (userId != null) {
                        context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE).edit().putString("userId", userId).putString("userEmail", email).putString("accessToken", responseBody.accessToken).apply()
                        Log.d("signIn", "Успешный вход: $email, ID=$userId")
                        signInSuccess.value = true
                    } else {
                        signInError.value = "Ошибка: нет данных пользователя"
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string() ?: ""
                    Log.e("signIn", "Ошибка входа. Код: ${response.code()}")

                    val errorMsg = when {
                        errorBodyString.contains("invalid_grant") || response.code() == 400 -> "Неверный email или пароль"
                        response.code() == 401 -> "Неверные данные"
                        response.code() == 429 -> "Слишком много попыток входа. Подождите."
                        else -> "Ошибка сервера: ${response.code()}"
                    }

                    signInError.value = errorMsg
                    signInSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("signIn", "Exception: ${e.message}", e)
                e.printStackTrace()
                signInError.value = "Ошибка подключения: ${e.message}"
                signInSuccess.value = false
            } finally {
                isLoading.value = false
            }
        }
    }

    fun sendPasswordResetCode(email: String, context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                Log.d("sendPasswordReset", "Отправка ссылки на: $email")
                val request = ResetPasswordRequest(email)
                val response = RetrofitInstance.userManagementService.resetPasswordForEmail(request)

                if (response.isSuccessful) {
                    Log.d("sendPasswordReset", "Ссылка успешно отправлена")
                    signInSuccess.value = true
                    signInError.value = null
                } else {
                    Log.e("sendPasswordReset", "Ошибка: ${response.code()}")
                    signInError.value = "Ошибка при отправке. Попробуйте еще раз."
                    signInSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("sendPasswordReset", "Exception: ${e.message}", e)
                signInError.value = e.message ?: "Неизвестная ошибка"
                signInSuccess.value = false
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearError() {
        signInError.value = null
    }

    fun clearSuccess() {
        signInSuccess.value = false
    }
}