package utils

import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValidateDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlin.math.tan


suspend fun validateComponents(
    components: List<ComponentDomain>,
    firstCheck: Boolean,
): Map<String, List<StringDesc>> {
    val errors = mutableMapOf<String, List<ResourceFormattedStringDesc>>()
    Napier.log(LogLevel.ASSERT, tag = "validateComponent", message = components.toList().toString())
    fun updateComponentsRecursively(component: ComponentDomain) {
        when (component.type) {
            FormViewerTypes.TextField -> {

                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {
                    component.validate?.let { validate ->

                        val validationErrors = validateShortText(component, validate)
//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//                            errors[component.id.toString()] = listMessageError
//                        }
                        validationErrors?.let {
                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }
                }
            }

            FormViewerTypes.TextAREA -> {
                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {

                    component.validate?.let { validate ->


                        val validationErrors =
                            validateTextarea(component, validate, true)
//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//
//                            errors[component.id.toString()] = listMessageError
//                        }
                        validationErrors?.let {

                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }

                }
            }


            FormViewerTypes.Number -> {

                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {


                    component.validate?.let { validate ->

                        val validationErrors =
                            validateNumber(component, validate, true)
//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//
//                            errors[component.id.toString()] = listMessageError
//                        }
                        validationErrors?.let {

                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }
                }
            }

            FormViewerTypes.LatLong -> {
                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {

                    component.validate?.let { validate ->
                        val validationErrors =
                            validateLatLong(component, validate)
//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//
//                            errors[component.id.toString()] = listMessageError
//                        }
                        validationErrors?.let {

                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }

                }
            }


            FormViewerTypes.Phone -> {

                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {
                    component.validate?.let { validate ->

                        val validationErrors =
                            validatePhone(component, validate, true)

//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//                            errors[component.id.toString()] = listMessageError
//
//                        }
                        validationErrors?.let {

                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }
                }
            }

            FormViewerTypes.Email -> {
                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {

                    component.validate?.let { validate ->

                        val validationErrors =
                            validateEmail(component, validate, true)

//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//                            errors[component.id.toString()] = listMessageError
//
//                        }
                        validationErrors?.let {

                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }
                }
            }

            FormViewerTypes.Checklist,
            FormViewerTypes.Select,
            FormViewerTypes.Multi,
            FormViewerTypes.Radio -> {
                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {
                    component.validate?.let { validate ->

                        val validationErrors =
                            validateSelected(component, validate)
//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//                            errors[component.id.toString()] = listMessageError
//
//                        }
                        validationErrors?.let {

                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = validationErrors != null,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }

                    }
                }
            }


            FormViewerTypes.FileUpload -> {

                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {
                    component.validate?.let { validate ->
                        val validationErrors = validateFileUpload(
                            component,
                            validate,
                            null,
                            firstCheck
                        )
//                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
//                        if (validationErrors != null) {
//                            listMessageError.add(validationErrors)
//                            errors[component.id.toString()] = listMessageError
//                        }
                        validationErrors?.let {
                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    required = true,
                                    errorMessage = validationErrors,
                                    hasInitialMessage = firstCheck
                                )
                            )
                        }
                    }
                }
            }


            FormViewerTypes.Datetime,
            FormViewerTypes.Date,
            FormViewerTypes.Time,
            FormViewerTypes.ImageView -> {
                if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {
                    component.validate?.let { validate ->

                        val validationErrors =
                            validateRequired(component, validate)
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)
                            errors[component.id.toString()] = listMessageError

                        }
                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(
                                required = validationErrors != null,
                                errorMessage = validationErrors,
                                hasInitialMessage = firstCheck
                            )
                        )

                    }
                }
            }

            FormViewerTypes.Group -> {
                component.components.value?.map { subComponent ->
                    updateComponentsRecursively(subComponent)
                }
            }
        }
    }

    components.map { component ->
        updateComponentsRecursively(component)
    }

    return errors
}