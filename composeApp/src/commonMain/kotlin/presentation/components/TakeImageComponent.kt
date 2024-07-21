package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceDefault

@Composable
fun TakeImageComponent(backgroundColor : Color = surfaceDefault, modifier: Modifier = Modifier, onCameraClick : () -> Unit = {}){
    Card(modifier = modifier.size(98.dp)
        .clickable {
            onCameraClick()
        }
        .background(
            color = backgroundColor,
            shape = RoundedCornerShape(spacing15X)
        ),
        border = BorderStroke(1.dp, strokeDefaultLight)) {
            Image(painter = painterResource(MR.images.blue_camera), contentDescription = "",modifier = modifier.padding(29.dp), colorFilter = ColorFilter.tint(
                backgroundColor
            ))
    }

}