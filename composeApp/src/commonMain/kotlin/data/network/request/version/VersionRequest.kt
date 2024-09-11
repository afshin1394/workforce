package data.network.request.version

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionRequest(
    @SerialName("current_version_code")
    val current_version_code : String,
    @SerialName("current_version_name")
    val current_version_name : String,
    @SerialName("device_model")
    val device_model : String,
    @SerialName("os")
    val os : String,
    @SerialName("os_version")
    val os_version : String,
)