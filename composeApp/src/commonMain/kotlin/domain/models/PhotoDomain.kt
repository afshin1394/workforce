package domain.models

import kotlinx.serialization.Serializable


data class PhotoDomain(
    val pk:Long=0,
    val component_key: Long,
    val index_row: Long,
    val origin_uri: String,
    val edited_uri: String,
    val angle: String
){
    constructor(    component_key: Long,
                    index_row: Long,
                    origin_uri: String,
                    edited_uri: String,
                    angle: String):this(0,component_key,index_row,origin_uri,edited_uri,angle)
}