package domain.models

import kotlinx.serialization.Serializable



@Serializable
data class PhotoDomain(
    val pk:Long = 0,
    val ticket_number: String,
    val componentId : String,
    val component_key: String,
    val index_row: Long,
    val origin_uri: String,
    var edited_uri: String,
    val angle: Float,
    var isFirstClick: Boolean = true

){


    constructor(
        ticket_number: String,
        componentId: String,
        component_key: String,
        index_row: Long,
        origin_uri: String,
        edited_uri: String,
        angle: Float):this(0,ticket_number,componentId,component_key,index_row,origin_uri,edited_uri,angle)

    override fun toString(): String {
        return "PhotoDomain(pk=$pk, ticket_number='$ticket_number', componentId='$componentId', component_key='$component_key', index_row=$index_row, origin_uri='$origin_uri', edited_uri='$edited_uri', angle='$angle')"
    }


}