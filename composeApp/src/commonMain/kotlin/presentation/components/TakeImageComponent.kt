package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceDefault

@Composable
fun TakeImageComponent(modifier: Modifier = Modifier,onCameraClick : () -> Unit = {}){
    Card(modifier = modifier
        .clickable {

            onCameraClick()
        }
        .background(
            color = surfaceDefault,
            shape = RoundedCornerShape(spacing15X)
        ),
        border = BorderStroke(1.dp, strokeDefaultLight)) {
            Image(painter = painterResource(MR.images.blue_camera), contentDescription = "",modifier = modifier.padding(29.dp))
    }

}