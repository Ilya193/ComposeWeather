package presentation.weather

data class WeatherUi(
    val country: String,
    val name: String,
    val localtime: String,
    val temp: Int
)