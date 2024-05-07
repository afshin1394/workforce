package presentation.screens.main.components.formViewer.draw

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.Shapes
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.MR
import presentation.theme.iconPrimary
import presentation.theme.surfaceBrandDefault

@Composable
fun ControlsBarEditPhoto(

    onSaveClick: () -> Unit,
    onColorClick: () -> Unit,
    onSizeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    undoVisibility: MutableState<Boolean>,
    colorValue: MutableState<Color>,
    sizeValue: MutableState<Int>,
) {

    Surface(
        elevation = 12.dp,
        color = MaterialTheme.colors.surface,
        shape = Shapes.medium,
    ) {
        Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceAround) {
            MenuItems(MR.images.edit, "stroke color", colorValue.value) {
                onColorClick()
            }

            MenuItems(MR.images.ic_brush_size, "stroke size", iconPrimary) {
                onSizeClick()
            }

            MenuItems(
                MR.images.eraser2,
                "reset",
                iconPrimary
            ) {
                onDeleteClick()

            }

            Button(
                modifier = Modifier.width(150.dp).padding(end = 8.dp),

                onClick = { if (undoVisibility.value) onSaveClick() },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (undoVisibility.value) surfaceBrandDefault else Color.Gray),
                shape = RoundedCornerShape(10.dp),

                ) {
                Text("save", style = TextStyle(color = Color.White))
            }
        }
    }
}






@Composable
fun ControlsBarPreview(


    isRotate: MutableState<Boolean>,
    onRotateClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeletePhoto:()->Unit,
    onSaveClick:()->Unit
) {

    Surface(
        elevation = 12.dp,
        color = MaterialTheme.colors.surface,
        shape = Shapes.medium,
    ) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceAround) {
            MenuItems(
                MR.images.delete,
                "delete",
                Color.Red
            ) {
                onDeletePhoto()
            }
            MenuItems(
                MR.images.rotate,
                "rotate",
                iconPrimary
            ) {
                onRotateClick()
            }
            MenuItems(
                MR.images.edit,
                "edit",
                iconPrimary
            ) {

                onEditClick()

            }
            Button(
                modifier = Modifier.width(150.dp).padding(end = 8.dp),

                onClick = { if (isRotate.value) onSaveClick() },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (isRotate.value) surfaceBrandDefault else Color.Gray),
                shape = RoundedCornerShape(10.dp),

                ) {
                Text("save", style = TextStyle(color = Color.White))
            }
        }
    }
}

@Composable
fun RowScope.MenuItems(

    resId: ImageResource,
    desc: String,
    colorTint: Color,
    border: Boolean = false,
    onClick: () -> Unit
) {
    val modifier = Modifier.size(24.dp)
    IconButton(
        onClick = onClick, modifier = Modifier.weight(1f, true)
    ) {
        if (desc.contains("color")) {

            Image(
                painter = ColorPainter(colorTint),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
        } else {

            Icon(
                painterResource(resId),
                contentDescription = desc,
                tint = colorTint,
                modifier = if (border) modifier.border(
                    0.5.dp,
                    Color.White,
                    shape = CircleShape
                ) else modifier
            )

        }
    }
}
