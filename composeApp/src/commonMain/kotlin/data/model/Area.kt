package data.model

import kotlinx.serialization.Serializable

@Serializable
data class Area(
    val areas: List<AreaX>,
    val id: String,
    val name: String,
    val parent_id: String
)