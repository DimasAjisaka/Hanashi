package com.aji.hanashi.viewmodels

import androidx.lifecycle.ViewModel
import com.aji.hanashi.repositories.StoriesRepository

class MapsViewModel(private val storiesRepository: StoriesRepository): ViewModel() {
    fun getLoc(auth: String) = storiesRepository.getLoc(auth)
}