package di

import data.cities.CitiesRepositoryImpl
import data.weather.WeatherRepositoryImpl
import domain.cities.CitiesRepository
import domain.weather.WeatherRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val commonAppModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
        }
    }

    factory<CitiesRepository> {
        CitiesRepositoryImpl(get())
    }

    factory<WeatherRepository> {
        WeatherRepositoryImpl(get())
    }
}