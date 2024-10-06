package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import presentation.theme.backgroundBackground3
import presentation.theme.textPrimary
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import presentation.theme.h4
import utils.AvailabilityStatus


//onNavigationClick: () -> Unit,onSearchClick: () -> Unit,onOnAndOffClick: () -> Unit,onNotificationClick:() -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    availability: AvailabilityStatus,
    title: String = "MyTicket",
    onNavigationItemClick: () -> Unit = {},
    onAvailabilityClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val availabilityIcon = when (availability) {
        AvailabilityStatus.Available -> MR.images.ic_on
        AvailabilityStatus.NoInternet -> MR.images.ic_no_internet
        AvailabilityStatus.NotRunning -> MR.images.ic_not_running
        AvailabilityStatus.Unavailable -> MR.images.ic_off
    }


    Column {
        TopAppBar(
            modifier = Modifier.background(color = backgroundBackground3),
            title = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = title, color = textPrimary, style = h4)
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        onNavigationItemClick()
                    }
                }) {
                    Image(
                        painterResource(MR.images.menu),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            }, actions = {
                IconButton(onClick = { onAvailabilityClick() }) {
                    Image(
                        painterResource(availabilityIcon),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
                IconButton(onClick = { onNotificationClick() }) {
                    Image(
                        painterResource(MR.images.notification_med_off),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            }
        )
    }
}