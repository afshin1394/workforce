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
fun FilterCard(item:StateFilter,isSelected:Boolean,onItemSelected:()->Unit){

    Card(modifier = Modifier
                    .fillMaxWidth()
                   .padding(spacing1X)
        .clickable {

            onItemSelected()
        }

        .background(
            color = if (isSelected) surfaceSelected else surfaceDefault,
            shape = RoundedCornerShape(radius2XLarge)
        ),

        border = if (isSelected) BorderStroke(1.dp, strokeBrand) else null) {
        Text(

            text = item.title,
            color = if (isSelected) textBrand else textSecondary,
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
        StateFilter(4, "Suspended", false),
        StateFilter(5, "Completed", false),
        StateFilter(6, "All", false)


    ), updateFilter: (stateFilter: StateFilter) -> Unit = {}
) {



    var select by remember { mutableStateOf(-1) }
    LazyRow(modifier = modifier) {
        items(items) { item ->

            FilterCard(item=item,isSelected = select==item.id, onItemSelected = {
                select=item.id
                updateFilter(item)
            })


        }
    }
}

