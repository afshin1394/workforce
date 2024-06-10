package domain.models

import kotlinx.serialization.Serializable


data class PhotoDomain(
    val pk:Long=0,
    val task_id: Long,
    val component_key: String,
    val index_row: Long,
    val origin_uri: String,
    var edited_uri: String,
    val angle: String
){


    constructor(
        taskId: Long,

        component_key: String,
                    index_row: Long,
                    origin_uri: String,
                    edited_uri: String,
                    angle: String):this(0,taskId,component_key,index_row,origin_uri,edited_uri,angle)


}