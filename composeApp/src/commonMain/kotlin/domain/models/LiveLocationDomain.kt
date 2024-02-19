package domain.models

data class LiveLocationDomain(
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val site: Long
)
