package domain

data class RegionDomain(
    val id: String,
    val name: String,
    val areas: List<CityDomain>,
)