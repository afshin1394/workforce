package presentation.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR

import presentation.theme.body_large
import presentation.theme.surfaceDefault
import presentation.theme.textBrand
import presentation.theme.textPrimary
import presentation.theme.textSecondary

@Composable
fun AvailabilityStatus(
    availability: Boolean = false,
    updateAvailability: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = spacing2X)
            .background(color = surfaceDefault)
    ) {
        Text(
            text = stringResource(MR.strings.tap_update_availability),
            color = textPrimary,
            style = body_large,
            textAlign = TextAlign.Center
        )

        val available = stringResource(MR.strings.available)
        val unAvailable = stringResource(MR.strings.unAvailable)
        val drawable = if (availability) MR.images.ic_on else MR.images.ic_off
        val text = if (availability) available else unAvailable
        val textColor = if (availability) textBrand else textSecondary

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = spacing3X, horizontal = spacing2X)
                .clickable {
                    updateAvailability()
                }
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = "",
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
            Spacer(modifier = Modifier.height(spacing1X))
            Text(
                text = text,
                style = body_large,
                color = textColor
            )
        }
    }
}