package com.aji.hanashi.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.aji.hanashi.utils.api.APIService
import com.aji.hanashi.utils.preferences.User
import com.aji.hanashi.utils.preferences.UserPreferences
import com.aji.hanashi.utils.responses.LogResponse
import com.aji.hanashi.utils.responses.RegResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepository(private val apiService: APIService, private val userPreferences: UserPreferences) {
    fun log(mail: String, pass: String): LiveData<Source<LogResponse>> = liveData {
        emit(Source.Loading)
        try {
            val service = apiService.log(mail, pass)
            userPreferences.log(true, service.loginResult?.token!!)
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

    fun reg(username: String, mail: String, pass: String): LiveData<Source<RegResponse>> = liveData {
        emit(Source.Loading)
        try {
            val service = apiService.reg(username, mail, pass)
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

    fun user(): LiveData<User> = userPreferences.user().asLiveData()

    fun logOut() = CoroutineScope(Dispatchers.Main).launch { userPreferences.logOut() }
}