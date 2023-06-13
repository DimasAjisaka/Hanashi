package com.aji.hanashi.viewmodels

import androidx.lifecycle.ViewModel
import com.aji.hanashi.repositories.StoriesRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val storiesRepository: StoriesRepository): ViewModel() {
    fun add(auth: String, img: MultipartBody.Part, desc: RequestBody) = storiesRepository.add(auth, img, desc)
}