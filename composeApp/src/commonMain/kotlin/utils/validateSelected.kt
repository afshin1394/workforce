package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValidateDomain
import irancell.nwg.wfm.MR

fun validateSelected (component: ComponentDomain, validateDomain: ValidateDomain):  ResourceFormattedStringDesc? {
    val errors = mutableListOf<String>()
    val required = validateDomain.required ?: false

    val values = component.values ?: emptyList()

    if (required && values.none { it.isSelected }) {
        return StringDesc.ResourceFormatted(
            MR.strings.field_required
        )
    }

    return null
}