package utils


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.uuid4
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc

import domain.models.PhotoDomain

import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValidateDomain

import domain.models.form_struct.ValueDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

import presentation.screens.main.components.formViewer.CheckList
import presentation.screens.main.components.formViewer.DropDownMultiChoice
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.Editable
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.ImagePicker
import presentation.screens.main.components.formViewer.ModalDatePicker
import presentation.screens.main.components.formViewer.ModalTimePicker
import presentation.screens.main.components.formViewer.Radio
import presentation.screens.main.components.formViewer.TypeEditable
import presentation.screens.main.components.formViewer.UploadFileComponent
import presentation.screens.main.components.formViewer.groupComponent


@Composable
fun initialize(
    savedIndex : Int,
    savedParentIndex : Int,
    taskID: String,
    modifier: Modifier,
    photoDomainList: MutableList<PhotoDomain>,
    components: List<ComponentDomain>,
    onChanges: (componentDomain : ComponentDomain, listValueDomain: List<ValueDomain>?) -> Unit,
    onAddItem: (componentDomain: ComponentDomain, indexChild: Int,onComplete:(position : Int)->Unit) -> Unit,
    onRemoveItem: (componentDomain:ComponentDomain,  indexChild: Int,onComplete:(position : Int)->Unit) -> Unit,
    onClickImage: (indexPhotoSelected: Int, componentKey: String,componentId : String) -> Unit,
    currentParentIndex: List<Int> = listOf(),
) {
    Napier.log(LogLevel.ASSERT, tag = "indexFile", message = "indexFile${savedIndex}")
    val indexChildSaveable = rememberSaveable { mutableStateOf(savedIndex) }
    val indexParentSaveable = rememberSaveable{ mutableStateOf<Int?>(null) }
    val itemState = remember { mutableStateOf(ComponentDomain()) }
    val uploadDomainLists = remember { mutableStateMapOf<String, MutableState<List<ValueDomain>>>() }


    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Napier.log(LogLevel.ASSERT, tag = "initialize", message = "reinititt${components.toList()}")
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        itemsIndexed(components,key = { index, _ -> index }) { index, item ->

            val updatedParentIndex = currentParentIndex + index

            Column(modifier = Modifier.padding(8.dp)) {

                when (item.type) {
                    FormViewerTypes.Group -> {
                        indexParentSaveable.value = index


                        groupComponent(
                            parentIndex = savedParentIndex,
                            savedIndex= savedIndex,
                            taskID,
                            modifier.heightIn(0.dp, 1000.dp),
                            photoDomainList,
                            onChanges,
                            onAddItem,
                            onRemoveItem,
                            onClickImage,
                            updatedParentIndex,
                            item,

                            onAddClick = {

                                val newComponents =
                                    copyComponentWithValues(item, uuid4().toString())
                                onAddItem(newComponents,index){ position->
                                    scope.launch {
                                        listState.animateScrollToItem(index + position)
                                    }
                                }





                            },
                            onDeleteClick = {

                                val newComponents =
                                    item.id?.let { it1 -> removeComponentById(components, it1) }
                                newComponents?.let {
                                    onRemoveItem(newComponents, index){
                                        scope.launch {
                                            listState.animateScrollToItem(index - 1)
                                        }
                                    }

                                }


                            })


                    }

                    FormViewerTypes.Number -> {
                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }

                        var valueState = remember { mutableStateOf(item.values?.get(0)?.value ?: "") }


                        Editable(
                            processLogicDomain = item.processLogicDomain,
                            type = TypeEditable.NUMBER,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            errorMessage = if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            keyboardType = KeyboardType.Number,
                            readOnly = item.readOnly,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(),
                                    newValue
                                )
                                item.values = listOf(updatedValueDomain)
                                updateNumberValidationError(item, errorMessageState)




                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )

                            }
                        )
                    }


                    FormViewerTypes.TextAREA -> {
                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        var valueState = remember { mutableStateOf(item.values?.get(0)?.value ?: "") }

                        Editable(
                            processLogicDomain = item.processLogicDomain,
                            type = TypeEditable.TEXTAREA,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            errorMessage = if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            keyboardType = KeyboardType.Text,
                            readOnly = item.readOnly,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(),
                                    newValue
                                )
                                item.values = listOf(updatedValueDomain)
                                updateNumberValidationError(item, errorMessageState)



                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )
                    }

                    FormViewerTypes.TextField -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        var valueState = remember { mutableStateOf(item.values?.get(0)?.value ?: "") }

                        Editable(
                            processLogicDomain = item.processLogicDomain,
                            type = TypeEditable.SHORT_TEXT,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            errorMessage = if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            keyboardType = KeyboardType.Text,
                            readOnly = item.readOnly,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue
                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.values = listOf(updatedValueDomain)


                                updateShortTextValidationError(item, errorMessageState)



                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )
                    }


                    FormViewerTypes.LatLong -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        var valueState = remember { mutableStateOf(item.values?.get(0)?.value ?: "") }


                        Editable(
                            processLogicDomain = item.processLogicDomain,
                            type = TypeEditable.LATLONG,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            errorMessage = if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            keyboardType = KeyboardType.Number,
                            readOnly = item.readOnly,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.values = listOf(updatedValueDomain)


                                updateLatLongValidationError(item, errorMessageState)

                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )


                    }


                    FormViewerTypes.Phone -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        var valueState = remember { mutableStateOf(item.values?.get(0)?.value ?: "") }

                        Editable(
                            processLogicDomain = item.processLogicDomain,
                            type = TypeEditable.PHONE,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            errorMessage = if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            keyboardType = KeyboardType.Number,
                            readOnly = item.readOnly,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.values = listOf(updatedValueDomain)


                                updatePhoneValidationError(item, errorMessageState)


                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )


                    }

                    FormViewerTypes.Email -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        var valueState = remember { mutableStateOf(item.values?.get(0)?.value ?: "") }

                        Editable(
                            processLogicDomain = item.processLogicDomain,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = item.readOnly,
                            maxLines = 1,
                            errorMessage = if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            onValueChange = { newValue ->
                                valueState.value = newValue


                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(),
                                    newValue
                                )
                                item.values = listOf(updatedValueDomain)


                                updateEmailValidationError(item, errorMessageState)



                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )

                            }
                        )
                    }


                    FormViewerTypes.Datetime -> {


                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val selectedDateState = remember {
                            mutableStateOf(
                                item.values?.getOrNull(0)?.value ?: " "
                            )
                        }

                        ModalDateTimePicker(
                            readOnly = item.readOnly,
                            processLogicDomain = item.processLogicDomain,
                            selectedDateState.value,
                            item.label.toString(),
                            if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                        ) { dateSelected ->
                            val newValues =
                                listOf(ValueDomain(FormViewerTypes.Datetime, dateSelected))
                            item.values = newValues
                            selectedDateState.value = dateSelected

                            updateDateTimeValidationError(item, errorMessageState)

                            onChanges( item, newValues)
                        }
                    }


                    FormViewerTypes.Date -> {
                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val selectedDateState = remember {
                            mutableStateOf(
                                item.values?.getOrNull(0)?.value ?: " "
                            )
                        }

                        ModalDatePicker(
                            item.readOnly,
                            item.processLogicDomain,
                            selectedDateState.value,
                            item.label.toString(),
                            if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                        ) { dateSelected ->
                            val newValues = listOf(ValueDomain(FormViewerTypes.Date, dateSelected))
                            item.values = newValues
                            selectedDateState.value = dateSelected

                            updateDateTimeValidationError(item, errorMessageState)

                            onChanges( item, newValues)


                        }

                    }


                    FormViewerTypes.Time -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val selectedDateState = remember {
                            mutableStateOf(
                                item.values?.getOrNull(0)?.value ?: " "
                            )
                        }

                        ModalTimePicker(
                            item.readOnly,
                            item.processLogicDomain,
                            selectedDateState.value,
                            item.label.toString(),
                            if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,

                            ) { timeSelected ->
                            val newValues = listOf(ValueDomain(FormViewerTypes.Time, timeSelected))
                            item.values = newValues
                            selectedDateState.value = timeSelected

                            updateDateTimeValidationError(item, errorMessageState)

                            onChanges( item, newValues)


                        }

                    }


                    FormViewerTypes.FileUpload -> {
                           var selectedComponentObject = components[index]
                            // Ensure there is an upload list for this item key
                            if (!uploadDomainLists.containsKey(item.key)) {
                                uploadDomainLists[item.key!!] = mutableStateOf(
                                    components[index].values ?: item.values ?: emptyList()
                                )

                            }


                            val uploadDomainList = uploadDomainLists[item.key!!]!!

                            val initialMessageError: ResourceFormattedStringDesc =
                                item.validate?.messageError ?: ResourceFormattedStringDesc(
                                    MR.strings.empty_error_message,
                                    emptyList()
                                )

                            val errorMessage = remember { mutableStateOf(initialMessageError) }

                            UploadFileComponent(
                                index = index,
                                item = item,
                                label = item.key ?: "",
                                errorMessage = if (errorMessage.value == initialMessageError) errorMessage.value else initialMessageError,
                                modifier = Modifier,
                                uploadList = uploadDomainList.value,
                                onChooseFileFromDevice = { list ->
                                        val oldList = uploadDomainList.value.toMutableList()
                                        val newValues = mutableListOf<ValueDomain>()

                                        updateFileUploadValidationError(
                                            selectedComponentObject,
                                            list,
                                            errorMessage,
                                            newValues
                                        )

                                        // Update the list and remove duplicates
                                        oldList.addAll(newValues)
                                        val newList = oldList.distinct()

                                        uploadDomainList.value = newList
                                        components[indexChildSaveable.value].values = newList

                                        onChanges(
                                            item,
                                            selectedComponentObject.values
                                        )
                                    Napier.log(LogLevel.ASSERT,tag = "UploadFileComponent onClickUpload",message = components[indexChildSaveable.value].toString())


                                },
                                onClickUpload = {indexClick->
                                    indexChildSaveable.value = indexClick
                                    Napier.log(
                                        LogLevel.ASSERT,
                                        tag = "UploadFileComponent",
                                        message = index.toString()
                                    )
                                    Napier.log(LogLevel.ASSERT,tag = "UploadFileComponent onClickUpload",message = components[indexChildSaveable.value].toString())

                                },
                                onRemoveFile = { fileToRemove ->

                                    val newList = uploadDomainList.value.toMutableList()
                                    newList.remove(fileToRemove)
                                    uploadDomainList.value = newList
                                    item.values = newList
                                    Napier.log(LogLevel.ASSERT,tag = "UploadFileComponent onClickUpload",message = item.toString())

                                    onChanges(
                                        item,
                                        item.values
                                    )
                                }

                            )

                    }


                    FormViewerTypes.ImageView -> {
                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        ImagePicker(
                            index,
                            item,
                            taskID,
                            if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            findPhotosByComponentId(photoDomainList,item.id, item.key),
                            onTakePhoto = { obj ,resultTakePhoto ->
                                (obj as ComponentDomain? )?.let {
                                    Napier.log(LogLevel.ASSERT,tag = "takePhoto onTakePhoto",message =it.toString())

                                    val newValues =
                                    listOf(ValueDomain("${obj?.type}:${obj?.id}", "${resultTakePhoto}"))
                                    it.values = newValues
                                updateImageViewValidationError(it, errorMessageState)

                                onChanges( it, newValues)
                                }

                            },
                            onImageClick = {
                                onClickImage(it, item.key ?: "",item.id?:"")
                            }, onCameraClick = { item ->
                                itemState.value = item
                                Napier.log(LogLevel.ASSERT,tag = "takePhoto onCameraClick",message =components[index].toString())

                            })
                    }


                    FormViewerTypes.Radio -> {
                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val componentLabel = item.label
                        val valuesState = remember {
                            mutableStateListOf(*item.values?.toTypedArray() ?: arrayOf())
                        }

                        Radio(
                            item.readOnly,
                            item.processLogicDomain ?: ProcessLogicDomain().copy(),
                            componentLabel.toString(),
                            if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            valuesState,
                            valuesState.firstOrNull { it.isSelected }?.label ?: ""
                        ) { selectedValue ->

                            valuesState.forEachIndexed { index, item ->
                                valuesState[index] = if (item.label == selectedValue) {
                                    item.copy(isSelected = true)
                                } else {
                                    item.copy(isSelected = false)
                                }
                            }
                            item.values = valuesState

                            updateSelectedComponentValidationError(item, errorMessageState)



                            onChanges( item, valuesState)
                        }
                    }

                    FormViewerTypes.Checklist -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val valuesState = remember { mutableStateOf("") }


                        val componentLabel = item.label
                        item.values?.let { values ->
                            CheckList(
                                item.readOnly,
                                item.processLogicDomain,
                                componentLabel.toString(),
                                if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                                valuesState.value,
                                values,

                                onSelect = { valueDomain ->


                                    updateSelectedComponentValidationError(item, errorMessageState)


                                    valuesState.value = valueDomain.value ?: ""
                                    Napier.log(
                                        LogLevel.ASSERT,
                                        tag = "valueStateee",
                                        message = valuesState.value
                                    )
                                    onChanges(
                                        item,
                                        listOf(valueDomain)
                                    )
                                }
                            )
                        }
                    }


                    FormViewerTypes.Multi -> {

                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val valuesState = remember { mutableStateOf("") }
                        val componentLabel = item.label
                        item.values?.let { values ->
                            if (item.isMulti) {
                                DropDownMultiChoice(
                                    item.readOnly,
                                    item.processLogicDomain,
                                    componentLabel.toString(),
                                    if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                                    "",
                                    valuesState.value,
                                    values, onItemSelected = { selectedItems, oneItem ->

                                        updateValuesForSelectType(values, selectedItems)


                                        valuesState.value = oneItem.value ?: ""

                                        updateSelectedComponentValidationError(
                                            item,
                                            errorMessageState
                                        )

                                        onChanges(
                                            item,
                                            item.values
                                        )
                                    },
                                    onSearchButtonClicked = {}
                                )
                            }

                        }
                    }


                    FormViewerTypes.Select -> {
                        val initialMessageError: ResourceFormattedStringDesc =
                            item.validate?.messageError ?: ResourceFormattedStringDesc(
                                MR.strings.empty_error_message,
                                emptyList()
                            )

                        val errorMessageState = remember { mutableStateOf(initialMessageError) }
                        val componentLabel = item.label
                        val valuesState = remember {
                            mutableStateListOf(
                                *item.values?.toTypedArray() ?: arrayOf()
                            )
                        }

                        DropDownSingleChoice(
                            item.readOnly,
                            item.processLogicDomain,
                            componentLabel.toString(),
                            if (errorMessageState.value == initialMessageError) errorMessageState.value else initialMessageError,
                            valuesState,
                            valuesState.firstOrNull { it.isSelected }?.label ?: "",
                            "",
                            { selectedValue ->
                                valuesState.forEachIndexed { index, item ->
                                    valuesState[index] = if (item.label == selectedValue) {
                                        item.copy(isSelected = true)
                                    } else {
                                        item.copy(isSelected = false)
                                    }
                                }
                                item.values = valuesState

                                updateSelectedComponentValidationError(item, errorMessageState)

                                onChanges( item, valuesState)
                            },
                            {}
                        )
                    }

                }

            }
        }


    }

}


private fun updateValuesForSelectType(values: List<ValueDomain>, selectedItems: List<ValueDomain>) {
    for (item in values) {
        item.isSelected = selectedItems.any { it.label == item.label }
    }
}


fun copyComponentWithValues(
    component: ComponentDomain,
    uuid: String = uuid4().toString()
): ComponentDomain {
    val updatedValidate = component.validate?.copy(
        messageError = ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    )

    val values =
        if (component.type == FormViewerTypes.Select || component.type == FormViewerTypes.Multi || component.type == FormViewerTypes.Checklist || component.type == FormViewerTypes.Radio) {
            component.values?.map { value ->
                value.copy(isSelected = false)
            }
        } else {
            null
        }

    val copiedComponents = component.components?.map { subComponent ->
        copyComponentWithValues(subComponent, uuid4().toString())
    }?.toMutableList()

    return component.copy(
        id = "${component.id}copy$uuid",
        components = copiedComponents,
        values = values?.toMutableList(),
        removable = true,
        validate = updatedValidate
    )
}

fun removeComponentById(components: List<ComponentDomain>, id: String): ComponentDomain? {
    val mutableComponents = components.toMutableList()
    val iterator = mutableComponents.iterator()
    while (iterator.hasNext()) {
        val component = iterator.next()
        if (component.id == id) {
            iterator.remove()
            return component.copy(removable = false)
        }
    }
    return null
}



fun findPhotosByComponentId(components: List<PhotoDomain>,id: String?, key: String?): MutableList<PhotoDomain> {
    val list: MutableList<PhotoDomain> = arrayListOf()
    components.forEach {
        if (it.component_key == key && it.componentId == id) {
            list.add(it)
        }
    }
    return list
}

fun updateValueDomain(
    valueDomain: ValueDomain,
    newValue: String,

    ): ValueDomain {

    return valueDomain.copy(
        value = newValue,
    )
}


fun updateNumberValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors = validateNumber(item, item.validate ?: ValidateDomain(), null)
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

fun updateTextareaValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors = validateTextarea(item, item.validate ?: ValidateDomain(), null)
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

fun updateShortTextValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors =
        validateShortText(item, item.validate ?: ValidateDomain())
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

fun updateLatLongValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors =
        validateLatLong(item, item.validate ?: ValidateDomain())
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

fun updatePhoneValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors =
        validatePhone(item, item.validate ?: ValidateDomain(), null)
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

fun updateEmailValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors = validateEmail(item, item.validate ?: ValidateDomain(), null)
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

fun updateDateTimeValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {

    val validationErrors = validateRequired(item, item.validate ?: ValidateDomain())
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate

}

fun updateFileUploadValidationError(
    item: ComponentDomain,
    list: List<ValueDomain>,
    errorMessageState: MutableState<ResourceFormattedStringDesc>,
    newValues: MutableList<ValueDomain>
) {
    var tempErrorMessage = ResourceFormattedStringDesc(MR.strings.empty_error_message, emptyList())

    list.forEachIndexed { index, valueMap ->
        val tempErrors = validateFileUpload(
            item,
            item.validate ?: ValidateDomain(),
            mutableListOf(valueMap),
            null
        )
        if (tempErrors == null) {
            newValues.add(
                ValueDomain(
                    "${item.type}:${item.id}/${valueMap.label}",
                    valueMap.value
                )
            )

            errorMessageState.value = tempErrorMessage
            val updatedValidate = item.validate?.copy(
                messageError = ResourceFormattedStringDesc(
                    MR.strings.empty_error_message,
                    emptyList()
                )
            )
            item.validate = updatedValidate
        } else {
            if (index == 0) {
                tempErrorMessage = tempErrors ?: ResourceFormattedStringDesc(
                    MR.strings.empty_error_message,
                    emptyList()
                )

                errorMessageState.value = tempErrorMessage
                val updatedValidate = item.validate?.copy(
                    messageError = tempErrorMessage
                )
                item.validate = updatedValidate
            }
        }
    }
}

fun updateImageViewValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {
    val validationErrors = validateRequired(item, item.validate ?: ValidateDomain())
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)

    item.validate = updatedValidate
}

fun updateSelectedComponentValidationError(
    item: ComponentDomain,
    errorMessageState: MutableState<ResourceFormattedStringDesc>
) {


    val validationErrors = validateSelected(item, item.validate ?: ValidateDomain())
    val messageError: ResourceFormattedStringDesc = validationErrors
        ?: ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    errorMessageState.value = messageError
    val updatedValidate = item.validate?.copy(messageError = messageError)
    item.validate = updatedValidate
}

