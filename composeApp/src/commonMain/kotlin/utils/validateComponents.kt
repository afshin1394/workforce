package utils

import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValidateDomain
import domain.models.form_struct.ValueDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlin.math.tan


fun validateComponents(
    components: List<ComponentDomain>,
    firstCheck: Boolean,
    fileSelected: MutableList<ValueDomain>?,
    listValueDomain: List<ValueDomain>?,

    initialCheckingFileUpload : Boolean = true,

    ): Map<String, List<StringDesc>> {
    val errors = mutableMapOf<String, List<ResourceFormattedStringDesc>>()
    components.map { component ->
        validateComponent(component,firstCheck, listValueDomain, initialCheckingFileUpload)
    }

    return errors
}

fun validateComponent(
    component: ComponentDomain,
    firstCheck: Boolean,
    listValueDomain: List<ValueDomain>?,
    initialCheckingFileUpload : Boolean = true,

    ): Map<String, List<StringDesc>> {
    val errors = mutableMapOf<String, List<ResourceFormattedStringDesc>>()
    when (component.type) {
        FormViewerTypes.TextField -> {

            if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {
                component.validate?.let { validate ->

                    val validationErrors = validateShortText(component, validate)
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)
                        errors[component.id.toString()] = listMessageError
                    }
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

        FormViewerTypes.TextAREA -> {
            if (!(component.disabled || component.processLogicDomain.value.disabled || component.processLogicDomain.value.shouldHide)) {

                component.validate?.let { validate ->


                    val validationErrors =
                        validateTextarea(component, validate, true)
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)

                        errors[component.id.toString()] = listMessageError
                    }
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
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)

                        errors[component.id.toString()] = listMessageError
                    }
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
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)

                        errors[component.id.toString()] = listMessageError
                    }
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

                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)
                        errors[component.id.toString()] = listMessageError

                    }
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

                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)
                        errors[component.id.toString()] = listMessageError

                    }
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
                    val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                    if (validationErrors != null) {
                        listMessageError.add(validationErrors)
                        errors[component.id.toString()] = listMessageError

                    }
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


                    val updatedList = component.values?.toMutableSet() ?: mutableSetOf()
                    listValueDomain?.let { updatedList.addAll(it) }

                    val validationErrors = validateFileUpload(
                        component,
                        validate,
                        updatedList.toMutableList(),
                        initialCheckingFileUpload
                    )

                    validationErrors?.let {
                        val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                        listMessageError.add(it)
                        errors[component.id.toString()] = listMessageError

                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(
                                required = true,
                                errorMessage = it,
                                hasInitialMessage = firstCheck
                            )
                        )
                    } ?: run {

                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(
                                required = updatedList.size==0,
                                errorMessage = null,
                                hasInitialMessage = if (updatedList.size==0) true else firstCheck
                            )
                        )

                        component.updateValues(updatedList.toList())

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
                validateComponent(subComponent,firstCheck, listValueDomain, initialCheckingFileUpload)
            }
        }
    }




    return errors
}