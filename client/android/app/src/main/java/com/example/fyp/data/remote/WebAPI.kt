package com.example.fyp.data.remote

import com.example.fyp.data.entities.UserInformation
import com.example.fyp.objectdetection.DetectedObjectList
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface WebAPI {
    @Multipart
    @POST("/detection/predict/")
    fun predictImage(@Part file: MultipartBody.Part): Call<DetectedObjectList>

    @GET("/users/me")
    fun getUserInformation(): Call<UserInformation>

    @POST("/users/")
    fun registerNewUser(@Body userInformation: UserInformation): Call<JSONObject>

}