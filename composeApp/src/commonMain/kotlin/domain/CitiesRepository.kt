package domain

interface CitiesRepository {
    suspend fun fetchCities(): LoadResult<List<RegionDomain>>
}