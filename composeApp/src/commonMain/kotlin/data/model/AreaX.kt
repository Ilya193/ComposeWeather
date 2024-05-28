package data.model

import kotlinx.serialization.Serializable

@Serializable
data class AreaX(
    val areas: List<String>,
    val id: String,
    val name: String,
    val parent_id: String
)