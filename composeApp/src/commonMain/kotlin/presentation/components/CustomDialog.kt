package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing25X
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch

import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.surfaceDefault
import presentation.theme.surfaceSuccessWeak

@Composable
fun CustomDialog(
    showDialog: Boolean,
    message:StringResource,
    title:StringResource,
    titleButton:StringResource,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(16.dp)),
                color = Color.White,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {

                    Text(text = stringResource(title), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(message), fontSize = 14.sp)
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = surfaceBrandDefault,
                            contentColor = Color.White
                        ),
                        onClick = {
                       onConfirm()
                        }
                    ) {
                        Text(text = stringResource(titleButton))
                    }
                }
            }
        }
    }
}


@Composable
fun CustomDialogDoubleAction(
    showDialog: Boolean,
    message:StringResource,
    title:StringResource,
    titleButton:StringResource,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    if (showDialog) {
        val scope = rememberCoroutineScope()
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(16.dp)),
                color = Color.White,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = stringResource(title), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(message), fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.Center) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = surfaceDefault,
                                contentColor = Color.Black
                            ),
                            onClick = {
                                scope.launch {
                                    onDismiss()
                                }
                            }
                        ) {
                            Text(text = stringResource(MR.strings.cancel))
                        }
                        Spacer(modifier = Modifier.width(spacing25X))
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = surfaceBrandDefault,
                                contentColor = Color.White
                            ),
                            onClick = {
                                onConfirm()

                            }
                        ) {
                            Text(text = stringResource(titleButton))
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun CompleteFlowDialog(
    showDialog: Boolean,
    message:StringResource,
    titleButton:StringResource,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(16.dp)),
                color = surfaceSuccessWeak,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {

                    Image(
                        painterResource(MR.images.check_square_green),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp)

                    )
                    Text(text = stringResource(message), fontSize = 14.sp)
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = surfaceBrandDefault,
                            contentColor = Color.White
                        ),
                        onClick = {
                            onConfirm()
                        }
                    ) {
                        Text(text = stringResource(titleButton))
                    }
                }
            }
        }
    }
}



