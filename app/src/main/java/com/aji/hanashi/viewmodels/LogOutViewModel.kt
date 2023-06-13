package com.aji.hanashi.viewmodels

import androidx.lifecycle.ViewModel
import com.aji.hanashi.repositories.AuthRepository

class LogOutViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun logOut() = authRepository.logOut()
}