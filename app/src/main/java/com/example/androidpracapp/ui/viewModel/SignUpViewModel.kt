package com.example.androidpracapp.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpracapp.data.RetrofitInstance
import com.example.androidpracapp.domain.models.User
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    val signUpSuccess = MutableStateFlow(false)
    val signUpError = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)

    fun signUp(email: String, password: String, context: Context) {
        Log.d("signUp", "Ввод: Email=$email, Password=$password")

        viewModelScope.launch {
            isLoading.value = true
            try {
                val user = User(email = email, password = password)
                val response = RetrofitInstance.userManagementService.signUp(user)

                Log.d("signUp", "Response Code: ${response.code()}")
                Log.d("signUp", "Response Body: ${response.body()}")

                val errorBodyString = response.errorBody()?.string()
                Log.d("signUp", "Error Body: $errorBodyString")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val userId = responseBody?.user?.id ?: responseBody?.id

                    if (userId != null) {
                        context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE).edit().putString("userId", userId).putString("userEmail", email).apply()
                        Log.d("signUp", "Успешная регистрация: userId=$userId")
                        signUpSuccess.value = true
                    } else {
                        Log.e("signUp", "Response успешен но ID пуст: $responseBody")
                        signUpError.value = "Ошибка: нет ID в ответе"
                    }
                } else {
                    var errorMsg = "Ошибка сервера: ${response.code()}"

                    if (errorBodyString != null && errorBodyString.isNotEmpty()) {
                        errorMsg = when {
                            errorBodyString.contains("email_address_invalid") -> "Email должен быть минимум 6 символа перед @. Пример: abc@gmail.com"
                            errorBodyString.contains("user_already_exists") -> "Этот email уже зарегистрирован"
                            errorBodyString.contains("weak_password") -> "Введенный пароль слишком короткий! Минимальная длина пароля 6 символов."
                            errorBodyString.contains("over_email_send_rate_limit") -> "Слишком много попыток. Используйте другой email или подождите немного."
                            errorBodyString.contains("already registered") -> "Этот email уже зарегистрирован"
                            response.code() == 400 -> "Ошибка: некорректные данные"
                            response.code() == 422 -> "Такой пользователь уже зарегистрирован"
                            else -> "Ошибка сервера: ${response.code()}"
                        }
                    }
                    Log.e("signUp", errorMsg)
                    signUpError.value = errorMsg
                    signUpSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("signUp", "Exception: ${e.message}", e)
                e.printStackTrace()
                signUpError.value = "Ошибка подключения: ${e.message}"
                signUpSuccess.value = false
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearError() {
        signUpError.value = null
    }

    fun clearSuccess() {
        signUpSuccess.value = false
    }
}