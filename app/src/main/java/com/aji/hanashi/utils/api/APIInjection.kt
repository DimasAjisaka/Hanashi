package com.aji.hanashi.utils.api

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.aji.hanashi.repositories.AuthRepository
import com.aji.hanashi.repositories.StoriesRepository
import com.aji.hanashi.utils.preferences.UserPreferences

object APIInjection {
    fun provideAuthRepository(dataStore: DataStore<Preferences>): AuthRepository {
        val apiService = APIConfig.getApiService()
        val userPreferences = UserPreferences.getInstance(dataStore)
        return AuthRepository(apiService, userPreferences)
    }
    fun provideStoriesRepository(dataStore: DataStore<Preferences>): StoriesRepository {
        val apiService = APIConfig.getApiService()
        return StoriesRepository(apiService)
    }
}