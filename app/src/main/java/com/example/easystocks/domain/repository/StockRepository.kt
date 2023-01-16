package com.example.easystocks.domain.repository

import com.example.easystocks.domain.model.CompanyInfo
import com.example.easystocks.domain.model.CompanyListing
import com.example.easystocks.domain.model.IntraDayInfo
import com.example.easystocks.utils.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntraDayInfo(
        symbol: String
    ): Resource<List<IntraDayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}