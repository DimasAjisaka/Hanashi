package com.aji.hanashi.viewmodels

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.repositories.AuthRepository
import com.aji.hanashi.utils.api.APIInjection

class AuthViewModelFactory(private val authRepository: AuthRepository): ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile private var instance: AuthViewModelFactory? = null
        fun getInstance(dataStore: DataStore<Preferences>): AuthViewModelFactory = instance ?: synchronized(this) {
            instance ?: AuthViewModelFactory(APIInjection.provideAuthRepository(dataStore))
        }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            return LogViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(RegViewModel::class.java)) {
            return RegViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(LogOutViewModel::class.java)) {
            return LogOutViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}