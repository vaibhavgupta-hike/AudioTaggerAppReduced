package com.hike.tagging.audiotaggerappreduced.retrofit

import com.hike.tagging.audiotaggerappreduced.data.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface TaggerRESTAPIService {

    @GET("get-user-details")
    fun getUserDetails(@Header("Authorization") authToken: String): Call<QueryResponse>

    @POST("save-user-details")
    fun saveUserDetails(@Body user: User, @Header("Authorization") authToken: String): Call<QueryResponse>

    @Multipart
    @POST("/uploadUserRecording")
    fun uploadAudio(
        @Part("username") username: String?,
        @Part("questionId") questionId: String?,
        @Part("text") questionText: String?,
        @Part audio: MultipartBody.Part?,
        @Header("Authorization") authToken: String?
    ): Call<QueryResponse>

    @POST("/uploadUserRecording")
    fun uploadAudio2(@Body recUploadBody: RecordingUploadBody, @Header("Authorization") authToken: String): Call<QueryResponse>

    @POST("/auth")
    fun getClientToken(@Header("Authorization") authToken: String): Call<AuthTokenResponse>

    @GET("/voice-to-text")
    fun getTextForAudio(@Header("Authorization") authToken: String,
                        @Body recUploadBody: RecordingUploadBody): Call<TextResponse>

}