@file:Suppress("NAME_SHADOWING")

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import domain.models.PhotoDomain

import domain.models.initialForm.ComponentDomain

import domain.models.initialForm.ValueDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

import presentation.screens.main.components.formViewer.CheckList
import presentation.screens.main.components.formViewer.DropDownMultiChoice
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.Editable
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.ImagePicker
import presentation.screens.main.components.formViewer.Radio
import presentation.screens.main.components.formViewer.TypeEditable
import presentation.screens.main.components.formViewer.UploadFileComponent
import presentation.screens.main.components.formViewer.groupComponent


@Composable
fun initialize(
    taskID: String,
    modifier: Modifier,
    photoDomainList: MutableList<PhotoDomain>,
    components: List<ComponentDomain>,
    onChanges: (list: List<ComponentDomain>, listValueDomain: List<ValueDomain>?, indexParent: List<Int>, indexChild: Int) -> Unit,
    onClickImage: (indexPhotoSelected: Int, componentId: String) -> Unit,
    currentParentIndex: List<Int> = listOf()
) {



    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var counter by remember { mutableStateOf(0) }


    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        itemsIndexed(components) { index, item ->

            val updatedParentIndex = currentParentIndex + index


            Column(modifier = Modifier.padding(8.dp)) {

                when (item.type) {
                    FormViewerTypes.Group -> {


                        groupComponent(
                            taskID,
                            modifier.heightIn(0.dp, 1000.dp),
                            photoDomainList,
                            onChanges,
                            onClickImage,
                            updatedParentIndex,
                            item,
                            onAddClick = {

                                val newComponents =listOf( copyComponentWithValues(item,counter))
                                counter++
                                onChanges(newComponents,null,currentParentIndex,index)
                                scope.launch {
                                    listState.scrollToItem(index + 1)
                                }


                            },
                            onDeleteClick = {

                                val newComponents = item.id?.let { it1 -> removeComponentById(components, it1) }
                                onChanges(newComponents!! ,null,currentParentIndex,index)

                                scope.launch {
                                    listState.animateScrollToItem(index - 1)
                                }


                            })


                    }


                    FormViewerTypes.Checklist -> {
                        val componentLabel = item.label
                        item.values?.let { values ->
                            CheckList(
                                componentLabel.toString(),
                                values,
                                onSelect =  { valueDomain->
                                    onChanges(components, listOf(valueDomain),currentParentIndex,index)
                                }
                            )
                        }
                    }


                    FormViewerTypes.Datetime -> {


                        ModalDateTimePicker(
                            item.values?.getOrNull(0)?.value ?: item.label.toString(),
                            item.label.toString(),

                            ) { dateSelected ->
                            val newValues = listOf(ValueDomain("datetime", "$dateSelected"))
                            item.values = newValues
                            onChanges(components, newValues, currentParentIndex, index)


                        }
                    }

                    FormViewerTypes.Email -> {
                        Editable(
                            TypeEditable.EMAIL,
                            "",
                            ImeAction.None,
                            keyboardType = KeyboardType.Email,
                            readOnly = false,
                            maxLines = 1
                        ) {
                        }
                    }

                    FormViewerTypes.FileUpload -> {
                        val uploadDomainList =
                            remember { mutableStateOf(item.values ?: emptyList()) }

                        UploadFileComponent(
                            titlePicker = item.label.toString(),
                            modifier = Modifier,
                            uploadList = uploadDomainList.value,
                            onSelected = { list ->
                                val oldList = uploadDomainList.value
                                val newValues = list.map { valueMap ->
                                    ValueDomain(
                                        "${item.type}:${item.id}/${valueMap.label}",
                                        valueMap.value
                                    )
                                }
                                val newList = (oldList + newValues).distinct()
                                uploadDomainList.value = newList
                                item.values = newList

                                onChanges(components, newValues, currentParentIndex, index)
                            }
                        )
                    }

                    FormViewerTypes.GridField -> {
                    }

                    FormViewerTypes.ImageView -> {

                        ImagePicker(
                            item.label.toString(),
                            taskID,
                            findPhotosByComponentId(photoDomainList, item.id),
                            onTakePhoto = {

                                val newValues =
                                    listOf(ValueDomain("${item.type}:${item.id}", "${it}"))
                                item.values = newValues
                                onChanges(components, newValues, currentParentIndex, index)

                            },
                            onImageClick = {

                                onClickImage(it, item.id ?: "")
                            })
                    }

                    FormViewerTypes.LatLong -> {}

                    FormViewerTypes.Radio -> {
                        val componentLabel = item.label
                        item.values?.let { values ->
                            Radio(
                                componentLabel.toString(),
                                values,
                                values.firstOrNull { it.isSelected }?.label ?: ""
                            ) { selectedValue ->
                                val updatedValues = values.map { item ->
                                    if (item.label == selectedValue) {
                                        item.copy(isSelected = true)
                                    } else {
                                        item.copy(isSelected = false)
                                    }
                                }
                                item.values = updatedValues
                                Napier.log(
                                    LogLevel.ASSERT,
                                    tag = "RadioChanges",
                                    message = item.values.toString()
                                )
                                onChanges(components, updatedValues, currentParentIndex, index)
                            }
                        }
                    }

                    FormViewerTypes.Phone -> {
                        Editable(
                            TypeEditable.PHONE,
                            "",
                            ImeAction.None,
                            keyboardType = KeyboardType.Phone,
                            readOnly = false,
                            maxLines = 1
                        ) {}
                    }

                    FormViewerTypes.TextField -> {
                        Editable(
                            TypeEditable.LONG_TEXT,
                            "",
                            ImeAction.None,
                            keyboardType = KeyboardType.Text,
                            readOnly = false,
                            maxLines = 4
                        ) {}
                    }

                    FormViewerTypes.Select -> {
                        val componentLabel = item.label
                        item.values?.let { values ->
                            if (item.isMulti) {
                                DropDownMultiChoice(
                                    componentLabel.toString(), "",
                                    values, onItemSelected = { selectedItems ->
                                        updateValuesForSelectType(values, selectedItems)
                                        onChanges(components, item.values, currentParentIndex, index)
                                    },
                                    onSearchButtonClicked = {}
                                )
                            } else {
                                DropDownSingleChoice(
                                    componentLabel.toString(),
                                    values,
                                    values.firstOrNull { it.isSelected }?.label ?: "",
                                    "",
                                    { selectedValue ->
                                        val updatedValues = mutableListOf<ValueDomain>()
                                        for (item in values) {
                                            updatedValues.add(
                                                if (item.label == selectedValue) {
                                                    item.copy(isSelected = true)
                                                } else {
                                                    item.copy(isSelected = false)
                                                }
                                            )
                                        }
                                        item.values = updatedValues
                                        onChanges(components, updatedValues, currentParentIndex, index)
                                    },
                                    {}
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

fun copyComponentWithValues(component: ComponentDomain, counter: Int): ComponentDomain {




    if (component.type == FormViewerTypes.Select||component.type ==FormViewerTypes.Checklist||component.type== FormViewerTypes.Radio) {
        val values = component.values?.map { value ->
            value.copy(isSelected = false)
        }
        return component.copy(
            id = "${component.id}copy$counter",
            components = component.components?.map { copyComponentWithValues(it, counter) }
                ?.toMutableList(),
            values = values?.toMutableList(),
            removable = true
        )


    }else{

        return component.copy(
            id = "${component.id}copy$counter",
            components = component.components?.map { copyComponentWithValues(it, counter) }
                ?.toMutableList(),
            values = null,
            removable = true
        )

    }






}

fun removeComponentById(components: List<ComponentDomain>, id: String): List<ComponentDomain> {
    val mutableComponents = components.toMutableList()
    val iterator = mutableComponents.iterator()
    while (iterator.hasNext()) {
        val component = iterator.next()
        if (component.id == id) {
            iterator.remove()
            break
        }
    }
    return mutableComponents
}




fun findPhotosByComponentId(components: List<PhotoDomain>, id: String?): MutableList<PhotoDomain> {
    val list: MutableList<PhotoDomain> = arrayListOf()
    components.forEach {
        if (it.component_key == id) {
            list.add(it)
        }
    }
    return list
}


