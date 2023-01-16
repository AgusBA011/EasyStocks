package com.example.easystocks.data.repository

import com.example.easystocks.data.csv.CSVParser
import com.example.easystocks.data.local.StockDatabase
import com.example.easystocks.data.mapper.toCompanyInfo
import com.example.easystocks.data.mapper.toCompanyListing
import com.example.easystocks.data.mapper.toCompanyListingEntity
import com.example.easystocks.data.remote.StockApi
import com.example.easystocks.domain.model.CompanyInfo
import com.example.easystocks.domain.model.CompanyListing
import com.example.easystocks.domain.model.IntraDayInfo
import com.example.easystocks.domain.repository.StockRepository
import com.example.easystocks.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intraDayInfoParser: CSVParser<IntraDayInfo>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {

            emit(Resource.Loading(true))

            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings
                    .map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val loadFromCache = !isDbEmpty && !fetchFromRemote

            if(loadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Trouble loading the companies"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Trouble loading the companies"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings
                        .map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntraDayInfo(symbol: String): Resource<List<IntraDayInfo>> {
        return try {
            val response = api.getIntraDayInfo(symbol)
            val results = intraDayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch(e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Trouble loading IntraDay Info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Trouble loading IntraDay Info"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol).toCompanyInfo()
            Resource.Success(result)
        } catch(e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Trouble loading Company Info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Trouble loading Company Info"
            )
        }
    }
}