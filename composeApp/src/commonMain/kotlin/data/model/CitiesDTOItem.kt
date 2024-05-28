package data.model

import kotlinx.serialization.Serializable

@Serializable
data class CitiesDTOItem(
    val areas: List<Area>,
    val id: String,
    val name: String,
    val parent_id: String? = null
)