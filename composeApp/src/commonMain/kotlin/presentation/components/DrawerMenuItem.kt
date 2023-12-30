package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR

import presentation.theme.body_large


@Composable
fun DrawerMenuItem(
    modifier: Modifier = Modifier,
    iconDrawable: ImageResource? = null,
    text: String = "My Tickets",
    onItemClick: () -> Unit = {}
) {
    Row(

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 16.dp)
            .clickable { onItemClick() }
    ) {
        iconDrawable?.let {
            Image(
                painter = painterResource(it),
                contentDescription = text,
                modifier = Modifier
                    .size(24.dp)
                    .fillMaxWidth(.1f)
            )
            Spacer(modifier = Modifier.width(spacing2X))
        }
        androidx.compose.material3.Text(
            text = text,
            style = body_large,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(.8f)
        )
        Spacer(modifier = Modifier.width(spacing2X))
        Image(
            painter = painterResource(MR.images.chevron_right),
            contentDescription = "chevron_right",
            modifier = Modifier
                .size(24.dp)
                .fillMaxWidth(.1f)

        )
    }
}

