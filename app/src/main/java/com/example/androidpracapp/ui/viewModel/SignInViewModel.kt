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

    fun checkUserExists(email: String, context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                Log.d("checkUser", "Проверка пользователя: $email")

                val dummyUser = User(email = email, password = "")
                val response = RetrofitInstance.userManagementService.signIn(dummyUser)

                val errorBodyString = response.errorBody()?.string() ?: ""

                Log.d("checkUser", "Response Code: ${response.code()}")
                Log.d("checkUser", "Error Body: $errorBodyString")

                if (errorBodyString.contains("invalid_grant")) {
                    Log.d("checkUser", "Пользователь СУЩЕСТВУЕТ, отправляем письмо")
                    sendPasswordResetCode(email, context)
                } else {
                    Log.d("checkUser", "Пользователь НЕ СУЩЕСТВУЕТ")
                    signInError.value = "Пользователь с этим email не найден"
                    signInSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("checkUser", "Exception: ${e.message}", e)
                signInError.value = "Ошибка проверки пользователя"
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

                Log.d("sendPasswordReset", "Response Code: ${response.code()}")
                val errorBodyString = response.errorBody()?.string() ?: ""
                Log.d("sendPasswordReset", "Response Body: $errorBodyString")

                if (response.isSuccessful) {
                    Log.d("sendPasswordReset", "Письмо успешно отправлено на $email")
                    signInSuccess.value = true
                    signInError.value = null
                } else {
                    Log.e("sendPasswordReset", "Ошибка: ${response.code()}")
                    Log.e("sendPasswordReset", "Тело ошибки: $errorBodyString")

                    val errorMsg = when {
                        response.code() == 429 -> "Письмо уже отправлено на $email. Проверьте почту."
                        response.code() == 400 -> "Пользователь с таким email не найден"
                        response.code() == 404 -> "Пользователь с таким email не найден"
                        else -> "Пользователь с таким email не найден"
                    }

                    signInError.value = errorMsg
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