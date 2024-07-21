package utils

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValidateDomain
import domain.models.initialForm.ValueDomain
import irancell.nwg.wfm.File
import irancell.nwg.wfm.MR

fun validateFileUpload(
    component: ComponentDomain,
    validateDomain: ValidateDomain,
    selectList: MutableList<ValueDomain>?,
    initialChecking: Boolean?
): ResourceFormattedStringDesc? {
    val errors = mutableListOf<String>()
    errors.clear()
    val required = validateDomain.required ?: false
    val maxTotalSize = validateDomain.maxTotalSize ?: Int.MAX_VALUE
    val maxFileNumber = validateDomain.maxFileNumber ?: Int.MAX_VALUE
    val blacklistAttachment =
        validateDomain.blacklistAttachment?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
    val whitelistAttachment =
        validateDomain.whitelistAttachment?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
    val validationType = validateDomain.attachedValidationType

    val values = component.values ?: emptyList()
    if (initialChecking == true) {
        if (required && values.isEmpty()) {
            return StringDesc.ResourceFormatted(
                MR.strings.field_required
            )
        }
    } else {
        val totalSize = selectList!!.sumOf { File(it.value ?: "").sizeInMB() }
        if (totalSize > maxTotalSize) {

            return StringDesc.ResourceFormatted(
                MR.strings.file_total_size,maxTotalSize
            )
        }

        if (values.size >= maxFileNumber) {

            return StringDesc.ResourceFormatted(
                MR.strings.file_total_count,maxFileNumber
            )
        }

        selectList.forEach { valueDomain ->
            val fileExtension = File(valueDomain.value ?: "").extension().lowercase()
            when (validationType) {
                "Blacklist" -> {
                    if (blacklistAttachment.contains(fileExtension)) {

                        return StringDesc.ResourceFormatted(
                            MR.strings.file_type_black,validateDomain.blacklistAttachment?: ""
                        )
                    }
                }
                "Whitelist" -> {
                    if (!whitelistAttachment.contains(fileExtension)) {
                        return StringDesc.ResourceFormatted(
                            MR.strings.file_type_white,validateDomain.whitelistAttachment?:""
                        )
                    }
                }
            }
        }
    }

    return null
}