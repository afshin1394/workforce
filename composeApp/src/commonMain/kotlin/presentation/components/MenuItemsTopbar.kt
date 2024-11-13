package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing075X
import presentation.theme.surfaceDefault
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import presentation.theme.h4
import presentation.theme.h5
import utils.Language
import utils.debounceClick
import utils.defaultDebounceClick


@Composable
fun MenuItemsTopBar(title: String = "About", onBackClick: () -> Unit = {}) {
    val backClickDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onBackClick)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.background(
            surfaceDefault
        ).padding(
            horizontal =
            spacing075X
        )
    ) {
        Image(painter = if (getSharedPref().getString(Language) == "fa") painterResource(MR.images.arrow_right) else
            painterResource(MR.images.arrow_left),
            contentDescription = "",
            modifier = Modifier.weight(.1f).clickable {
                backClickDebounce()
            })
        Text(
            text = title,
            style = h4,
            modifier = Modifier.padding(vertical = 15.dp).weight(.8f),
            textAlign = TextAlign.Center
        )
        Text("", modifier = Modifier.weight(.1f))
    }
}

@Composable
fun TicketInfoTopBar(title: String = "About", onBackClick: () -> Unit = {}) {
    val backClickDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onBackClick)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.background(
            surfaceDefault
        ).padding(
            horizontal =
            spacing075X
        )
    ) {
        Image(painter = if (getSharedPref().getString(Language) == "fa") painterResource(MR.images.arrow_right) else
            painterResource(MR.images.arrow_left),
            contentDescription = "",
            modifier = Modifier.weight(.1f).clickable {
                backClickDebounce()
            })
        Text(
            text = stringResource(MR.strings.ticket_info),
            style = h4,
            modifier = Modifier
                .padding(vertical = 15.dp)
                .weight(0.9f) // Fixed width to prevent text from occupying full row width
                .scale(0.9f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Ellipsis if text is too long
        )
        Text("", modifier = Modifier.weight(.1f))
    }
}

@Composable
fun TicketProcessTopBar(
    title: String = "",
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {}
) {
    val backClickDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onBackClick)
    val infoClickDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onInfoClick)
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(surfaceDefault)
                .padding(horizontal = spacing075X)
        ) {
            Image(
                painter = if (getSharedPref().getString(Language) == "fa") painterResource(MR.images.arrow_right) else painterResource(MR.images.arrow_left),
                contentDescription = "",
                modifier = Modifier
                    .weight(.1f)
                    .clickable { backClickDebounce() }
            )

            // Constrain the text width to keep side items visible
            Text(
                text = title,
                style = h4,
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .weight(0.9f) // Fixed width to prevent text from occupying full row width
                    .scale(0.9f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Ellipsis if text is too long
            )

            Image(
                painter = painterResource(MR.images.info_square),
                contentDescription = "TicketInfo",
                modifier = Modifier
                    .weight(.1f)
                    .clickable { infoClickDebounce() }
            )
        }
    }
}