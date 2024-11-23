package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.model.SelectableItemStringResource
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.getSharedPref
import presentation.theme.body_large
import presentation.theme.brand_blue_3
import presentation.theme.surfaceDefault
import presentation.theme.textBrand
import presentation.theme.textSecondary
import utils.Language
import utils.ModeApp
import utils.UpdateType


@Composable
fun SelectableSingleLanguageItemComponentStringResource(
    selectableItems: MutableList<SelectableItemStringResource>,
    itemSelected:String,
    onOptionSelected: (index: Int, selectableItem: SelectableItemStringResource) -> Unit,

    ) {

    val selectableItemsState by remember {
        mutableStateOf(selectableItems)
    }


    Column(
        Modifier
            .background(color = surfaceDefault)
            .padding(spacing2X)
    ) {




        var select by remember { mutableStateOf(itemSelected) }


        LazyColumn() {
            itemsIndexed(items = selectableItemsState) { index, item ->

                val text = item.selectItemType

                OptionsItemComponentStringResource(item=item
                    ,isSelected = select== item.selectItemType.lowercase().take(2),
                    onItemSelected = {
                    select= text.lowercase().take(2)
                    if (getSharedPref().getString(Language)!=select){
                        onOptionSelected(index, item)
                    }

                })

            }
        }
    }


}



@Composable
fun SelectableSingleModeAppItemComponentStringResource(
    selectableItems: MutableList<SelectableItemStringResource>,
    itemSelected:String,
    onOptionSelected: (index: Int, selectableItem: SelectableItemStringResource) -> Unit,

    ) {

    val selectableItemsState by remember {
        mutableStateOf(selectableItems)
    }


    Column(
        Modifier
            .background(color = surfaceDefault)
            .padding(spacing2X)
    ) {




        var select by remember { mutableStateOf(itemSelected) }


        LazyColumn() {
            itemsIndexed(items = selectableItemsState) { index, item ->

                val text = item.selectItemType

                OptionsItemComponentStringResource(item=item
                    ,isSelected = select== item.selectItemType.lowercase(),
                    onItemSelected = {
                        select= text.lowercase()
                        if (getSharedPref().getString(ModeApp)!=select){
                            onOptionSelected(index, item)
                        }

                    })

            }
        }
    }


}


@Composable
fun SelectableSingleUpdateTypeItemComponentStringResource(
    selectableItems: MutableList<SelectableItemStringResource>,
    itemSelected:String,
    onOptionSelected: (index: Int, selectableItem: SelectableItemStringResource) -> Unit,

    ) {

    val selectableItemsState by remember {
        mutableStateOf(selectableItems)
    }


    Column(
        Modifier
            .background(color = surfaceDefault)
            .padding(spacing2X)
    ) {




        var select by remember { mutableStateOf(itemSelected) }


        LazyColumn() {
            itemsIndexed(items = selectableItemsState) { index, item ->

                val text = item.selectItemType

                OptionsItemComponentStringResource(item=item
                    ,isSelected = select== item.selectItemType.lowercase(),
                    onItemSelected = {
                        select= text.lowercase().take(2)
                        if (getSharedPref().getString(UpdateType)!=select){
                            onOptionSelected(index, item)
                        }

                    })

            }
        }
    }


}

@Composable
fun OptionsItemComponentStringResource(item: SelectableItemStringResource, isSelected:Boolean, onItemSelected:()->Unit){


    Surface(
        modifier = Modifier.
        fillMaxWidth()
            .padding(spacing1X)
            .clickable {

                onItemSelected()
            },
        tonalElevation = 3.dp,
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) brand_blue_3 else surfaceDefault
    ) {

        Text(
            text = stringResource(item.text),
            color = if (isSelected) textBrand else textSecondary,
            style = body_large,
            modifier = Modifier
                .padding(vertical = spacing15X, horizontal = spacing15X)
        )
    }


}