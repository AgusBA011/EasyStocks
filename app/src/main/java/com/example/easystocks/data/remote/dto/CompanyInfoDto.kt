package com.example.easystocks.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class CompanyInfoDto(
    @field:Json(name = "Symbol") val symbol: String?,
    @field:Json(name = "Description") val description: String?,
    @field:Json(name = "Name") val name: String?,
    @field:Json(name = "Country") val country: String?,
    @field:Json(name = "Industry") val industry: String?
)