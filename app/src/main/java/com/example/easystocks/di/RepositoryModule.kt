package com.example.easystocks.di

import com.example.easystocks.data.csv.CSVParser
import com.example.easystocks.data.csv.CompanyListingsParser
import com.example.easystocks.data.csv.IntraDayParser
import com.example.easystocks.data.repository.StockRepositoryImpl
import com.example.easystocks.domain.model.CompanyListing
import com.example.easystocks.domain.model.IntraDayInfo
import com.example.easystocks.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntraDayInfoParser(
        intraDayParser: IntraDayParser
    ): CSVParser<IntraDayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}