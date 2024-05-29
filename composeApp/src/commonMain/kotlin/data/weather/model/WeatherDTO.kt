package data.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDTO(
    val current: Current,
    val location: Location
)