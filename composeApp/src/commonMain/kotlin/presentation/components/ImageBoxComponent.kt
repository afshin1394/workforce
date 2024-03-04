package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceDefault
@Composable
fun ImageBoxComponent(path : String,modifier: Modifier = Modifier, onImageClick : () -> Unit = {}){
    var imageUrl by remember { mutableStateOf(path) }

    Card(modifier = modifier
        .clickable {
            onImageClick()
        }
        .background(
            color = surfaceDefault,
            shape = RoundedCornerShape(spacing15X)
        ),
        border = BorderStroke(1.dp, strokeDefaultLight)) {
        CoilImage(modifier= Modifier.size(98.dp),
            imageModel = { imageUrl }, // loading a network image or local resource using an URL.
            imageOptions = ImageOptions(
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center
            )
        )

    }

}