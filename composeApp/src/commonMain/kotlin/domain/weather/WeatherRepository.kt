package domain.weather

import domain.LoadResult

interface WeatherRepository {
    suspend fun fetchWeather(city: String): LoadResult<WeatherDomain>
}