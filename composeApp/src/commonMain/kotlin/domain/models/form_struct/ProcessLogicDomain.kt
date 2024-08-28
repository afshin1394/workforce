package domain.models.form_struct

import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import kotlinx.serialization.Serializable

data class ProcessLogicDomain(var shouldHide : Boolean = false,var required : Boolean= false,var disabled : Boolean= false,var readOnly : Boolean= false,var calculatedValue : String? = null,var validate : Boolean = false,var errorMessage : ResourceFormattedStringDesc? = null)
{
    override fun toString(): String {
        return "ProcessLogicDomain(shouldHide=$shouldHide, required=$required, disabled=$disabled, readOnly=$readOnly, calculatedValue=$calculatedValue, validate=$validate, errorMessage=$errorMessage)"
    }
    fun ProcessLogicDomain.copy() : ProcessLogicDomain{
        return ProcessLogicDomain(shouldHide,required,disabled,readOnly, calculatedValue,validate,errorMessage)
    }
}

