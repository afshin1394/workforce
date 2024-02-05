package com.irancell.nwg.wfm.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing075X
import presentation.theme.surfaceDefault
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import presentation.theme.h4
import utils.Language


@Composable
fun MenuItemsTopBar (title : String = "About",onBackClick : () -> Unit = {}){
 Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.background(
     surfaceDefault
 ).padding(horizontal =
     spacing075X)) {
     Image(painter =   if (getSharedPref().getString(Language)=="en")painterResource(MR.images.arrow_left) else
         painterResource(MR.images.arrow_right), contentDescription = "", modifier = Modifier.weight(.1f).clickable {
         onBackClick()
     })
     Text(text = title, style = h4, modifier = Modifier.padding(vertical = 15.dp).weight(.8f), textAlign = TextAlign.Center)
     Text("", modifier = Modifier.weight(.1f))
 }
}