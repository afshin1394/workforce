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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.FilterRow
import com.irancell.nwg.wfm.presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.model.Notification
import com.irancell.nwg.wfm.presentation.model.NotificationModel
import com.irancell.nwg.wfm.presentation.model.StateFilter
import presentation.screens.main.components.NotificationItem
import presentation.theme.mediumDivider
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
@OptIn(ExperimentalMaterialApi::class)
class NotificationScreen (
    private val title: String
) : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

        val notificationItems = arrayListOf(
            NotificationModel(
                ticketId = 2,
                ticketType = Notification.Suspend.type,
                "Suspended ticket",
                body = "has suspended ticket"
            ),
            NotificationModel(
                ticketId = 3,
                ticketType = Notification.Canceled.type,
                "Canceled ticket",
                body = "has canceled the ticket"
            ),
            NotificationModel(
                ticketId = 4,
                ticketType = Notification.NewUpdate.type,
                "New update",
                body = "A new version of the app is released",
                raiser = ""
            ),
            NotificationModel(ticketId = 5),
            NotificationModel(ticketId = 6),
            NotificationModel(ticketId = 7),
            NotificationModel(ticketId = 8),
            NotificationModel(ticketId = 9),
            NotificationModel(ticketId = 10),
            NotificationModel(ticketId = 11),
            NotificationModel(ticketId = 12),


            )


        Scaffold(topBar = {
            MenuItemsTopBar(title) {

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
                        StateFilter(1, "All", false),
                        StateFilter(2, "Unread", false),
                        StateFilter(3, "Read", false)

                    ), modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing2X)
                ) {

                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing15X),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(notificationItems) { item ->
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
