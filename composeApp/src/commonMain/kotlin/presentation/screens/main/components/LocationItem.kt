package presentation.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.irancell.nwg.wfm.presentation.theme.spacing15X

import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.db.GeneralLocation
import presentation.theme.body_small
import presentation.theme.body_small_strong
import presentation.theme.errorIcon
import presentation.theme.surfaceDefault

@Composable
fun LocationItem(modifier : Modifier = Modifier,generalLocation: GeneralLocation) {
    Column(modifier = modifier
        .background(surfaceDefault)
        ) {
        Row( horizontalArrangement = Arrangement.SpaceBetween) {
            androidx.compose.material3.Card(modifier = Modifier,
                shape = RoundedCornerShape(spacing05X),
                content = {
                    Image(
                        painter = painterResource( MR.images.location),
                        contentDescription = "chevron_up",
                        modifier = modifier.width(24.dp).height(24.dp)
                    )
                    Column(modifier.padding(spacing05X), verticalArrangement = Arrangement.SpaceBetween) {

                        Spacer(modifier = Modifier.padding(horizontal = spacing15X))
                        Text(text = generalLocation.latitude, style =  body_small_strong)
                        Spacer(modifier = Modifier.padding(horizontal = spacing15X))
                        Text(text = generalLocation.longitude, style =  body_small)
                        Spacer(modifier = Modifier.padding(horizontal = spacing15X))
                        Text(text = generalLocation.datetime, style =  body_small)
                    }
                })


        }



    }
}