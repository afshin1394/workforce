package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValidateDomain
import irancell.nwg.wfm.MR

fun validatePhone(component: ComponentDomain, validateDomain: ValidateDomain ,initialChecking: Boolean?): ResourceFormattedStringDesc? {
    val required = validateDomain.required ?: false


    val mobilePattern = "^09\\d{9}$".toRegex()
    val landlinePattern = "^[1-8]\\d{9}$".toRegex()

    val value = component.values?.getOrNull(0)?.value ?: ""


        if (required && value.isEmpty()){
            return StringDesc.ResourceFormatted(
                MR.strings.field_required
            )

        }



    if (value.isNotEmpty() && !mobilePattern.matches(value) && !landlinePattern.matches(value)) {
        return StringDesc.ResourceFormatted(
            MR.strings.invalid_phone_format
        )
    }

    return null
}