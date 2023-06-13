package com.aji.hanashi.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aji.hanashi.repositories.AuthRepository
import com.aji.hanashi.utils.preferences.User

class LogViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun log(mail: String, pass: String) = authRepository.log(mail, pass)
    fun user(): LiveData<User> = authRepository.user()
}