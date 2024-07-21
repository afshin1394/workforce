package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValidateDomain
import irancell.nwg.wfm.MR

fun validateNumber(component: ComponentDomain, validateDomain: ValidateDomain,initialChecking: Boolean?): ResourceFormattedStringDesc?  {
    val required = validateDomain.required ?: false
    val max = validateDomain.max ?: Int.MAX_VALUE
    val min = validateDomain.min ?: Int.MIN_VALUE

    val valueStr = component.values?.getOrNull(0)?.value ?: ""
    val value = valueStr.toIntOrNull()


        if (required && valueStr.isEmpty()) {
            return StringDesc.ResourceFormatted(
                MR.strings.field_required
            )

        }



        if (value == null) {

            return StringDesc.ResourceFormatted(
                MR.strings.invalid_number_format
            )
        } else {
            if (value > max) {

                return StringDesc.ResourceFormatted(
                    MR.strings.maximum_value,max
                )
            }
            if (value < min) {


                return StringDesc.ResourceFormatted(
                    MR.strings.must_value,min
                )
            }


    }


    return null
}