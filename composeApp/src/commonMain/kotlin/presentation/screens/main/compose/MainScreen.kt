package com.irancell.nwg.wfm.presentation.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import com.irancell.nwg.wfm.presentation.components.*
import com.irancell.nwg.wfm.presentation.model.BottomSheetDoubleActionModel
import com.irancell.nwg.wfm.presentation.screens.main.components.*
import com.irancell.nwg.wfm.presentation.screens.main.events.MainEvent
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.theme.*
import com.irancell.nwg.wfm.ui.compose.TicketListScreen
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.CustomTopAppBar
import presentation.components.DrawerBody
import presentation.components.DrawerHeader
import presentation.screens.main.components.AvailabilityStatus
import presentation.screens.main.components.MoreOptions
import presentation.screens.main.compose.BaseScreen
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary

class MainScreen constructor(
    private val title: String,

    ) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val viewModel : MainScreenVM = koinInject()
        Napier.log(LogLevel.ASSERT,"MainScreenVM", message = viewModel.toString())
//        val viewModel = remember { MainScreenVM() }
        val availability by viewModel.availability.collectAsState()

        val ticketInfoScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.TicketProcess.TicketInfo)
        val notificationScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Main.Notification)
        val accountScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Main.AccountInfo)
        val settingsScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.Settings)
        val aboutScreen = rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.About)
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState();
        val events by viewModel.events

        val suspendItems by lazy {
            viewModel.suspendItems
        }

        val cancelItems by lazy {
            viewModel.cancelItems
        }

        val bottomSheetTitle: String =
            when (events) {
                MainEvent.ActionFilter -> {
                    "Filters"
                }

                MainEvent.Logout -> {
                    "logout"
                }

                MainEvent.AvailabilityStatus -> {
                    "AvailabilityStatus"
                }

                MainEvent.CancelTicket -> {
                    "Cancel ticket"
                }

                MainEvent.MoreOptions -> {

                    "More options"
                }

                MainEvent.SuspendTicket -> {
                    "Suspend ticket"
                }

                MainEvent.SuspendReason -> {
                    "Suspend reason"
                }

                MainEvent.CancelReason -> {
                    "Cancel Reason"
                }

                MainEvent.Default -> {
                    ""
                }

                MainEvent.Accept -> {
                    ""
                }
            }


        BaseScreen(scaffoldState = scaffoldState, hasDrawer = true, topBar = {
            CustomTopAppBar(availability,title, onNavigationItemClick = {
                scope.launch {
                    if (scaffoldState.drawerState.isOpen)
                        scaffoldState.drawerState.close()
                    else
                        scaffoldState.drawerState.open()
                }
            }, onAvailabilityClick = {
                viewModel.events.value = MainEvent.AvailabilityStatus
            }, onNotificationClick = {
                navigator.push(notificationScreen)
            })
        }, drawerContent = {
            DrawerHeader() {
                scope.launch {
                    if (scaffoldState.drawerState.isOpen)
                        scaffoldState.drawerState.close()
                    else
                        scaffoldState.drawerState.open()
                }

                navigator.push(accountScreen)

            }
            DrawerBody(onItemClick = {

                scope.launch {
                    if (scaffoldState.drawerState.isOpen)
                        scaffoldState.drawerState.close()
                    else
                        scaffoldState.drawerState.open()

                    when (it) {
                        com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.About -> {
                            navigator.push(aboutScreen)

                        }

                        com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.Logout -> {
                            viewModel.events.value = MainEvent.Logout
                        }

                        com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.MyTickets -> {
//                        navigator.push(settingsScreen)

                        }

                        com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.Settings -> {
                            navigator.push(settingsScreen)

                        }

                    }
                    scaffoldState.drawerState.close()

                }
            })
        }, title = title, bottomSheetTitle = bottomSheetTitle,
            bottomBarBottomSheetContent = {
                when (events) {
                    MainEvent.ActionFilter -> {
                        bottomSheetDoubleActionBottomBar(BottomSheetDoubleActionModel(
                            "Clear all",
                            Color.Transparent,
                            textInverseDisabled,
                            "Filter",
                            surfaceBrandDefault,
                            textInverse
                        ), onFirstButtonClick = {
                            scope.launch {
                                viewModel.removeAllFilters()
                                scaffoldState.bottomSheetState.collapse()
                                viewModel.events.value = MainEvent.Default
                            }

                        }, onSecondButtonClick = {
                            scope.launch {
                                viewModel.getActiveFilterItems()
                                scaffoldState.bottomSheetState.collapse()
                                viewModel.events.value = MainEvent.Default

                            }
                        })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.Default -> {

                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }

                    MainEvent.Logout -> {
                        bottomSheetDoubleActionBottomBar(
                            BottomSheetDoubleActionModel(
                                "Cancel",
                                surfaceDefault, textPrimary, "Logout", surfaceBrandDefault,
                                textInverse
                            )
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.AvailabilityStatus -> {


                    }

                    MainEvent.CancelTicket -> {
                        CancelTicketBottomBarComponent(viewModel.enableCancelSubmit.value) {
                            viewModel.events.value = MainEvent.Default
                        }
                    }

                    MainEvent.MoreOptions -> {


                    }

                    MainEvent.SuspendTicket -> {


                        SuspendTicketBottomBarComponent(viewModel.enableSuspendSubmit.value) {
                            viewModel.events.value = MainEvent.Default
                        }


                    }

                    MainEvent.SuspendReason -> {

                    }

                    MainEvent.CancelReason -> {

                    }

                    MainEvent.Accept -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }

                    }
                }
            }, bottomSheetContent = {

                when (events) {

                    MainEvent.ActionFilter -> {
                        CustomFilterSectionPreview(viewModel.filterSectionItems)
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.Default -> {

                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }

                    MainEvent.Logout -> {
                        Text(
                            text = "Are you sure you want to logout?",
                            style = body_large,
                            modifier = Modifier.padding(start = spacing2X)
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.AvailabilityStatus -> {
                        AvailabilityStatus(availability){
                            viewModel.changeAvailability()
                        }
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.CancelTicket -> {
                        CancelTicketComponent(viewModel.cancelReason.value, onSelectReason = {
                            viewModel.events.value = MainEvent.CancelReason
                        }, onCompleted = {
                            viewModel.enableCancelSubmit.value = it
                        })
                    }

                    MainEvent.MoreOptions -> {
                        MoreOptions(onCancelClick = {
                            viewModel.events.value = MainEvent.CancelTicket
                        }, onSuspendClick = {
                            viewModel.events.value = MainEvent.SuspendTicket
                        })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.SuspendTicket -> {
                        SuspendTicketContentComponent(
                            viewModel.suspendReason.value,
                            onSelectReason = {
                                viewModel.events.value = MainEvent.SuspendReason
                            },
                            onCompleted = {
                                viewModel.enableSuspendSubmit.value = it
                            })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.SuspendReason -> {

                        SelectableComponentPreview(suspendItems, { index, selectableItem ->
                            suspendItems[index] = selectableItem
                            suspendItems.filter { it.id != selectableItem.id }.map {
                                it.isSelected = false
                                it.isSelectedState.value = false
                            }
                            viewModel.suspendReason.value = selectableItem.text
                            viewModel.events.value = MainEvent.SuspendTicket
                        }, onSearch = { searchQuery ->
                            suspendItems.clear()
                            suspendItems.addAll(viewModel.suspendItems.filter {
                                it.text.lowercase().contains(searchQuery.lowercase())
                            })


                        })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.CancelReason -> {
                        SelectableComponentPreview(viewModel.cancelItems, { index, selectableItem ->
                            cancelItems[index] = selectableItem
                            cancelItems.filter { it.id != selectableItem.id }.map {
                                it.isSelected = false
                                it.isSelectedState.value = false
                            }
                            viewModel.cancelReason.value = selectableItem.text
                            viewModel.events.value = MainEvent.CancelTicket
                        }, onSearch = { searchQuery ->
                            cancelItems.clear()
                            cancelItems.addAll(viewModel.cancelItems.filter {
                                it.text.lowercase().contains(searchQuery.lowercase())
                            })
                        })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.Default -> {

                    }

                    MainEvent.Accept -> {
                        navigator.push(ticketInfoScreen)
                    }
                }

            }, content = {
                TicketListScreen(searchText = "", onEvent = {
                    Napier.i("TicketListScreen")
                    viewModel.events.value = it
                }, tasks = viewModel.tasks)

            }, onCloseBottomSheet = {
                viewModel.events.value = MainEvent.Default
            })
    }


}

