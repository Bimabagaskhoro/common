package com.bimabk.common.extension

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName(value = "status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)