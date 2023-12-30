package com.irancell.nwg.wfm.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.model.StateFilter

import com.irancell.nwg.wfm.presentation.theme.*

import presentation.theme.body_large
import presentation.theme.strokeBrand
import presentation.theme.surfaceDefault
import presentation.theme.surfaceSelected
import presentation.theme.textBrand
import presentation.theme.textSecondary


@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    stateFilter: StateFilter,
    isActive: Boolean = false,
    onClick: (stateFilter: StateFilter) -> Unit = {}
) {


    var isActiveState by remember {
        mutableStateOf(stateFilter.isActive)
    }

    Card(modifier = modifier
        .clickable {
            isActiveState = !isActiveState
            onClick(stateFilter)
        }

        .background(
            color = if (isActiveState) surfaceSelected else surfaceDefault,
            shape = RoundedCornerShape(radius2XLarge)
        ),

        border = if (isActiveState) BorderStroke(1.dp, strokeBrand) else null) {
        Text(

            text = stateFilter.title,
            color = if (isActiveState) textBrand else textSecondary,
            style = body_large,

            modifier = Modifier
                .padding(vertical = spacing05X, horizontal = spacing15X)
        )
    }


}

@Composable
fun FilterRow(
    modifier: Modifier = Modifier,
    items: List<StateFilter> = arrayListOf(
        StateFilter(1, "Pending", false),
        StateFilter(2, "Doing", false),
        StateFilter(3, "Done", false),
        StateFilter(3, "Suspended", false),
        StateFilter(3, "Completed", false)


    ), updateFilter: (stateFilter: StateFilter) -> Unit = {}
) {
    val itemsState by remember {
         mutableStateOf(items)
    }


    LazyRow(modifier = modifier) {
        items(itemsState) { item ->
            FilterCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing1X), isActive = item.isActive,
                stateFilter = item
            ) { stateFilter ->
                    itemsState.map() {  if (it.id != stateFilter.id) it.isActive = false }



//                  itemsState.map{
//                      if (it.id !=stateFilter.id)
//                          it.isActive = false
//                  }
            }

        }
    }
}

