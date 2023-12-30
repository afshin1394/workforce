package presentation.screens.auth.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

import presentation.theme.body_large




@Composable
fun AuthAlertText(
    modifier: Modifier = Modifier,
    alertTextItem: AuthAlertTextItem,
    onClick: () -> Unit
) {
    Row(modifier = modifier
        .wrapContentWidth()
        .wrapContentHeight()
        .clickable {
            onClick()
        }
    ) {
        if (alertTextItem.hasIcon && alertTextItem.res!=null)
            Image(
                painter = painterResource(alertTextItem.res),
                contentDescription = alertTextItem.contentDescriptor,
                modifier.size(20.dp)
            )
        Spacer(modifier = Modifier.width(spacing1X))
        Text(text = alertTextItem.text, style = body_large, color = alertTextItem.textColor)

    }

}

data class AuthAlertTextItem(
    val hasIcon: Boolean = false,
    val res: ImageResource? = null,
    val text: String,
    val textColor: Color,
    val contentDescriptor: String = ""
)

