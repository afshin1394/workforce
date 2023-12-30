package com.irancell.nwg.wfm.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class SelectableItem(val id : Int, val text : String, var isSelected : Boolean){
    var isSelectedState = mutableStateOf(isSelected)
}


