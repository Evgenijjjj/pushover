package com.test.test.retrofit

import com.test.test.model.MessageRequest
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApi {
    @POST("messages.json")
    fun sendMessage(@Body messageRequest: MessageRequest): Call<JSONObject>
}