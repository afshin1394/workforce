package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValidateDomain
import irancell.nwg.wfm.MR

fun validateShortText(component: ComponentDomain,validateDomain: ValidateDomain): ResourceFormattedStringDesc? {
    val errors = mutableListOf<String>()
    val required = validateDomain.required ?: false
    val maxLength = validateDomain.maxLength ?: Int.MAX_VALUE
    val minLength =validateDomain.minLength ?: 0
    val pattern= validateDomain.pattern ?:""

    val value = component.processLogicDomain.value.calculatedValue?:component.values?.getOrNull(0)?.value ?: ""

    if (required && value.isEmpty()) {
        return StringDesc.ResourceFormatted(
            MR.strings.field_required
        )
    }
    if (value.length > maxLength) {
        return StringDesc.ResourceFormatted(
            MR.strings.maximum_length,maxLength
        )
    }
    if (value.length < minLength) {
        return StringDesc.ResourceFormatted(
            MR.strings.must_length,minLength
        )
    }

    if (pattern!=""&&!Regex(pattern).matches(value)){
        return StringDesc.ResourceFormatted(
            MR.strings.invalid_format
        )
    }

    return null
}