package com.aji.hanashi.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.aji.hanashi.utils.api.APIService
import com.aji.hanashi.utils.paging.PagingList
import com.aji.hanashi.utils.responses.AddResponse
import com.aji.hanashi.utils.responses.ListLocItem
import com.aji.hanashi.utils.responses.ListStoryItem
import com.aji.hanashi.utils.responses.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class StoriesRepository(private val apiService: APIService) {
    fun get(token: String) : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { PagingList(apiService, token) }
        ).liveData
    }

    fun add(auth: String, img: MultipartBody.Part, desc: RequestBody): LiveData<Source<AddResponse>> = liveData {
        emit(Source.Loading)
        try {
            val service = apiService.add("Bearer $auth", img, desc)
            emit(Source.Success(service))
        } catch (e: HttpException) {
            emit(Source.Error(
                try {
                    e.response()?.errorBody()?.string()?.let { JSONObject(it).get("message") }
                } catch (e: JSONException) {
                    e.localizedMessage
                } as String
            ))
        }
    }

    fun detail(auth: String, id: String): LiveData<Source<Story>> = liveData {
        emit(Source.Loading)
        try {
            val service = apiService.detail("Bearer $auth", id)
            val response = service.story
            val story = Story(
                response?.id,
                response?.photoUrl,
                response?.name,
                response?.createdAt,
                response?.description
            )
            emit(Source.Success(story))
        } catch (e: HttpException) {
            emit(Source.Error(
                try {
                    e.response()?.errorBody()?.string()?.let { JSONObject(it).get("message") }
                } catch (e: JSONException) {
                    e.localizedMessage
                } as String
            ))
        }
    }

    fun getLoc(auth: String): LiveData<Source<List<ListLocItem>>> = liveData {
        emit(Source.Loading)
        try {
            val service = apiService.getLoc("Bearer $auth")
            val response = service.listStory
            val map = response.map {
                ListLocItem(
                    it.name,
                    it.lon,
                    it.lat
                )
            }
            emit(Source.Success(map))
        } catch (e: HttpException) {
            emit(Source.Error(
                try {
                    e.response()?.errorBody()?.string()?.let { JSONObject(it).get("message") }
                } catch (e: JSONException) {
                    e.localizedMessage
                } as String
            ))
        }
    }
}