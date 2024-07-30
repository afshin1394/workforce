package data.network.response.task.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BasicInfo(
    @SerialName("ticket_number")
    val ticket_number : String?,
    @SerialName("ticket_state")
    val ticket_state : String?,
    @SerialName("level")
    val level : String?,
    @SerialName("location")
    val location : String?,
    @SerialName("site")
    val site : String?,
    @SerialName("region")
    val region : String?,
    @SerialName("province")
    val province : String?,
    @SerialName("city")
    val city : String?,
){
    override fun toString(): String {
        return "BasicInfo(ticket_number=$ticket_number, ticket_state=$ticket_state, level=$level, location=$location, site=$site, region=$region, province=$province, city=$city)"
    }
}
