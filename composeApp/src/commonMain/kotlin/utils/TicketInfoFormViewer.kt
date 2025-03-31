


package utils


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.uuid4
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.InitComponentDomain
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
import presentation.screens.main.components.formViewer.TicketInfoCheckList
import presentation.screens.main.components.formViewer.TicketInfoDateTime
import presentation.screens.main.components.formViewer.TicketInfoDropDownMultiChoice
import presentation.screens.main.components.formViewer.TicketInfoDropDownSingleChoice
import presentation.screens.main.components.formViewer.TicketInfoEditable

import presentation.screens.main.components.formViewer.TicketInfoRadio
import presentation.screens.main.components.formViewer.TypeEditable
import presentation.screens.main.components.formViewer.UploadFileComponent
import presentation.screens.main.components.formViewer.groupComponent
import presentation.screens.main.components.formViewer.ticketInfoGroupComponent

@Composable
fun ticketInfoInitialize(
    modifier: Modifier,
    components: List<InitComponentDomain>,

) {
    val componentsState = remember { components }

    val indexParentSaveable = rememberSaveable { mutableStateOf<Int?>(null) }
    val itemState = remember { mutableStateOf(ComponentDomain()) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()


    Napier.log(LogLevel.ASSERT, tag = "initialize", message = "reinititt${components.toList()}")
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        itemsIndexed(componentsState, key = { _, item -> item.id!! }) { index, item ->

            val nestedComponentsState by remember { item.components }
            val processLogicDomainState by remember { item.processLogicDomain }


            Column(modifier = Modifier.padding(8.dp)) {
                when (item.type) {
                    FormViewerTypes.Group -> {
                        indexParentSaveable.value = index
                        ticketInfoGroupComponent(

                            readOnly = item.readOnly,
                            processLogicDomainState,


                            modifier.heightIn(0.dp, 1000.dp),

                            item,
                            nestedComponentsState,
                        )

                    }

                    FormViewerTypes.Number -> {
                        val valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoEditable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.NUMBER,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Number,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->



                            }
                        )
                    }

                    FormViewerTypes.TextAREA -> {
                        val valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoEditable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.TEXTAREA,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Text,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->

                            }
                        )
                    }

                    FormViewerTypes.TextField -> {
                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoEditable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.SHORT_TEXT,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Text,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->

                            }
                        )
                    }


                    FormViewerTypes.LatLong -> {


                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString()?: "")


                        TicketInfoEditable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.LATLONG,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Number,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->

                            }
                        )


                    }


                    FormViewerTypes.Phone -> {


                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString()?: "")

                        TicketInfoEditable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.PHONE,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Number,
                            readOnly =true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->

                            }
                        )


                    }

                    FormViewerTypes.Email -> {


                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoEditable(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->


                            }
                        )
                    }


                    FormViewerTypes.Datetime -> {

                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoDateTime(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->


                            }
                        )
                    }


                    FormViewerTypes.Date -> {

                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoDateTime(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->


                            }
                        )
                    }

                    FormViewerTypes.Time -> {
                        var valueState =
                            mutableStateOf(item.injected_value.toSafeString() ?: "")

                        TicketInfoDateTime(
                            processLogicDomain = processLogicDomainState,
                            type = TypeEditable.EMAIL,
                            value = valueState.value,
                            placeholder = item.label.toString(),
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = true,
                            disable = item.disabled,
                            maxLines = 1,
                            onValueChange = { newValue ->


                            }
                        )
                    }

                    FormViewerTypes.FileUpload -> {

               /*         val currentId = item.id ?: ""
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

                            },
                            onClickUpload = { indexClick ->

                            },
                            onRemoveFile = { fileToRemove ->


                            }
                        )*/
                    }

                    FormViewerTypes.ImageView -> {
                        /*ImagePicker(
                            item.disabled,
                            item.readOnly,
                            processLogicDomainState,
                            item,
                            findPhotosTicketInfoByComponentId(photoDomainList, item.id, item.key),
                            onTakePhoto = { obj, resultTakePhoto ->


                            },
                            onImageClick = {

                            }, onCameraClick = { item ->

                            })*/
                    }

                    FormViewerTypes.Radio -> {
                        val componentLabel = item.label


                        val valuesState = remember {
                            val raw = item.injected_value.toSafeString().orEmpty()
                            val values = when {
                                raw.isBlank() -> emptyList()
                                raw.contains(",") -> raw.split(",").map { it.trim() }
                                else -> listOf(raw.trim())
                            }
                            mutableStateListOf(*values.toTypedArray())
                        }


                        val selectedItem = remember {
                            valuesState.firstOrNull() ?: ""
                        }

                        TicketInfoRadio(
                            readOnly = true,
                            disable = item.disabled,
                            processLogicDomain = processLogicDomainState,
                            title = componentLabel.orEmpty(),
                            itemList = valuesState,
                            selectItem = selectedItem
                        ) { selectedValue ->

                        }
                    }
                    FormViewerTypes.Checklist -> {
                        val componentLabel = item.label


                        val itemList = remember {
                            val raw = item.injected_value.toSafeString().orEmpty()
                            when {
                                raw.isBlank() -> emptyList()
                                raw.contains(",") -> raw.split(",").map { it.trim() }
                                else -> listOf(raw.trim())
                            }
                        }


                        val selectedItems = remember {
                            mutableStateListOf<String>().apply {
                                addAll(itemList)
                            }
                        }

                        TicketInfoCheckList(
                            readOnly = true,
                            disable = item.disabled,
                            processLogicDomain = processLogicDomainState,
                            title = componentLabel.orEmpty(),
                            selectedItems = selectedItems,
                            itemList = itemList
                        ) { updatedSelection ->

                        }
                    }

                    FormViewerTypes.Multi -> {
                        val componentLabel = item.label


                        val options = remember {
                            val raw = item.injected_value.toSafeString().orEmpty()
                            when {
                                raw.isBlank() -> emptyList()
                                raw.contains(",") -> raw.split(",").map { it.trim() }
                                else -> listOf(raw.trim())
                            }
                        }


                        val selectedItems = remember {
                            mutableStateListOf<String>().apply {
                                addAll(options)
                            }
                        }

                        TicketInfoDropDownMultiChoice(
                            readOnly = true,
                            disable = item.disabled,
                            processLogicDomain = processLogicDomainState,
                            titleDropDown = componentLabel.toString(),
                            searchText = "",
                            itemList = options,
                            selectedItems = selectedItems,
                            onItemSelected = { updatedList, selectedItem ->

                            },
                            onSearchButtonClicked = { query ->

                            }
                        )
                    }
                    FormViewerTypes.Select -> {
                        val componentLabel = item.label

                        val options = remember {
                            val raw = item.injected_value.toSafeString().orEmpty()
                            when {
                                raw.isBlank() -> emptyList()
                                raw.contains(",") -> raw.split(",").map { it.trim() }
                                else -> listOf(raw.trim())
                            }
                        }

                        val selectedItem = remember { options.firstOrNull() ?: "" }

                        TicketInfoDropDownSingleChoice(
                            readOnly = true,
                            disable = item.disabled,
                            processLogicDomain = processLogicDomainState,
                            titleDropDown = componentLabel.toString(),
                            itemList = options,
                            selectItem = selectedItem,
                            searchText = "",
                            onItemSelected = { selectedValue ->
                                // Handle selection
                            },
                            onSearchButtonClicked = { query ->
                                // Handle search
                            }
                        )
                    }
                }
            }
        }
    }
}







fun findPhotosTicketInfoByComponentId(
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



data class ValueDomain(
    val label: String,
    val value: String
)


