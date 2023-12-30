package com.irancell.nwg.wfm.presentation.screens.main.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.theme.*

import presentation.theme.body_large
import presentation.theme.surfaceDefault
import presentation.theme.surfaceSelected
import presentation.theme.textPrimary


@Composable
fun OptionsItemComponent(modifier: Modifier = Modifier,selectableItem: SelectableItem = SelectableItem(1,"Unbelievable",false)
                         ,onOptionClick : (selectableItem: SelectableItem) -> Unit = {}){

    val selectableItemState by remember {
        mutableStateOf(selectableItem)
    }


    val cardColor = if (selectableItemState.isSelectedState.value) surfaceSelected else surfaceDefault
    Card(modifier = modifier
        .background(
            color = cardColor,
            shape = RoundedCornerShape(spacing15X)
        )
        .clickable {
            selectableItemState.isSelectedState.value = !selectableItemState.isSelectedState.value
            selectableItemState.isSelected = !selectableItemState.isSelected
            onOptionClick(selectableItemState)
        },

    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(color = cardColor)
                .padding(spacing2X),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = selectableItemState.text,
                style = body_large,
                color = textPrimary,
                textAlign = TextAlign.Start,
                modifier = modifier.weight(.9f)
            )
            Spacer(modifier = Modifier.padding(horizontal = spacing2X))
            if (selectableItemState.isSelectedState.value) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_check),
//                    contentDescription = "",
//                    modifier = modifier.weight(.1f)
//                )
            }
        }
    }
}
