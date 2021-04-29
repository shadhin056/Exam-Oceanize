package com.example.oceanizeapplication.model

import com.google.gson.annotations.SerializedName

data class DataModelResponse(
    @SerializedName("id")
    var id: Int?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("host")
    var host: String?,

    @SerializedName("port")
    var port: String?,

    @SerializedName("username")
    var username: String?,

    @SerializedName("password")
    var password: String?,

    @SerializedName("command")
    var command: String?,


    @SerializedName("createdAt")
    var createdAt: String?,

    @SerializedName("updatedAt")
    var updatedAt: String?,

    @SerializedName("status")
    var status: Int?,

)