package presentation.screens.main.components.formViewer

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
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import utils.debounceClick
import utils.defaultDebounceClick


@Composable
fun AttachedFileComponent(modifier: Modifier = Modifier,backgroundColor : Color, onAttachClick : () -> Unit = {}) {
    val onAttachClickDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onAttachClick)

    Card(modifier = modifier.size(98.dp)
        .clickable {
            onAttachClickDebounce()
        }
        .background(
            color = backgroundColor,
            shape = RoundedCornerShape(spacing15X)
        ),
        border = BorderStroke(1.dp, backgroundColor)) {
        Image(painter = painterResource(MR.images.attach_blue), contentDescription = "",modifier = modifier.padding(14.dp), colorFilter = ColorFilter.tint(
            backgroundColor
        ))
    }

}