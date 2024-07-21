package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValidateDomain
import irancell.nwg.wfm.MR

fun validateTextarea(component: ComponentDomain, validateDomain: ValidateDomain,initialChecking: Boolean?): ResourceFormattedStringDesc? {
    val errors = mutableListOf<String>()
    val required = validateDomain.required ?: false
    val maxLength = validateDomain.maxLength ?: Int.MAX_VALUE
    val minLength =validateDomain.minLength ?: 0


    val value = component.values?.getOrNull(0)?.value ?: ""



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



    return null

}