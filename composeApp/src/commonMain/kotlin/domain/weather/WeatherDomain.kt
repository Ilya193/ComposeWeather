package domain.weather

data class WeatherDomain(
    val country: String,
    val name: String,
    val localtime: String,
    val temp: Double
)