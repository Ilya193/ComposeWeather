package domain.cities

import domain.LoadResult

interface CitiesRepository {
    suspend fun fetchCities(): LoadResult<List<RegionDomain>>
}