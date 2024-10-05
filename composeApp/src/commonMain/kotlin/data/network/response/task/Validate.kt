package data.network.response.task

import data.network.INetworkObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Validate (
    @SerialName("id")
    val id : String?=null,
    @SerialName("key")
    val key : String?=null,
    @SerialName("hide")
    val hide : String?=null,
    @SerialName("layout")
    val layout : Layout?=null,
    @SerialName("subtype")
    val subtype : String? = null,
    @SerialName("required")
    val required : Boolean?= null,
) : INetworkObject {
    override fun hasNullProperty(): Boolean {
      return  (id == null || key == null || hide == null || layout == null || subtype == null || required == null)
    }

    override fun areAllMembersNull(): Boolean {
       return (id == null && key == null && hide == null && layout == null && subtype == null && required == null)
    }
}
