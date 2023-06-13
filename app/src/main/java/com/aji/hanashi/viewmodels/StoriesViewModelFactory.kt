package com.aji.hanashi.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.repositories.StoriesRepository
import com.aji.hanashi.utils.api.APIInjection

class StoriesViewModelFactory(private val storiesRepository: StoriesRepository): ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: StoriesViewModelFactory? = null
        fun getIntance(dataStore: DataStore<Preferences>): StoriesViewModelFactory = instance ?: synchronized(this) {
            instance ?: StoriesViewModelFactory(APIInjection.provideStoriesRepository(dataStore))
        }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storiesRepository) as T
        } else if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(storiesRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storiesRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storiesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}