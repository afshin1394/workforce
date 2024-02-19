package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.nav.Screen
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR


@Composable
fun DrawerBody( onItemClick: (navRoute : Screen.Main.Menu) -> Unit={}) {
    Column(modifier = Modifier.padding(end =  spacing1X)) {
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.home, stringResource(MR.strings.my_tickets)
        ) {
            onItemClick(Screen.Main.Menu.MyTickets)
        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X), MR.images.settings, stringResource(MR.strings.settings)
        ) {
            onItemClick(Screen.Main.Menu.Settings)

        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.about, stringResource(MR.strings.about)) {
            onItemClick(Screen.Main.Menu.About)

        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.location, stringResource(MR.strings.gps_tracker)) {
            onItemClick(Screen.Main.Menu.GpsTrackingReport)
        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.logout, stringResource(MR.strings.logout)) {
            onItemClick(Screen.Main.Menu.Logout)

        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.form, "form") {
            onItemClick(Screen.Main.Menu.FormViewer)

        }
    }

}