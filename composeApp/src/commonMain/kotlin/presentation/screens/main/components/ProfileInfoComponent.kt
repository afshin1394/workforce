package com.irancell.nwg.wfm.presentation.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

import presentation.theme.body_large
import presentation.theme.surfaceInputReadOnly


@Composable
fun ProfileInfoComponent(  text : String = "Ali Sohrabi",imageResource: ImageResource? = null,backgroundColor : Color = surfaceInputReadOnly){
Row(modifier = Modifier
    .fillMaxWidth()
    .background(color = backgroundColor, shape = RoundedCornerShape(radius2XLarge))
    .padding(spacing15X)){
    imageResource?.let{
        Image(painter = painterResource(it), contentDescription = "")
    }
    Spacer(modifier = Modifier.padding(end = spacing15X))
    Text(text = text, style = body_large)
}
}