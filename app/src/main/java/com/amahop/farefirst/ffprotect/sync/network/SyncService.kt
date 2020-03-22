package com.amahop.farefirst.ffprotect.sync.network

import com.amahop.farefirst.ffprotect.sync.network.pojo.SyncData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SyncService {
    @POST("/sync")
    fun sync(@Header("Authorization") bearerToken: String, @Body syncData: SyncData): Call<Response<Void>>
}