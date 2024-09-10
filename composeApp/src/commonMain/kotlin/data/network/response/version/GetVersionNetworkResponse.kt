package data.network.response.version

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetVersionNetworkResponse(
    @SerialName("id")
    val id : Int,
    @SerialName("version_name")
    val version_name : String,
    @SerialName("version_code")
    val version_code : Double,
    @SerialName("os")
    val os : String?,
    @SerialName("apk_file")
    val apk_file : String,
    @SerialName("ipa_link")
    val ipa_link: String?,
    @SerialName("title")
    val title : String,
    @SerialName("description")
    val description : String,
    @SerialName("force_update")
    val force_update : Boolean,

)