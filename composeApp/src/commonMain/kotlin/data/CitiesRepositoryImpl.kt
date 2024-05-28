package data

import data.model.CitiesDTOItem
import domain.CitiesRepository
import domain.LoadResult
import domain.RegionDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CitiesRepositoryImpl(
    private val client: HttpClient
) : CitiesRepository {
    override suspend fun fetchCities(): LoadResult<List<RegionDomain>> {
        return try {
            val body = client.get("https://api.hh.ru/areas").body<List<CitiesDTOItem>>()
            LoadResult.Success(body[0].areas.map { it.toRegionDomain() })
        } catch (_: Exception) {
            LoadResult.Error()
        }
    }
}