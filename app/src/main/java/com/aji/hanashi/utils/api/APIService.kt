package com.aji.hanashi.utils.api

import com.aji.hanashi.utils.responses.AddResponse
import com.aji.hanashi.utils.responses.DetailResponse
import com.aji.hanashi.utils.responses.GetLocStoryResponse
import com.aji.hanashi.utils.responses.GetResponse
import com.aji.hanashi.utils.responses.LogResponse
import com.aji.hanashi.utils.responses.RegResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @FormUrlEncoded
    @POST("/v1/login")
    suspend fun log(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LogResponse

    @FormUrlEncoded
    @POST("/v1/register")
    suspend fun reg(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegResponse

    @GET("/v1/stories")
    suspend fun get(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : GetResponse

    @Multipart
    @POST("/v1/stories")
    suspend fun add(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") desc: RequestBody
    ) : AddResponse

    @GET("/v1/stories/{id}")
    suspend fun detail(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ) : DetailResponse

    @GET("/v1/stories?location=1.")
    suspend fun getLoc(
        @Header("Authorization") auth: String
    ) : GetLocStoryResponse
}