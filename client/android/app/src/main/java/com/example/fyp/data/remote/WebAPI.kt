package com.example.fyp.data.remote

import com.example.fyp.data.entities.UserInformation
import com.example.fyp.objectdetection.DetectedObjectList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WebAPI {
    @GET("/detection/predict/")
    fun predictImage(@Query("b64") base64Image:String) : Call<DetectedObjectList>

    @GET("/users/me")
    fun getUserInformation(): Call<UserInformation>

}