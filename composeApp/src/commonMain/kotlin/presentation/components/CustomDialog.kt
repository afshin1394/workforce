package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

import presentation.theme.surfaceBrandDefault

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