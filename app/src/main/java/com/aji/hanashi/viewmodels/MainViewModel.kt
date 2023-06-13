package com.aji.hanashi.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aji.hanashi.repositories.StoriesRepository
import com.aji.hanashi.utils.responses.ListStoryItem

class MainViewModel(private val storiesRepository: StoriesRepository): ViewModel() {
    fun get(token: String): LiveData<PagingData<ListStoryItem>> = storiesRepository.get(token).cachedIn(viewModelScope)
}