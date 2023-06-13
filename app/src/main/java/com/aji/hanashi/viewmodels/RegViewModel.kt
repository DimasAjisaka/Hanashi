package com.aji.hanashi.viewmodels

import androidx.lifecycle.ViewModel
import com.aji.hanashi.repositories.AuthRepository

class RegViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun reg(username: String, mail: String, pass: String) = authRepository.reg(username, mail, pass)
}