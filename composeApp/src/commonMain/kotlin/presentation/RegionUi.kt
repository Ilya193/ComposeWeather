package presentation

import androidx.compose.runtime.Immutable

@Immutable
data class RegionUi(
    val id: String,
    val name: String,
    val areas: List<CityUi>,
    val showCities: Boolean = false
)