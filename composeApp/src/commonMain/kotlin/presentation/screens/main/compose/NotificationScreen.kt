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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import arrow.core.toOption
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
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import org.koin.compose.koinInject
import presentation.screens.main.compose.BaseScreen
import presentation.screens.main.viewmodel.NotificationScreenVM
import utils.NotificationState

@OptIn(ExperimentalMaterialApi::class)
class NotificationScreen (
) : Screen {



    @Composable
    override fun Content() {
        val viewModel: NotificationScreenVM = koinInject()

        val notificationScreenStateList = viewModel.notificationItemsState.toList()
        val selectedState = viewModel.selectState.collectAsState()
        val scaffoldState = rememberBottomSheetScaffoldState()

        val navigator = LocalNavigator.currentOrThrow


        BaseScreen(viewModel, scaffoldState = scaffoldState, stringResource(MR.strings.notification),
            hasDrawer = false, topBar = {
                MenuItemsTopBar(stringResource(MR.strings.notification)) {
                    navigator.pop()
                }
            }, content = {
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
                            viewModel.updateSelectState("")
                        }else{
                            viewModel.updateSelectState(it.id.toString())
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(spacing15X),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(notificationScreenStateList.filter {  it.statusId.toString().lowercase().contains(selectedState.value.lowercase())}) { item ->
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
            }, onBackPressed = {
                navigator.pop()
            })


    }
}
