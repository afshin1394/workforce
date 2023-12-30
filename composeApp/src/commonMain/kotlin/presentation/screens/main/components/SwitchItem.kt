package com.irancell.nwg.wfm.presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.theme.*

import presentation.theme.body_large
import presentation.theme.surfaceDefault


@Composable
fun SwitchItem(text : String = "Save photo in gallery" , isActive : Boolean = false,onCheckChange : (isActive : Boolean) -> Unit = {}){
    var isActiveState by remember {
        mutableStateOf(isActive)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = surfaceDefault)
        .padding(spacing2X), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Text(text = text, style = body_large, modifier = Modifier.weight(.8f))
        Spacer(modifier = Modifier
            .padding(spacing1X)
            .weight(.2f))
        Switch(isActiveState, onCheckedChange = {
            isActiveState = !isActiveState
            onCheckChange(it)
        })
    }


}