package com.hike.tagging.audiotaggerappreduced.retrofit

import com.hike.tagging.audiotaggerappreduced.data.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface TaggerRESTAPIService {

    @GET("get-user-details")
    fun getUserDetails(@Header("Authorization") authToken: String): Call<QueryReponse>

    @POST("save-user-details")
    fun saveUserDetails(@Body user: User, @Header("Authorization") authToken: String): Call<QueryReponse>

    @Multipart
    @POST("/uploadUserRecording")
    fun uploadAudio(
        @Part("username") username: String?,
        @Part("questionId") questionId: String?,
        @Part("text") questionText: String?,
        @Part audio: MultipartBody.Part?,
        @Header("Authorization") authToken: String?
    ): Call<QueryReponse>

    @POST("/uploadUserRecording")
    fun uploadAudio2(@Body recUploadBody: RecordingUploadBody, @Header("Authorization") authToken: String): Call<QueryReponse>

    @POST("/auth")
    fun getClientToken(@Header("Authorization") authToken: String): Call<AuthTokenResponse>

}