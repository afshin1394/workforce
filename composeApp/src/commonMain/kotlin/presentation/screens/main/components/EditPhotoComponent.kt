package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.stack.Stack
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.CreateDrawBox
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.ImageBitmapToBitmap
import irancell.nwg.wfm.ParseUri
import irancell.nwg.wfm.SaveBitmapToFile
import irancell.nwg.wfm.UriToImageBitmap
import irancell.nwg.wfm.getDpi
import presentation.screens.main.components.formViewer.draw.BrushModal
import presentation.screens.main.components.formViewer.draw.ColorModal
import presentation.screens.main.components.formViewer.draw.ControlsBarEditPhoto
import presentation.screens.main.components.formViewer.draw.blue
import presentation.screens.main.components.formViewer.draw.deepPurple

import presentation.screens.main.components.formViewer.draw.defaultSelectedColor
import presentation.screens.main.components.formViewer.draw.green
import presentation.screens.main.components.formViewer.draw.red
import presentation.screens.main.components.formViewer.draw.yellow
import presentation.theme.surfaceDefault

@Composable
fun EditPhotoComponent(
    angle: Float,
    originUriPhotoSelected: String,
    onEditUri: (newUri: String, originUri: String) -> Unit
) {


    val bitmapNew = UriToImageBitmap(ParseUri(originUriPhotoSelected), angle)


    val undoVisibility = remember { mutableStateOf(false) }
    val redoVisibility = remember { mutableStateOf(false) }
    val colorBarVisibility = remember { mutableStateOf(false) }
    val sizeBarVisibility = remember { mutableStateOf(false) }
    val currentColor = remember { mutableStateOf(DrawController.getColor()) }
    val bg = androidx.compose.material.MaterialTheme.colors.background
    val currentBgColor = remember { mutableStateOf(bg) }
    val currentSize = remember { mutableStateOf(10) }
    val colorIsBg = remember { mutableStateOf(false) }
    val drawBottomMenu = remember { mutableStateOf(true) }


    val savedImageUri = remember { mutableStateOf("") }
    val savedImageUriCheck = remember { mutableStateOf(false) }


    val colorArray = listOf(
        red,
        deepPurple,
        blue,
        green,
        yellow,


        )

    val colors: List<List<Color>> = colorArray

    val sizeBrushArray = listOf(
        15,
        55,
        95
    )
    val sizBrush: List<Int> = sizeBrushArray
    bitmapNew as ImageBitmap
    val widthImage = bitmapNew.width/ getDpi()
    val heightImage = bitmapNew.height / getDpi()

    Napier.log(LogLevel.ASSERT,tag = "andazee", message = widthImage.toString())
    Napier.log(LogLevel.ASSERT,tag = "andazee", message = heightImage.toString())
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CreateDrawBox(
            imageBitmap = bitmapNew as ImageBitmap,


            backgroundColor = currentBgColor.value,
            modifier = Modifier.padding(
                bottom = 2.dp,
                end = 2.dp,
                start = 2.dp,
            )
                .weight(.8f)
                .clipToBounds()
                .fillMaxWidth(),
            bitmapCallback = { imageBitmap, error ->
                imageBitmap?.let { image ->

                    savedImageUriCheck.value = true

                    savedImageUri.value = SaveBitmapToFile(
                        "/wfmImages/Suspend/",
                        ImageBitmapToBitmap(image)
                    ).toString()


                }
            }
        ) { undoCount, redoCount ->
            drawBottomMenu.value = true

            sizeBarVisibility.value = false
            colorBarVisibility.value = false
            undoVisibility.value = undoCount != 0
            redoVisibility.value = redoCount != 0
        }


        if (savedImageUriCheck.value) {

            onEditUri(savedImageUri.value, originUriPhotoSelected)
            DrawController.reset()
        }

        Column(Modifier) {
            if (drawBottomMenu.value) {
                ControlsBarEditPhoto(
                    onSaveClick = {
                        DrawController.saveBitmap()
                    },
                    onColorClick =
                    {
                        colorBarVisibility.value =
                            when (colorBarVisibility.value) {
                                false -> true
                                colorIsBg.value -> true
                                else -> false
                            }
                        colorIsBg.value = false
                        sizeBarVisibility.value = false
                        drawBottomMenu.value = false
                    }, onSizeClick = {
                        sizeBarVisibility.value =
                            !sizeBarVisibility.value
                        colorBarVisibility.value = false
                        drawBottomMenu.value = false
                    },
                    undoVisibility = undoVisibility,
                    colorValue = currentColor,
                    sizeValue = currentSize
                )
            }
            ColorModal(
                isVisible = colorBarVisibility.value,
                showShades = true,
                colors = colors,
                defaultColor = defaultSelectedColor,

                clickedColor = {
                    if (colorIsBg.value) {
                        currentBgColor.value = it
                        DrawController.changeBgColor(it)
                    } else {
                        currentColor.value = it
                        DrawController.changeColor(it)
                    }
                    drawBottomMenu.value = true
                    colorBarVisibility.value = false
                }
            )

            BrushModal(
                isVisible = sizeBarVisibility.value,
                sizeBrush = sizBrush

            ) {
                currentSize.value = it
                DrawController.changeStrokeWidth(it.toFloat())
                sizeBarVisibility.value = false
                drawBottomMenu.value = true
            }
//                Spacer(modifier = Modifier.weight(1f))
        }


    }


}