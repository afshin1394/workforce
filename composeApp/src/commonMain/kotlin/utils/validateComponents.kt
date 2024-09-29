package utils

import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValidateDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlin.math.tan


fun validateComponents(
    components: List<ComponentDomain>,
    clearTempComponentListCallback: (List<ComponentDomain>) -> Unit
): Map<String, List<StringDesc>> {
    val errors = mutableMapOf<String, List<ResourceFormattedStringDesc>>()
    Napier.log(LogLevel.ASSERT, tag = "validateComponent", message = components.toList().toString())

    fun updateComponentsRecursively(component: ComponentDomain): ComponentDomain {
        return when (component.type) {
            FormViewerTypes.TextField -> {

                if (!component.disabled||!component.processLogicDomain.disabled) {
                    component.validate?.let { validate ->

                        val validationErrors = validateShortText(component, validate)
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)
                            errors[component.id.toString()] = listMessageError
                        }
                        component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run {
                        return component
                    }
                } else {
                    return component
                }
            }

            FormViewerTypes.TextAREA -> {
                if (!component.disabled||!component.processLogicDomain.disabled) {

                    component.validate?.let { validate ->

                        val validationErrors =
                            validateTextarea(component, validate, true)
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)

                            errors[component.id.toString()] = listMessageError
                        }
                        component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run {
                        return component
                    }

                } else {
                    return component
                }
            }


            FormViewerTypes.Number -> {

                if (!component.disabled||!component.processLogicDomain.disabled) {


                    component.validate?.let { validate ->

                        val validationErrors =
                            validateNumber(component, validate, true)
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)

                            errors[component.id.toString()] = listMessageError
                        }

                        component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run {
                        return component
                    }
                } else {
                    return component
                }
            }

            FormViewerTypes.LatLong -> {
                if (!component.disabled||!component.processLogicDomain.disabled) {

                    component.validate?.let { validate ->
                        val validationErrors =
                            validateLatLong(component, validate)
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)

                            errors[component.id.toString()] = listMessageError
                        }
                        return component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run {
                        return component
                    }

                } else {
                    return component
                }
            }


            FormViewerTypes.Phone -> {

                if (!component.disabled||!component.processLogicDomain.disabled) {
                    component.validate?.let { validate ->

                        val validationErrors =
                            validatePhone(component, validate, true)

                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)
                            errors[component.id.toString()] = listMessageError

                        }

                        component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run {
                        return component
                    }
                } else {
                    return component
                }
            }

            FormViewerTypes.Email -> {
                if (!component.disabled||!component.processLogicDomain.disabled) {

                    component.validate?.let { validate ->

                        val validationErrors =
                            validateEmail(component, validate, true)

                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)
                            errors[component.id.toString()] = listMessageError

                        }

                        component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run {
                        return component
                    }
                } else {
                    return component
                }
            }

            FormViewerTypes.Checklist,
            FormViewerTypes.Select,
            FormViewerTypes.Multi,
            FormViewerTypes.Radio -> {
                if (!component.disabled||!component.processLogicDomain.disabled) {
                    component.validate?.let { validate ->

                        val validationErrors =
                            validateSelected(component, validate)
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        if (validationErrors != null) {
                            listMessageError.add(validationErrors)
                            errors[component.id.toString()] = listMessageError

                        }
                        component.copy(
                            validate = component.validate?.copy(
                                messageError = validationErrors
                            ) ?: ValidateDomain(
                                messageError = validationErrors
                            )
                        )
                    } ?: run { return component }
                } else {
                    return component
                }
            }


            FormViewerTypes.FileUpload -> {

                if (!component.disabled||!component.processLogicDomain.disabled) {
                component.validate?.let { validate ->
                    val validationErrors = validateFileUpload(
                        component,
                        validate,
                        null,
                        true
                    )
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)
                        errors[component.id.toString()] = listMessageError

                    }
                    component.copy(
                        validate = component.validate?.copy(
                            messageError = validationErrors
                        ) ?: ValidateDomain(
                            messageError = validationErrors
                        )
                    )
                } ?: run {
                    return component
                }
                } else {
                    return component
                }
            }


            FormViewerTypes.Datetime,
            FormViewerTypes.Date,
            FormViewerTypes.Time,
            FormViewerTypes.ImageView -> {
                if (!component.disabled||!component.processLogicDomain.disabled) {
                component.validate?.let { validate ->

                    val validationErrors =
                        validateRequired(component, validate)
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)
                        errors[component.id.toString()] = listMessageError

                    }
                    component.copy(
                        validate = component.validate?.copy(
                            messageError = validationErrors
                        ) ?: ValidateDomain(
                            messageError = validationErrors
                        )
                    )
                } ?: run {
                    return component
                }
                } else {
                    return component
                }
            }

            FormViewerTypes.Group -> {
                val updatedSubComponents = component.components?.map { subComponent ->
                    updateComponentsRecursively(subComponent)
                } ?: emptyList()

                component.copy(components = updatedSubComponents)
            }

            else -> component
        }
    }

    val updatedComponents = components.map { component ->
        updateComponentsRecursively(component)
    }
    clearTempComponentListCallback.invoke(updatedComponents)

    return errors
}