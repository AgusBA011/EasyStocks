package com.example.easystocks.data.csv

import com.example.easystocks.data.mapper.toIntraDayInfo
import com.example.easystocks.data.remote.dto.IntraDayInfoDto
import com.example.easystocks.domain.model.CompanyListing
import com.example.easystocks.domain.model.IntraDayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntraDayParser @Inject constructor(): CSVParser<IntraDayInfo> {

    override suspend fun parse(stream: InputStream): List<IntraDayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        // TODO: Util function that those everything and receives closure for mapNotNull
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntraDayInfoDto(timestamp = timestamp, close = close)
                    dto.toIntraDayInfo()
                }
                .filter { it.date.dayOfMonth == LocalDate.now().minusDays(1).dayOfMonth }
                .sortedBy {
                    it.date.hour
                }
                .also { csvReader.close() }
        }
    }
}