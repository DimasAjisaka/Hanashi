package com.aji.hanashi

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aji.hanashi.utils.responses.ListStoryItem

class PagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snap(item: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(item)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return if (params.loadSize != 0) {
            LoadResult.Page(emptyList(), 0, 1)
        } else {
            LoadResult.Page(emptyList(), null, null)
        }
    }
}