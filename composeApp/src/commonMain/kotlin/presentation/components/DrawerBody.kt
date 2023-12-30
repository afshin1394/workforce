package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.nav.Screen
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import irancell.nwg.wfm.MR
import presentation.components.DrawerMenuItem

@Composable
fun DrawerBody( onItemClick: (navRoute : Screen.Main.Menu) -> Unit={}) {
    Column(modifier = Modifier.padding(end =  spacing1X)) {
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.settings, "My Tickets") {
            onItemClick(Screen.Main.Menu.MyTickets)
        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X), MR.images.settings, "Settings") {
            onItemClick(Screen.Main.Menu.Settings)

        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.about, "About") {
            onItemClick(Screen.Main.Menu.About)

        }
        DrawerMenuItem(modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing2X),MR.images.logout, "Logout") {
            onItemClick(Screen.Main.Menu.Logout)

        }
    }

}