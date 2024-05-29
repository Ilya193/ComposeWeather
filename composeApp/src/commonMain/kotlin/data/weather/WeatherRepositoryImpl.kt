package data.weather

import data.toWeatherDomain
import data.weather.model.WeatherDTO
import domain.BuildConfig
import domain.LoadResult
import domain.weather.WeatherDomain
import domain.weather.WeatherRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class WeatherRepositoryImpl(
    private val client: HttpClient
) : WeatherRepository {
    override suspend fun fetchWeather(city: String): LoadResult<WeatherDomain> {
        return try {
            val body = client.get("https://api.weatherapi.com/v1/current.json?key=${BuildConfig.API_KEY}&q=$city").body<WeatherDTO>()
            LoadResult.Success(body.toWeatherDomain())
        } catch (_: Exception) {
            LoadResult.Error()
        }
    }
}