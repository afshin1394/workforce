package presentation.screens.main.compose

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
import presentation.nav.Screen.Main.*
import com.irancell.nwg.wfm.presentation.components.*
import presentation.model.BottomSheetDoubleActionModel
import presentation.screens.main.events.MainEvent
import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.theme.*
import presentation.screens.main.components.TicketListScreen

import dev.icerock.moko.resources.compose.stringResource
import domain.models.TaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Camera
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.checkConnectivity
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.CustomTopAppBar
import presentation.components.DrawerBody
import presentation.components.DrawerHeader
import presentation.screens.main.components.AvailabilityStatus
import presentation.screens.main.components.CancelTicketBottomBarComponent
import presentation.screens.main.components.CancelTicketComponent
import presentation.screens.main.components.MoreOptions
import presentation.screens.main.components.SuspendTicketBottomBarComponent
import presentation.screens.main.components.SuspendTicketContentComponent
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary
import utils.Availability
import utils.PhoneNumber
import utils.Token

class MainScreen(
    private val title: String,

    ) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MainScreenVM = koinInject()
        Napier.log(LogLevel.ASSERT, "MainScreenVM", message = viewModel.toString())
        val availability by viewModel.availability.collectAsState()
        val openCamera by viewModel.openCamera.collectAsState()
        val state by viewModel.state.collectAsState()
        val suspendTaskState by viewModel.suspendTaskDomain.collectAsState()
        val profileName by viewModel.profileName.collectAsState()

        val ticketInfoScreen =
            rememberScreen(presentation.nav.Screen.TicketProcess.TicketInfo)
        val notificationScreen =
            rememberScreen(Notification)
        val accountScreen =
            rememberScreen(AccountInfo)
        val settingsScreen =
            rememberScreen(Menu.Settings)
        val formViewerScreen = rememberScreen(Menu.FormViewer)

        val aboutScreen = rememberScreen(Menu.About)
        val gpsTrackingReportScreen = rememberScreen(Menu.GpsTrackingReport)
        val loginScreen = rememberScreen(presentation.nav.Screen.Auth.Login)

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
                    stringResource(MR.strings.filters)
                }

                MainEvent.Logout -> {
                    stringResource(MR.strings.logout)
                }

                MainEvent.AvailabilityStatus -> {
                    stringResource(MR.strings.availability_status)
                }

                MainEvent.CancelTicket -> {
                    stringResource(MR.strings.cancel_ticket)
                }

                MainEvent.MoreOptions -> {
                    stringResource(MR.strings.more_options)

                }

                MainEvent.SuspendTicket -> {
                    stringResource(MR.strings.suspend_ticket)
                }

                MainEvent.SuspendReason -> {
                    stringResource(MR.strings.suspend_reason)

                }

                MainEvent.CancelReason -> {
                    stringResource(MR.strings.cancel_reason)
                }

                MainEvent.Default -> {
                    ""
                }


                MainEvent.AcceptTicket -> {
                    ""
                }
            }


        BaseScreen(
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            hasDrawer = true,
            topBar = {
                CustomTopAppBar(
                    availability,
                    stringResource(MR.strings.ticket_list),
                    onNavigationItemClick = {
                        scope.launch {
                            if (scaffoldState.drawerState.isOpen)
                                scaffoldState.drawerState.close()
                            else
                                scaffoldState.drawerState.open()
                        }
                    },
                    onAvailabilityClick = {
                        viewModel.events.value = MainEvent.AvailabilityStatus
                    },
                    onNotificationClick = {
                        navigator.push(notificationScreen)
                    })
            },
            drawerContent = {
                DrawerHeader(profileName) {
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
                            Menu.About -> {

                                navigator.push(aboutScreen)

                            }

                            Menu.Logout -> {
                                viewModel.events.value = MainEvent.Logout
                            }

                            Menu.MyTickets -> {
//                        navigator.push(settingsScreen)

                            }

                            Menu.Settings -> {
                                navigator.push(settingsScreen)

                            }

                            Menu.GpsTrackingReport -> {
                                navigator.push(gpsTrackingReportScreen)
                            }

                            Menu.FormViewer -> {
                                navigator.push(formViewerScreen)

                            }
                        }
                        scaffoldState.drawerState.close()

                    }
                })
            },
            title = title,
            bottomSheetTitle = bottomSheetTitle,
            bottomBarBottomSheetContent = {
                when (events) {
                    MainEvent.ActionFilter -> {
                        Napier.log(LogLevel.ASSERT,tag="ActionFilter", message = "ActionFilter")

                        bottomSheetDoubleActionBottomBar(
                            BottomSheetDoubleActionModel(
                                stringResource(MR.strings.clear_all),
                                Color.Transparent,
                                textInverseDisabled,
                                stringResource(MR.strings.filters),
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
                                stringResource(MR.strings.cancel),
                                surfaceDefault,
                                textPrimary,
                                stringResource(MR.strings.logout),
                                surfaceBrandDefault,
                                textInverse
                            ), onFirstButtonClick = {


                            }, onSecondButtonClick = {
                                getSharedPref().put(Availability,false)
                                getSharedPref().put(PhoneNumber,"")
                                getSharedPref().put(Token, "")
                                navigator.popAll()
                                navigator.push(loginScreen)

                            })
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
                            viewModel.saveSuspendTask()
                            viewModel.events.value = MainEvent.Default
                        }


                    }

                    MainEvent.SuspendReason -> {

                    }

                    MainEvent.CancelReason -> {

                    }

                    MainEvent.AcceptTicket -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }

                    }

                }
            },
            bottomSheetContent = {

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
                            text = stringResource(MR.strings.are_you_logout),
                            style = body_large,
                            modifier = Modifier.padding(start = spacing2X)
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    MainEvent.AvailabilityStatus -> {
                        AvailabilityStatus(availability) {
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
//                        val factory: PermissionsControllerFactory =
//                            rememberPermissionsControllerFactory()
                        viewModel.loadSuspendTask()
                        SuspendTicketContentComponent(
                            taskid = viewModel.selectedTask.value!!.instanceTitle,
                            suspendTaskDomain = viewModel.suspendTaskDomain.value
                            ,
                            onSelectReason = {
                                viewModel.events.value = MainEvent.SuspendReason
                            },
                            onCompleted = { isComplete, description ->
                                viewModel.enableSuspendSubmit.value = isComplete
                                viewModel.suspendDescription.value = description
                            },
                            onCameraClick = {
                                viewModel.openCamera()
                            }
                        )

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

                    MainEvent.AcceptTicket -> {
//                        Napier.log(LogLevel.ASSERT,tag="Accepttt", message = "Accpettcket")
//                        navigator.push(ticketInfoScreen)
                    }

                }

            },
            content = {

                if (openCamera) {
                    viewModel.selectedTask.value?.let {
                        InternalStorage.createWorkItemImages(provideAppContext(),"Suspend",it.workId.toString())

                        Camera.ImagePicker("/wfmImages/Suspend/${it.workId}") { it ->
                            viewModel.suspendImageUri.value.plus(it.toString())
                        }
                        viewModel.updateCameraStatus(false)
                    }
                }
                if (availability) {
                    TicketListScreen(
                        searchText = "",
                        onEvent = { mainEvent: MainEvent, task: TaskDomain? ->
                            Napier.i("TicketListScreen")
                            viewModel.events.value = mainEvent
                            viewModel.selectedTask.value = task
                        },
                        tasks = ArrayList(viewModel.tasks.toList())
                        , onAccept = {
                            viewModel.selectedTask.value = it

                            navigator.push(ticketInfoScreen)
                        }
                    )
                }

            },
            onCloseBottomSheet = {
                viewModel.events.value = MainEvent.Default
            })
    }


}

