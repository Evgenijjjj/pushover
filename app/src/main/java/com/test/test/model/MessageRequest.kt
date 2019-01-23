package com.test.test.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageRequest() {

    @SerializedName("token")
    @Expose
    var appToken: String = ""

    @SerializedName("user")
    @Expose
    var userKey: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("device")
    @Expose
    var deviceName: String = ""

}