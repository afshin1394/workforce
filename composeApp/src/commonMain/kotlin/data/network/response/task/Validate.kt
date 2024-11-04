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
    @SerialName("maxLength")
    val maxLength:Int?=null,
    @SerialName("minLength")
    val minLength :Int?=null,
    @SerialName("pattern")
    val pattern:String?=null,
    @SerialName("maxTotalSize")
    val maxTotalSize :Int?=null,
    @SerialName("maxFileNumber")
    val maxFileNumber :Int?=null,
    @SerialName("max")
    val max :Int?=null,
    @SerialName("min")
    val min :Int?=null,
    @SerialName("blacklistAttachment")
    val blacklistAttachment :String?=null,
    @SerialName("attachedValidationType")
    val attachedValidationType:String?=null,
    @SerialName("whitelistAttachment")
    val whitelistAttachment:String?=null,
    @SerialName("domainType")
    val domainType:String?=null,
    @SerialName("domainList")
    val domainList:String?=null,
    @SerialName("validationType")
    val validationType:String?=null,
) : INetworkObject {
    override fun hasNullProperty(): Boolean {
      return  (id == null || key == null || hide == null || layout == null || subtype == null || required == null || maxLength == null || minLength == null || pattern == null || maxTotalSize == null || maxFileNumber == null || max==null || min == null || blacklistAttachment == null || whitelistAttachment == null || domainType==null || domainList == null || validationType == null)
    }

    override fun areAllMembersNull(): Boolean {
       return (id == null && key == null && hide == null && layout == null && subtype == null && required == null && maxLength == null && minLength == null && pattern == null && maxTotalSize == null && maxFileNumber == null && max==null && min == null && blacklistAttachment == null && whitelistAttachment == null && domainType==null && domainList == null && validationType == null)
    }
}
