package presentation.screens.main.components.formViewer.draw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun BrushModal(
    isVisible: Boolean,
    max: Int = 200,
    sizeBrush: List<Int>,

    onProgressChanged: (Int) -> Unit
) {


    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(start = 120.dp)
        ) {
            repeat(3) { rowIndex ->
                if (sizeBrush.size - 1 < rowIndex) {
                    Spacer(Modifier.weight(1f, true))
                    return@repeat
                }
                val size = sizeBrush[rowIndex]
                BrushDots(
                    sizeBrush = size,

                    ) {
                    onProgressChanged(it)
                }
            }
        }
    }

}
@Composable
internal fun RowScope.BrushDots(
    sizeBrush: Int,
    clickedColor: (Int) -> Unit
) {

    var dotSize=0

    IconButton(
        onClick = {
            clickedColor(sizeBrush)
        }, modifier = Modifier


    ) {

        if (sizeBrush==15){
            dotSize=24

        }
        if (sizeBrush==55){
            dotSize=28
        }

        if (sizeBrush==95){
            dotSize=32
        }


        Image(
            painter = ColorPainter(Color.Gray),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = Modifier
                .size(dotSize.dp)
                .clip(CircleShape)
        )
    }
}