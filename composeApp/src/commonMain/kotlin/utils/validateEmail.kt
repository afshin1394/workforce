package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValidateDomain
import irancell.nwg.wfm.MR


fun validateEmail(
    component: ComponentDomain,
    validateDomain: ValidateDomain,
    initialChecking: Boolean?
): ResourceFormattedStringDesc? {
    val required = validateDomain.required ?: false
    val domainList =
        validateDomain.domainList?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
    val domainType = validateDomain.domainType ?: ""
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    val value = component.values?.getOrNull(0)?.value ?: ""




        if (required && value.isEmpty()){
            return StringDesc.ResourceFormatted(MR.strings.email_required)

        }






        if (!emailPattern.matches(value)) {
            return StringDesc.ResourceFormatted(
                MR.strings.invalid_email_format
            )
        }


        val emailDomain = value.substringAfter("@").substringBeforeLast(".").lowercase()
        when (domainType) {
            "Blacklist" -> {
                if (domainList.contains(emailDomain)) {
                    return StringDesc.ResourceFormatted(
                        MR.strings.domain_error_message_black,
                        validateDomain.domainList ?: ""
                    )
                }
            }

            "Whitelist" -> {
                if (!domainList.contains(emailDomain)) {
                    return StringDesc.ResourceFormatted(
                        MR.strings.domain_error_message_White,
                        validateDomain.domainList ?: ""
                    )
                }
            }
        }






    return null
}
