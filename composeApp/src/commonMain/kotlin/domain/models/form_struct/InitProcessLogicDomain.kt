package domain.models.form_struct

import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class InitProcessLogicDomain (
    var shouldHide: Boolean = false,
    var required: Boolean = false,
    var disabled: Boolean = false,
    var readOnly: Boolean = false,
    var calculatedValue: String? = null,
    var validate: Boolean = false,
    @Transient
    var errorMessage: ResourceFormattedStringDesc? = null,
    var hasInitialMessage: Boolean = false,
    var isAutoFillLoading: Boolean = false,
) {
    override fun toString(): String {
        return "ProcessLogicDomain(shouldHide=$shouldHide, required=$required, disabled=$disabled,isAutoFillLoading=$isAutoFillLoading, readOnly=$readOnly, calculatedValue=$calculatedValue, validate=$validate, errorMessage=$errorMessage)"
    }

    fun ProcessLogicDomain.copy(): ProcessLogicDomain {
        return ProcessLogicDomain(
            shouldHide,
            required,
            disabled,
            readOnly,
            calculatedValue,
            validate,
            errorMessage,
            isAutoFillLoading,
        )
    }
}

