package irancell.nwg.wfm

import android.graphics.Canvas
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


@Composable
actual  fun CreateDrawBox(
    imageBitmap: ImageBitmap,
    modifier: Modifier,
    backgroundColor: Color,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit
): Any {
    return AndroidView(
        factory = {
            ComposeView(it).apply {

                setContent {

                    LaunchedEffect(DrawController) {
                        DrawController.changeBgColor(backgroundColor)
                        DrawController.trackBitmaps(this@apply, this, bitmapCallback)
                        DrawController.trackHistory(this, trackHistory)
                    }
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        DrawController.insertNewPath(offset)
                                        DrawController.updateLatestPath(offset)
                                        DrawController.pathList
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        DrawController.insertNewPath(offset)
                                    }
                                ) { change, _ ->
                                    val newPoint = change.position
                                    DrawController.updateLatestPath(newPoint)
                                }
                            }

                    ) {

                        val canvasWidth = size.width
                        val canvasHeight = size.height


                        drawImage(
                            image = imageBitmap,
                            dstSize = IntSize(canvasWidth.toInt(),canvasHeight.toInt()),
                        )



                        DrawController.pathList.forEach { pw ->
                            drawPath(
                                createPath(pw.points),
                                color = pw.strokeColor,
                                alpha = pw.alpha,
                                style = Stroke(
                                    width = pw.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    )
}





