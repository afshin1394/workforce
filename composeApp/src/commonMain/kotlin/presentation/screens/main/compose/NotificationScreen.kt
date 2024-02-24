package com.irancell.nwg.wfm.presentation.screens.main.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.FilterRow
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.model.Notification
import com.irancell.nwg.wfm.presentation.model.NotificationModel
import presentation.model.StateFilter
import presentation.screens.main.components.NotificationItem
import presentation.theme.mediumDivider
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import utils.NotificationState

@OptIn(ExperimentalMaterialApi::class)
class NotificationScreen (
    private val title: String
) : Screen {



    @Composable
    override fun Content() {

        var selectState by remember {
            mutableStateOf("")
        }
        val navigator = LocalNavigator.currentOrThrow
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()


        val notificationItems = arrayListOf(
            NotificationModel(
                ticketId = 2,
                ticketType = Notification.Suspend.type,
                "Suspended ticket",
                body = "has suspended ticket",
                status = "Read",
                statusId = NotificationState.Read.id
            ),
            NotificationModel(
                ticketId = 3,
                ticketType = Notification.Canceled.type,
                "Canceled ticket",
                body = "has canceled the ticket",
                status = "Unread",
                statusId = NotificationState.Read.id
            ),
            NotificationModel(
                ticketId = 4,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = NotificationState.Read.id
            ),
            NotificationModel(ticketId = 5,
                    ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = NotificationState.Read.id),
            NotificationModel(ticketId = 6,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Unread",
                statusId = NotificationState.UnRead.id),
            NotificationModel(ticketId = 7,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = 3),
            NotificationModel(ticketId = 8,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = NotificationState.UnRead.id),
            NotificationModel(ticketId = 9,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Unread",
                statusId = NotificationState.UnRead.id),
            NotificationModel(ticketId = 10,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = NotificationState.UnRead.id),
            NotificationModel(ticketId = 11,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = NotificationState.UnRead.id),
            NotificationModel(ticketId = 12,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                status = "Read",
                statusId = NotificationState.Read.id),


            )


        Scaffold(topBar = {
            MenuItemsTopBar(stringResource(MR.strings.notification)) {

                navigator.pop()
//            navHostController.navigate(Screen.Main.route) {
//                popUpTo(Screen.Main.route) {
//
//                }
//            }
            }

        }) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {

                FilterRow(
                    items = arrayListOf(
                        StateFilter(NotificationState.All.id, stringResource(MR.strings.all), false),
                        StateFilter(NotificationState.UnRead.id, stringResource(MR.strings.unread), false),
                        StateFilter(NotificationState.Read.id, stringResource(MR.strings.read), false)

                    ), modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing2X)
                    , itemTitleSelected = stringResource(MR.strings.all)
                ) {
                    if (it.id == NotificationState.All.id){
                        selectState=""
                    }else{
                        selectState=it.id.toString()
                    }


                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing15X),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(notificationItems.filter {  it.statusId.toString().lowercase().contains(selectState.lowercase())}) { item ->
                        NotificationItem(
                            modifier = Modifier.fillMaxWidth(),
                            notificationItemModel = item
                        )
                        Divider(
                            thickness = 1.dp,
                            color = mediumDivider,
                            modifier = Modifier.fillMaxWidth(.95f)
                        )
                    }
                }

            }
        }
    }
}
