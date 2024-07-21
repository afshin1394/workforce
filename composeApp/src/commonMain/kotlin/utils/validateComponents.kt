package utils

import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValidateDomain


fun validateComponents(
    components: List<ComponentDomain>,
    clearTempComponentListCallback: (List<ComponentDomain>) -> Unit
): Map<String, List<StringDesc>> {
    val errors = mutableMapOf<String, List<ResourceFormattedStringDesc>>()

    fun updateComponentsRecursively(component: ComponentDomain): ComponentDomain {
        return when (component.type) {
            FormViewerTypes.TextField -> {
                val validationErrors =
                    validateShortText(component, component.validate ?: ValidateDomain())
                val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                if (validationErrors!=null){
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
            }


            FormViewerTypes.TextAREA -> {
                val validationErrors =
                    validateTextarea(component, component.validate ?: ValidateDomain(),true)
                val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                if (validationErrors!=null){
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
            }


            FormViewerTypes.Number -> {
                val validationErrors =
                    validateNumber(component, component.validate ?: ValidateDomain(),true)
                val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                if (validationErrors!=null){
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
            }

            FormViewerTypes.LatLong -> {
                val validationErrors =
                    validateLatLong(component, component.validate ?: ValidateDomain())
                val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                if (validationErrors!=null){
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
            }

            FormViewerTypes.Phone -> {
                val validationErrors =
                    validatePhone(component, component.validate ?: ValidateDomain(),true)

                val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                if (validationErrors!=null){
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
            }

         FormViewerTypes.Email -> {


             val validationErrors =
                    validateEmail(component, component.validate ?: ValidateDomain(),true)

             val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
             if (validationErrors!=null){
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
            }

           FormViewerTypes.Checklist,
            FormViewerTypes.Select,
            FormViewerTypes.Multi,
            FormViewerTypes.Radio ->{
                val validationErrors = validateSelected(component, component.validate ?: ValidateDomain())
               val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
               if (validationErrors!=null){
                   listMessageError.add(validationErrors)
                   errors[component.id.toString()] = listMessageError

               }
                component.copy(validate = component.validate?.copy(
                    messageError = validationErrors
                ) ?: ValidateDomain(
                    messageError = validationErrors
                )
                )
            }


           FormViewerTypes.FileUpload -> {
                val validationErrors = validateFileUpload(
                    component,
                    component.validate ?: ValidateDomain(),
                    null,
                    true
                )
               val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
               if (validationErrors!=null){
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
            }



            FormViewerTypes.Datetime,
            FormViewerTypes.Date,
            FormViewerTypes.Time,
            FormViewerTypes.ImageView -> {
                val validationErrors = validateRequired(component, component.validate ?: ValidateDomain())
                val listMessageError: ArrayList<ResourceFormattedStringDesc> = arrayListOf()
                if (validationErrors!=null){
                    listMessageError.add(validationErrors)
                    errors[component.id.toString()] = listMessageError

                }
                component.copy(validate = component.validate?.copy(
                    messageError = validationErrors
                ) ?: ValidateDomain(
                    messageError = validationErrors
                )
                )
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