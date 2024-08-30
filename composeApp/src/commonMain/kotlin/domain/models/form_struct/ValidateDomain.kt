package domain.models.form_struct

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import data.network.response.task.Layout
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ValidateDomain(
    val id : String?=null,
    val key : String?=null,
    val hide : String?=null,
    val layout : Layout?=null,
    val subtype : String? = null,
    val required : Boolean?= null,
    val maxLength:Int?=null,
    val minLength :Int?=null,
    val pattern:String?=null,
    val maxTotalSize :Int?=null,
    val maxFileNumber :Int?=null,
    val max :Int?=null,
    val min :Int?=null,
    val blacklistAttachment :String?=null,
    val attachedValidationType:String?=null,
    val whitelistAttachment:String?=null,
    val domainType:String?=null,
    val domainList:String?=null,
    val validationType:String?=null,
    @Transient
    val messageError: ResourceFormattedStringDesc? = null
) 
