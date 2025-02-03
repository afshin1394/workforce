package data.network.response.task.task

import data.network.response.task.FormStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Detail(


    @SerialName("id")
    val id:Int,
    @SerialName("instance__tickets__number")
    val instance__tickets__number:String,
    @SerialName("instance__tickets__properties")
    val instance__tickets__properties:List<InstanceTicketsProperties>,
    @SerialName("instance__tickets__id")
    val  instance__tickets__id:Int,
    @SerialName("instance__tickets__ticket_id")
    val  instance__tickets__ticket_id:Int,
    @SerialName("instance__tickets__state")
    val instance__tickets__state:String,
    @SerialName("instance__tickets__basic_information__values")
    val instance__tickets__basic_information__values:List<InstanceTicketsBasicInformationValues>,
    @SerialName("activity_id")
    val activity_id:Long,
    @SerialName("activity__title")
    val activity__title:String


){
    override fun toString(): String {
        return "Detail(instance__tickets__number=$instance__tickets__number,activity__title=$activity__title )"
    }
}
