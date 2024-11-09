package com.irancell.nwg.wfm.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.screens.main.components.OptionsItemComponent
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import presentation.components.CustomSearchBar
import presentation.theme.surfaceDefault


@Composable
fun SelectableComponentPreview(
    selectableItems: MutableList<SelectableItem>,
    onOptionClick: (index : Int,selectableItem: SelectableItem) -> Unit,
    onSearch : (searchContent : String) -> Unit
) {


    SelectableComponent(
        searchContent = "",
        selectableItems = selectableItems,
        onOptionSelected = { index, selectableItem ->

                onOptionClick(index,selectableItem)

        }, onSearch = {
            onSearch(it)
        })

}

@Composable
fun SelectableComponent(
    selectableItems: MutableList<SelectableItem> =
        mutableStateListOf(
            SelectableItem(1, "Equipment failure", false),
            SelectableItem(2, "Weather condition", false),
            SelectableItem(3, "Travel restrictions", false),
            SelectableItem(4, "Location Constraints", false),
            SelectableItem(5, "Blocked road", false),
            SelectableItem(6, "Other", false)

        ),
    hasSearch : Boolean = false,
    onOptionSelected: (index: Int, selectableItem: SelectableItem) -> Unit ,
    onSearch : (searchContent : String) -> Unit = {},
    searchContent: String = ""
) {
    var searchQuery by remember {
        mutableStateOf(searchContent)
    }
    val selectableItemsState by remember {
        mutableStateOf(selectableItems)
    }


    Column(
        Modifier
            .background(color = surfaceDefault)
            .padding(spacing2X)
    ) {
        if (hasSearch) {
            CustomSearchBar(hasFilter = false, textFieldState = searchQuery, updatedText = {
                searchQuery = it
                onSearch(searchQuery)
            })
            Spacer(modifier = Modifier.padding(vertical = spacing1X))
        }
        LazyColumn() {
            itemsIndexed(items = selectableItemsState) { index, item ->
                OptionsItemComponent(selectableItem =
                SelectableItem(
                    item.id,
                    text = item.text,
                    isSelected = item.isSelected,
                ),
                    onOptionClick = { option ->

                        onOptionSelected(index, option)
                    })

            }
        }
    }
}


