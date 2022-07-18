package com.example.videoplayer.retrofit

import com.example.videoplayer.models.VideoListApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface VideoService {

    @GET("{path}/{subPath}")
    suspend fun getResults(@Path("path") path : String,
                           @Path("subPath") subPath: String) :Response<VideoListApiResponse>
}
