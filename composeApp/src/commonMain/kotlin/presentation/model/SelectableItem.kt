package com.irancell.nwg.wfm.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc

data class SelectableItem(val id : Int, val text : String, var isSelected : Boolean){
    var isSelectedState = mutableStateOf(isSelected)
}


data class SelectableItemStringResource(val id : Int, val text : StringResource, var isSelected : Boolean,val languageType:String=""){
    var isSelectedState = mutableStateOf(isSelected)
}
