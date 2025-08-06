package utils


import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
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
import domain.models.form_struct.ValueDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
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
    isChild: Boolean,
    scrollingState: Pair<Int, Int>,
    savedIndex: Int,
    savedParentIndex: Int,
    taskID: String,
    modifier: Modifier,
    photoDomainList: MutableList<PhotoDomain>,
    components: List<ComponentDomain>,
    onChanges: (componentDomain: ComponentDomain, listValueDomain: List<ValueDomain>?) -> Unit,
    onAddItem: (componentDomain: ComponentDomain, indexChild: Int, onComplete: (position: Int) -> Unit) -> Unit,
    onRemoveItem: (componentDomain: ComponentDomain, indexChild: Int, onComplete: (position: Int) -> Unit) -> Unit,
    onClickImage: (indexPhotoSelected: Int, componentKey: String, componentId: String, componentDomain: ComponentDomain) -> Unit,
    currentParentIndex: List<Int> = listOf(),
) {
    val componentsState = remember { components }
    Napier.log(LogLevel.ASSERT, tag = "indexFile", message = "indexFile${savedIndex}")
    val indexChildSaveable = rememberSaveable { mutableStateOf(savedIndex) }
    val indexParentSaveable = rememberSaveable { mutableStateOf<Int?>(null) }
    val itemState = remember { mutableStateOf(ComponentDomain()) }
    val uploadDomainLists =
        remember { mutableStateMapOf<String, MutableState<List<ValueDomain>>>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(scrollingState) {
        Napier.log(
            LogLevel.ASSERT,
            tag = "scrollingState",
            message = "first-->${scrollingState.first} second-->${scrollingState.second}"
        )
        if (isChild) {
            if (scrollingState.second != -1)
                listState.animateScrollToItem(scrollingState.second)
        } else {
            if (scrollingState.first != -1)
                listState.animateScrollToItem(scrollingState.first)
        }
    }
    Napier.log(LogLevel.ASSERT, tag = "initialize", message = "reinititt${components.toList()}")
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        itemsIndexed(componentsState, key = { _, item -> item.id!! }) { index, item ->
            val valuesState by item.valuesState
            val nestedComponentsState by remember { item.components }
            val processLogicDomainState by remember { item.processLogicDomain }
            val updatedParentIndex = currentParentIndex + index

            Column(modifier = Modifier.padding(8.dp)) {
                when (item.type) {
                    FormViewerTypes.Group -> {
                        indexParentSaveable.value = index
                        groupComponent(
                            disable = item.disabled,
                            readOnly = item.readOnly,
                            processLogicDomainState,
                            true,
                            scrollingState = scrollingState,
                            parentIndex = savedParentIndex,
                            savedIndex = savedIndex,
                            taskID,
                            modifier.heightIn(0.dp, 1000.dp),
                            photoDomainList,
                            onChanges,
                            onAddItem,
                            onRemoveItem,
                            onClickImage,
                            updatedParentIndex,
                            item,
                            nestedComponentsState,
                            onAddClick = {
                                val newComponents = nestedComponentsState?.let {
                                    copyComponentWithValues(
                                        item,
                                        nestedComponentsState,
                                        uuid4().toString()
                                    )
                                }
                                newComponents?.let {
                                    onAddItem(newComponents, index) { position ->
                                        scope.launch {
                                            listState.animateScrollToItem(index + position)
                                        }
                                    }
                                }

                            },
                            onDeleteClick = {

                                onRemoveItem(item, index) {
                                    scope.launch {
                                        listState.animateScrollToItem(index - 1)
                                    }
                                }
                            })
                    }

                    FormViewerTypes.Number -> {
                        val valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")

                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.NUMBER,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Number,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(),
                                    newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))

                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )
                    }

                    FormViewerTypes.TextAREA -> {
                        val valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")

                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.TEXTAREA,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Text,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(),
                                    newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))

                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )
                    }

                    FormViewerTypes.TextField -> {
                        var valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")

                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.SHORT_TEXT,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Text,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->

                                valueState.value = newValue
                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))
                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )
                    }


                    FormViewerTypes.LatLong -> {


                        var valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")


                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.LATLONG,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Number,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))

                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )


                    }


                    FormViewerTypes.Phone -> {


                        var valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")

                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.PHONE,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Number,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue

                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))
                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )


                    }

                    FormViewerTypes.Email -> {


                        var valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")

                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue


                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(),
                                    newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))

                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )

                            }
                        )
                    }


                    FormViewerTypes.Datetime -> {


                        val selectedDateState = remember {
                            mutableStateOf(
                                item.values?.getOrNull(0)?.value ?: " "
                            )
                        }

                        ModalDateTimePicker(
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            processLogicDomain = processLogicDomainState,
                            selectedDateState.value,
                            item.label.toString(),
                        ) { dateSelected ->
                            val newValues =
                                listOf(ValueDomain(FormViewerTypes.Datetime, dateSelected))
                            item.values = newValues
                            selectedDateState.value = dateSelected

                            onChanges(item, newValues)
                        }
                    }


                    FormViewerTypes.Date -> {

                        val selectedDateState = remember {
                            mutableStateOf(
                                item.values?.getOrNull(0)?.value ?: " "
                            )
                        }

                        ModalDatePicker(
                            item.readOnly,
                            item.disabled,
                            processLogicDomainState,
                            selectedDateState.value,
                            item.label.toString(),
                        ) { dateSelected ->
                            val newValues = listOf(ValueDomain(FormViewerTypes.Date, dateSelected))
                            item.values = newValues
                            selectedDateState.value = dateSelected
                            onChanges(item, newValues)


                        }
                    }

                    FormViewerTypes.Time -> {
                        val selectedDateState = remember {
                            mutableStateOf(
                                item.values?.getOrNull(0)?.value ?: " "
                            )
                        }

                        ModalTimePicker(
                            item.readOnly,
                            item.disabled,
                            processLogicDomainState,
                            selectedDateState.value,
                            item.label.toString(),
                        ) { timeSelected ->
                            val newValues = listOf(ValueDomain(FormViewerTypes.Time, timeSelected))
                            item.values = newValues
                            selectedDateState.value = timeSelected
                            onChanges(item, newValues)
                        }
                    }

                    FormViewerTypes.FileUpload -> {

                        val currentId = item.id ?: ""
                        var filteredValuesState =
                            valuesState?.filter { it.value?.contains(currentId) == true }
                        UploadFileComponent(
                            item.readOnly,
                            item.disabled,
                            processLogicDomainState,
                            index = index,
                            item = item,
                            label = item.label ?: "",
                            uploadList = filteredValuesState,
                            onChooseFileFromDevice = { list ->
                                onChanges(
                                    item,
                                    list,
                                )
                            },
                            onClickUpload = { indexClick ->
                                indexChildSaveable.value = indexClick

                            },
                            onRemoveFile = { fileToRemove ->

                                val newList = filteredValuesState?.toMutableList()
                                newList?.remove(fileToRemove)
                                filteredValuesState = newList!!
                                item.values = newList
                                onChanges(
                                    item,
                                    item.values
                                )
                            }
                        )
                    }

                    FormViewerTypes.ImageView -> {
                        ImagePicker(
                            item.disabled,
                            item.readOnly,
                            processLogicDomainState,
                            item,
                            findPhotosByComponentId(photoDomainList, item.id, item.key),
                            onTakePhoto = { obj, resultTakePhoto ->
                                (obj as ComponentDomain?)?.let {
                                    Napier.log(
                                        LogLevel.ASSERT,
                                        tag = "takePhoto onTakePhoto",
                                        message = it.toString()
                                    )

                                    val newValues =
                                        listOf(
                                            ValueDomain(
                                                "${obj?.type}:${obj?.id}",
                                                resultTakePhoto
                                            )
                                        )
                                    it.values = newValues
                                    onChanges(it, newValues)
                                }

                            },
                            onImageClick = {
                                onClickImage(it, item.key ?: "", item.id ?: "", item)
                            }, onCameraClick = { item ->
                                itemState.value = item
                                Napier.log(
                                    LogLevel.ASSERT,
                                    tag = "takePhoto onCameraClick",
                                    message = components[index].toString()
                                )
                            })
                    }

                    FormViewerTypes.Radio -> {
                        val componentLabel = item.label
                        val valuesState = remember {
                            mutableStateListOf(*item.values?.toTypedArray() ?: arrayOf())
                        }
                        Radio(
                            item.readOnly,
                            item.disabled,
                            processLogicDomainState ?: ProcessLogicDomain().copy(),
                            componentLabel.toString(),
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
                            onChanges(item, valuesState)
                        }
                    }

                    FormViewerTypes.Checklist -> {
                        val valuesState = remember { mutableStateOf("") }
                        val componentLabel = item.label
                        item.values?.let { values ->
                            CheckList(
                                item.readOnly,
                                item.disabled,
                                processLogicDomainState,
                                componentLabel.toString(),
                                valuesState.value,
                                values,
                                onSelect = { valueDomain ->
                                    valuesState.value = valueDomain.value ?: ""

                                    onChanges(
                                        item,
                                        listOf(valueDomain)
                                    )
                                }
                            )
                        }
                    }

                    FormViewerTypes.Multi -> {
                        val valuesState = remember { mutableStateOf("") }
                        val componentLabel = item.label
                        item.values?.let { values ->
                            if (item.isMulti) {
                                DropDownMultiChoice(
                                    item.readOnly,
                                    item.disabled,
                                    processLogicDomainState,
                                    componentLabel.toString(),
                                    "",
                                    valuesState.value,
                                    values, onItemSelected = { selectedItems, oneItem ->
                                        updateValuesForSelectType(values, selectedItems)
                                        valuesState.value = oneItem?.value ?: ""

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

                        val componentLabel = item.label
                        val valuesState = remember {
                            mutableStateListOf(
                                *item.values?.toTypedArray() ?: arrayOf()
                            )
                        }

                        DropDownSingleChoice(
                            item.readOnly,
                            item.disabled,
                            processLogicDomainState,
                            componentLabel.toString(),
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
                                onChanges(item, valuesState)
                            },
                            {}
                        )
                    }

                    FormViewerTypes.Email -> {
                        var valueState =
                            mutableStateOf(item.values?.get(0)?.value ?: "")

                        Editable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = item.readOnly,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->
                                valueState.value = newValue
                                val updatedValueDomain = updateValueDomain(
                                    item.values?.get(0) ?: ValueDomain(), newValue
                                )
                                item.updateValues(listOf(updatedValueDomain))
                                onChanges(
                                    item,
                                    listOf(updatedValueDomain)
                                )
                            }
                        )
                    }

                    FormViewerTypes.GridField -> {
                        // GridField is a complex component that may contain nested components
                        // For now, handle as a group with nested components
                        Napier.log(LogLevel.WARNING, tag = "FormViewer", message = "GridField component not fully implemented, rendering as group")
                        val nestedComponents = nestedComponentsState
                        if (nestedComponents != null && nestedComponents.isNotEmpty()) {
                            initialize(
                                isChild = true,
                                scrollingState = scrollingState,
                                savedIndex = savedIndex,
                                savedParentIndex = savedParentIndex,
                                taskID = taskID,
                                modifier = modifier.heightIn(0.dp, 1000.dp),
                                photoDomainList = photoDomainList,
                                components = nestedComponents,
                                onChanges = onChanges,
                                onAddItem = onAddItem,
                                onRemoveItem = onRemoveItem,
                                onClickImage = onClickImage,
                                currentParentIndex = updatedParentIndex
                            )
                        }
                    }

                    else -> {
                        // Default case for unrecognized component types
                        Napier.log(LogLevel.WARNING, tag = "FormViewer", message = "Unrecognized component type: ${item.type} for component ${item.id}/${item.key}")
                        
                        // Try to render as text field if it has values that can be edited
                        if (item.values != null && item.values!!.isNotEmpty()) {
                            var valueState = mutableStateOf(item.values?.get(0)?.value ?: "")
                            
                            Editable(
                                processLogicDomain = processLogicDomainState,
                                type = TypeEditable.SHORT_TEXT,
                                value = valueState.value,
                                placeholder = "${item.label} (${item.type})",
                                imeAction = ImeAction.None,
                                keyboardType = KeyboardType.Text,
                                readOnly = item.readOnly,
                                disable = item.disabled,
                                maxLines = 1,
                                onValueChange = { newValue ->
                                    valueState.value = newValue
                                    val updatedValueDomain = updateValueDomain(
                                        item.values?.get(0) ?: ValueDomain(), newValue
                                    )
                                    item.updateValues(listOf(updatedValueDomain))
                                    onChanges(
                                        item,
                                        listOf(updatedValueDomain)
                                    )
                                }
                            )
                        } else {
                            // If it has nested components, try to render them
                            val nestedComponents = nestedComponentsState
                            if (!nestedComponents.isNullOrEmpty()) {
                                initialize(
                                    isChild = true,
                                    scrollingState = scrollingState,
                                    savedIndex = savedIndex,
                                    savedParentIndex = savedParentIndex,
                                    taskID = taskID,
                                    modifier = modifier.heightIn(0.dp, 1000.dp),
                                    photoDomainList = photoDomainList,
                                    components = nestedComponents,
                                    onChanges = onChanges,
                                    onAddItem = onAddItem,
                                    onRemoveItem = onRemoveItem,
                                    onClickImage = onClickImage,
                                    currentParentIndex = updatedParentIndex
                                )
                            } else {
                                // Show a placeholder text for completely unrecognized components
                                Text(
                                    text = "${item.label ?: "Unknown Component"} (${item.type})",
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.Gray
                                )
                            }
                        }
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
    nestedComponents: List<ComponentDomain>?,
    uuid: String = uuid4().toString()
): ComponentDomain {

    // Update the validation message
    val updatedValidate = component.validate?.copy(
        messageError = ResourceFormattedStringDesc(
            MR.strings.empty_error_message,
            emptyList()
        )
    )

    // Determine values based on component type
    val values = when (component.type) {
        FormViewerTypes.Select,
        FormViewerTypes.Multi,
        FormViewerTypes.Checklist,
        FormViewerTypes.Radio -> {
            component.values?.map { value ->
                value.copy(isSelected = false) // Reset selection for these types
            }
        }

        else -> null
    }

    // Create copies of nested components that are directly related to the current component
    val copiedComponents = component.components.value?.map { subComponent ->
        copyComponentWithValues(
            subComponent,
            subComponent.components.value,
            uuid4().toString()
        ) // Recursive call with relevant nested components
    }?.toMutableList() ?: mutableListOf() // Default to empty list if no components

    // Return a new instance of ComponentDomain with updated properties
    return component.copy(
        id = "${component.id}copy$uuid", // Unique ID for the copied component
        _components = copiedComponents, // Updated nested components
        values = values?.toMutableList(), // Updated values list
        removable = true, // Mark as removable
        validate = updatedValidate // Updated validation
    )
}


fun findPhotosByComponentId(
    components: List<PhotoDomain>,
    id: String?,
    key: String?
): MutableList<PhotoDomain> {
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




