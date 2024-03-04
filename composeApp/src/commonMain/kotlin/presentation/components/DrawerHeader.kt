package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.nav.Screen
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref

import presentation.theme.body_large
import presentation.theme.mediumDivider
import presentation.theme.surfaceDefault
import utils.Language


@Composable
fun DrawerHeader(title : String,onItemClick : () -> Unit = {}) {
    Column(modifier = Modifier.background(color = surfaceDefault).padding(end = spacing1X),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(spacing3X))
        Spacer(modifier = Modifier.height(spacing2X))

        Row(

            modifier = Modifier
                .background(color = surfaceDefault)

                .padding(vertical = 16.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth().clickable {
                        onItemClick()
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(MR.images.ic_avatar),
                        contentDescription = "avatar",
                        modifier = Modifier
                            .fillMaxWidth(.2f)
                            .height(40.dp)
                    )
                    Text(
                        text = title,
                        style = body_large,
                        modifier = Modifier.fillMaxWidth(.8f)
                    )
                }
                Image(
                    painter = if (getSharedPref().getString(Language)=="en") painterResource(MR.images.chevron_right)else
                        painterResource(MR.images.chevron_left),
                    contentDescription = "chevron",
                    modifier = Modifier
                        .wrapContentSize()
                )
            }


        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing2X))
            Divider(thickness = 1.dp, color = mediumDivider, modifier = Modifier.fillMaxWidth(.8f))
            Spacer(modifier = Modifier.height(spacing2X))
        }

    }

}