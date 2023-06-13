package com.aji.hanashi.viewmodels

import androidx.lifecycle.ViewModel
import com.aji.hanashi.repositories.StoriesRepository

class DetailViewModel(private val storiesRepository: StoriesRepository): ViewModel() {
    fun detail(auth: String, id: String) = storiesRepository.detail(auth, id)
}