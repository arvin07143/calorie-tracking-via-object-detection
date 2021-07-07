package com.example.fyp.data.remote

import com.example.fyp.data.entities.UserInformation
import com.example.fyp.objectdetection.DetectedObjectList
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WebAPI {
    @Multipart
    @POST("/detection/predict/")
    fun predictImage(@Part file: MultipartBody.Part): Call<DetectedObjectList>

    @GET("/users/me")
    fun getUserInformation(): Call<UserInformation>

}