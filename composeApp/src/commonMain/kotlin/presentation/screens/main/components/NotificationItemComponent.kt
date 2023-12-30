package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.irancell.nwg.wfm.presentation.model.NotificationModel
import com.irancell.nwg.wfm.presentation.theme.*

import presentation.theme.body_large
import presentation.theme.body_small
import presentation.theme.body_small_strong
import presentation.theme.errorIcon
import presentation.theme.surfaceDefault


@Composable
fun NotificationItem(modifier: Modifier = Modifier,notificationItemModel: NotificationModel = NotificationModel()){


   Column(modifier = modifier
       .background(surfaceDefault)
       .padding(spacing2X)) {
       Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
           androidx.compose.material3.Card(modifier = Modifier
               .width(3.dp)
               .height(18.dp),
               colors = CardDefaults.cardColors(errorIcon),
               shape = RoundedCornerShape(spacing05X),
               content = {

               })
           Spacer(modifier = Modifier.padding(horizontal = spacing05X))
           Text(text = notificationItemModel.ticketTypeName, style = body_large)
           
       }
       Spacer(modifier = Modifier.padding(vertical = spacing1X))

       Row(Modifier.padding(start = spacing1X)) {
           Text(text = notificationItemModel.raiser, style =  body_small_strong)
           Spacer(modifier = Modifier.padding(horizontal = spacing05X))
           Text(text = notificationItemModel.body, style =  body_small)
       }
       Spacer(modifier = Modifier.padding(vertical = spacing1X))
       Row(Modifier.padding(start = spacing1X)) {
           Text(text = notificationItemModel.date, style =  body_small)
           Spacer(modifier = Modifier.padding(horizontal = spacing15X))
           Text(text = notificationItemModel.time, style =  body_small)
       }

   }
}