/**
 * ViewModel аутентификации
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val signInSuccessMutable = MutableStateFlow(false)
    val signInSuccess = signInSuccessMutable.asStateFlow()

    private val signInErrorMutable = MutableStateFlow<String?>(null)
    val signInError = signInErrorMutable.asStateFlow()

    private val isLoadingMutable = MutableStateFlow(false)
    val isLoading = isLoadingMutable.asStateFlow()

    fun setError(error: String) {
        signInErrorMutable.value = error
    }

    fun signIn(email: String, password: String, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            signInErrorMutable.value = "Пожалуйста, заполните все поля"
            return
        }

        viewModelScope.launch {
            isLoadingMutable.value = true
            signInErrorMutable.value = null

            try {
                Log.d("signIn", "Попытка входа: Email=$email")
                val user = User(email = email, password = password)
                val response = RetrofitInstance.userManagementService.signIn(user)

                Log.d("signIn", "Response Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val userId = responseBody.user?.id ?: responseBody.id

                    if (userId != null) {
                        val sharedPrefs = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
                        sharedPrefs.edit().putString("userId", userId).putString("userEmail", email).putString("accessToken", responseBody.accessToken).apply()

                        Log.d("signIn", "Успешный вход: $email, ID=$userId")
                        signInSuccessMutable.value = true
                    } else {
                        signInErrorMutable.value = "Ошибка: сервер не вернул ID пользователя"
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string() ?: ""
                    Log.e("signIn", "Ошибка входа. Код: ${response.code()}")

                    val errorMsg = when {
                        errorBodyString.contains("invalid_grant") || response.code() == 400 -> "Неверный email или пароль"
                        response.code() == 401 -> "Ошибка авторизации. Проверьте данные."
                        response.code() == 429 -> "Слишком много попыток. Подождите немного."
                        else -> "Ошибка сервера: ${response.code()}"
                    }

                    signInErrorMutable.value = errorMsg
                    signInSuccessMutable.value = false
                }
            } catch (e: Exception) {
                Log.e("signIn", "Exception: ${e.message}", e)
                e.printStackTrace()
                signInErrorMutable.value = "Ошибка подключения: ${e.message}"
                signInSuccessMutable.value = false
            } finally {
                isLoadingMutable.value = false
            }
        }
    }

    fun checkUserExists(email: String, context: Context) {
        if (email.isBlank()) {
            signInErrorMutable.value = "Введите Email"
            return
        }

        viewModelScope.launch {
            isLoadingMutable.value = true
            signInErrorMutable.value = null

            try {
                Log.d("checkUser", "Проверка пользователя: $email")

                val dummyUser = User(email = email, password = "dummy_password_check")
                val response = RetrofitInstance.userManagementService.signIn(dummyUser)
                val errorBodyString = response.errorBody()?.string() ?: ""

                if (errorBodyString.contains("invalid_grant") || response.code() == 400) {
                    Log.d("checkUser", "Пользователь СУЩЕСТВУЕТ, отправляем письмо")
                    sendPasswordResetCode(email, context)
                } else {
                    Log.d("checkUser", "Пользователь НЕ СУЩЕСТВУЕТ или другая ошибка")
                    signInErrorMutable.value = "Пользователь с этим email не найден"
                }
            } catch (e: Exception) {
                Log.e("checkUser", "Exception: ${e.message}", e)
                signInErrorMutable.value = "Ошибка проверки: ${e.message}"
            } finally {
                isLoadingMutable.value = false
            }
        }
    }

    fun sendPasswordResetCode(email: String, context: Context) {
        viewModelScope.launch {
            isLoadingMutable.value = true
            try {
                Log.d("sendPasswordReset", "Отправка ссылки на: $email")
                val request = ResetPasswordRequest(email)
                val response = RetrofitInstance.userManagementService.resetPasswordForEmail(request)

                if (response.isSuccessful) {
                    Log.d("sendPasswordReset", "Письмо успешно отправлено")
                    signInErrorMutable.value = null
                } else {
                    val errorMsg = when (response.code()) {
                        429 -> "Письмо уже отправлено. Проверьте почту."
                        404 -> "Пользователь не найден."
                        else -> "Ошибка отправки: ${response.code()}"
                    }
                    signInErrorMutable.value = errorMsg
                }
            } catch (e: Exception) {
                signInErrorMutable.value = "Ошибка сети: ${e.message}"
            } finally {
                isLoadingMutable.value = false
            }
        }
    }

    fun clearError() {
        signInErrorMutable.value = null
    }

    fun clearSuccess() {
        signInSuccessMutable.value = false
    }
}
