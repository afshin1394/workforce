package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValidateDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR

fun validateTextarea(component: ComponentDomain, validateDomain: ValidateDomain): ResourceFormattedStringDesc? {
    val required = validateDomain.required ?: false
    val maxLength = validateDomain.maxLength ?: Int.MAX_VALUE
    val minLength =validateDomain.minLength ?: 0


    val value = component.values?.getOrNull(0)?.value ?: ""
    Napier.log(LogLevel.ASSERT, tag = "validateShortText", message =  component.label.toString())

    Napier.log(LogLevel.ASSERT, tag = "validateShortText", message =  value)

    Napier.log(LogLevel.ASSERT, tag = "validateShortText", message = (required && value.isEmpty()).toString() )


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