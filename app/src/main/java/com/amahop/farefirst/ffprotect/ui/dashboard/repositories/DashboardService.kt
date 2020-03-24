package com.amahop.farefirst.ffprotect.ui.dashboard.repositories

import com.amahop.farefirst.ffprotect.ui.dashboard.repositories.pojos.GovMessage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DashboardService {
    @GET("/gov_message")
    fun fetchGovMessage(@Header("Authorization") bearerToken: String, @Query("locale") locale: String): Call<GovMessage>
}