package presentation.screens.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import presentation.nav.Screen.Main.*
import com.irancell.nwg.wfm.presentation.components.*
import presentation.screens.main.events.MainEvent
import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.theme.*
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.icerock.moko.resources.compose.painterResource
import presentation.screens.main.components.TicketListScreen
import dev.icerock.moko.resources.compose.stringResource
import domain.models.task.TaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.Camera
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.ExitApp
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import presentation.components.CustomTopAppBar
import presentation.components.DrawerBody
import presentation.components.DrawerHeader
import presentation.components.bottomSheetDoubleActionBottomBar
import presentation.model.BottomSheetActionModel
import presentation.screens.main.components.AvailabilityStatus
import presentation.screens.main.components.CancelTicketBottomBarComponent
import presentation.screens.main.components.CancelTicketComponent
import presentation.screens.main.components.EditPhotoComponent
import presentation.screens.main.components.MoreOptions
import presentation.screens.main.components.PhotoPreviewComponent
import presentation.screens.main.components.SuspendTicketBottomBarComponent
import presentation.screens.main.components.SuspendTicketContentComponent
import presentation.screens.main.viewmodel.TicketListStatus
import presentation.screens.ticket_process.compose.TicketInfoScreen
import presentation.screens.ticket_process.compose.TicketProcessScreen
import presentation.screens.ticket_process.compose.TicketStructureInfoScreen
import presentation.screens.download.compose.DownloadScreen
import presentation.theme.body_large
import presentation.theme.body_small
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary
import utils.AvailabilityStatus
import utils.NetworkStates
import utils.ServiceState

class MainScreen(private val forceReload: Boolean = false) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MainScreenVM = koinInject()
        Napier.log(LogLevel.ASSERT, "MainScreenVM", message = viewModel.toString())
        val availability by viewModel.availability.collectAsState()
        val openCamera by viewModel.openCamera.collectAsState()
        val suspendTaskState by viewModel.suspendTaskDomain.collectAsState()
        val profileName by viewModel.profileName.collectAsState()
        val positionSelectedPhotoForEdit by viewModel.positionSelected.collectAsState()
        var indexPhotoSelected by remember { mutableStateOf(0) }
        val isTicketEditedState by viewModel.ticketIsEdited.collectAsState()
        var isClickable by remember { mutableStateOf(true) }
        val reloadState by viewModel.reload.collectAsState()
        val accountScreen = rememberScreen(AccountInfo)
        val settingsScreen = rememberScreen(Menu.Settings)
        val aboutScreen = rememberScreen(Menu.About)
        val loginScreen = rememberScreen(presentation.nav.Screen.Auth.Login)
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val eventsState by viewModel.events.collectAsState()
        var hasDrawer by mutableStateOf(false)
        val availabilityStatus by viewModel.availabilityStatus.collectAsState()
        var showContent by remember { mutableStateOf(false) }
        val ticketListStatus by viewModel.ticketListStatus.collectAsState()
        val networkState by viewModel.networkState.collectAsState()


        val connectivityState = remember { mutableStateOf<NetworkConnection>(NetworkConnection.NONE) }


        LaunchedEffect(viewModel.konnectivity) {
           viewModel.konnectivity.currentNetworkConnectionState.collect { connection ->
                connectivityState.value = connection
            }
        }
        LaunchedEffect(Unit) {
            showContent = true
        }
        
        // Force reload if coming from download screen
        LaunchedEffect(forceReload) {
            if (forceReload) {
                viewModel.updateReloadState(true)
                viewModel.refreshTasks()
                viewModel.getActivityList()
            }
        }

        val suspendItems by lazy {
            viewModel.suspendItems
        }

        val cancelItems by lazy {
            viewModel.cancelItems
        }

        val underDevelopment = stringResource(MR.strings.under_development)

        val bottomSheetTitle: String =
            when (eventsState) {
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

                MainEvent.Exit -> {
                    stringResource(MR.strings.exit)
                }

                MainEvent.PhotoPreview -> {
                    stringResource(MR.strings.photo_preview)
                }

                MainEvent.DeletePhoto -> {
                    stringResource(MR.strings.delete_photo)
                }

                MainEvent.EditPhoto -> {
                    stringResource(MR.strings.edit_photo)
                }

                MainEvent.DiscardSuspendTicket -> {
                    stringResource(MR.strings.save_change)
                }

                MainEvent.ShowAcceptTicketDialog -> {
                    stringResource(MR.strings.continue_flow_title)
                }


                else -> {
                    ""
                }
            }

        Camera.onResult { _, uri ->
            viewModel.updateSuspendTicketImageUri(uri.toString())
        }

        Napier.log(
            LogLevel.ASSERT,
            tag = "availabilityStatus",
            message = availabilityStatus.toString()
        )


        if (showContent) {

            hasDrawer = true
            BaseScreen(
                viewModel = viewModel,
                scaffoldState = scaffoldState,
                drawerState = drawerState,
                hasDrawer = hasDrawer,
                isShwCloseBtnBottomSheet = viewModel.events.value != MainEvent.ShowAcceptTicketDialog,
                hasSwipeDrawer = viewModel.events.value != MainEvent.PhotoPreview && viewModel.events.value != MainEvent.EditPhoto,
                topBar = {
                    CustomTopAppBar(availabilityStatus,
                        stringResource(MR.strings.ticket_list),
                        onNavigationItemClick = {
                            scope.launch {
                                Napier.log(
                                    LogLevel.ASSERT,
                                    tag = "drawerState",
                                    message = drawerState.isOpen.toString()
                                )
                                if (drawerState.isOpen) drawerState.close()
                                else drawerState.open()
                            }
                        },
                        onAvailabilityClick = {
                            if (availabilityStatus == AvailabilityStatus.Available || availabilityStatus == AvailabilityStatus.Unavailable) {
                                viewModel.updateState(MainEvent.AvailabilityStatus)
                            }
                        },
                        onNotificationClick = {
                            scope.launch {
//                                viewModel.updateTask()
                                scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment)
                            }
//                        navigator.push(notificationScreen)
                        })
                },
                drawerContent = {
                    DrawerHeader(profileName) {
                        scope.launch {
                            Napier.log(
                                LogLevel.ASSERT,
                                tag = "drawerState",
                                message = drawerState.isOpen.toString()
                            )
                            if (drawerState.isOpen) drawerState.close()
                            else drawerState.open()
                        }
                        navigator.push(accountScreen)
                    }
                    DrawerBody(onItemClick = {
                        scope.launch {
                            Napier.log(
                                LogLevel.ASSERT,
                                tag = "drawerState",
                                message = drawerState.isOpen.toString()
                            )
                            if (drawerState.isOpen)
                                drawerState.close()
                            else
                                drawerState.open()
                            when (it) {
                                Menu.About -> {
//                                    scope.launch {
//                                        scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment)
//                                    }
                                    navigator.push(aboutScreen)
                                }

                                Menu.Logout -> {
                                    viewModel.updateState(MainEvent.Logout)
                                }

                                Menu.Settings -> {
                                    navigator.push(settingsScreen)
                                }

                                Menu.GpsTrackingReport -> {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment)
                                    }
//                                navigator.push(gpsTrackingReportScreen)
                                }

                                Menu.FormViewer -> {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment)
                                    }
//                                navigator.push(formViewerScreen)
                                }

                                Menu.Download -> {
                                    navigator.push(DownloadScreen())
                                }

                                else -> {}
                            }
                        }
                    })
                },
                title = stringResource(MR.strings.ticket_list),
                bottomSheetTitle = bottomSheetTitle,
                bottomBarBottomSheetContent = {
                    when (eventsState) {
                        MainEvent.ActionFilter -> {
                            Napier.log(
                                LogLevel.ASSERT, tag = "ActionFilter", message = "ActionFilter"
                            )
                            bottomSheetDoubleActionBottomBar(
                                BottomSheetActionModel(
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
                                        viewModel.updateState(MainEvent.Default)
                                    }
                                }, onSecondButtonClick = {
                                    scope.launch {
                                        viewModel.getActiveFilterItems()
                                        scaffoldState.bottomSheetState.collapse()
                                        viewModel.updateState(MainEvent.Default)
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
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.logout),
                                    surfaceBrandDefault,
                                    textInverse
                                ), onFirstButtonClick = {},
                                onSecondButtonClick = {
                                    viewModel.logoutCallApi {
                                        navigator.pop()
                                        navigator.push(loginScreen)
                                    }

                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.DeletePhoto -> {
                            bottomSheetDoubleActionBottomBar(
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.delete),
                                    Color.Red,
                                    textInverse
                                ), onFirstButtonClick = {
                                    viewModel.updateState(MainEvent.PhotoPreview)
                                }, onSecondButtonClick = {
                                    viewModel.updateSuspendTicketImageUriForDeletePhoto(
                                        viewModel.photoDomainList[positionSelectedPhotoForEdit].origin_uri,
                                        positionSelectedPhotoForEdit
                                    )

                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.CancelTicket -> {
                            CancelTicketBottomBarComponent(viewModel.enableCancelSubmit.value) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment)
                                }
                                viewModel.updateState(MainEvent.Default)
                            }
                        }

                        MainEvent.SuspendTicket -> {
                            SuspendTicketBottomBarComponent(true) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment)
                                }
//                            viewModel.saveSuspendTask()
                                viewModel.updateState(MainEvent.Default)
                            }
                        }

                        MainEvent.DiscardSuspendTicket -> {
                            bottomSheetDoubleActionBottomBar(
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.discard),
                                    surfaceBrandDefault,
                                    textInverse
                                ), onFirstButtonClick = {
                                    scope.launch {
                                        viewModel.updateState(MainEvent.SuspendTicket)
                                    }
                                }, onSecondButtonClick = {
                                    scope.launch {
                                        viewModel.updateState(MainEvent.Default)
                                    }
                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.AcceptTicket -> {
                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }

                        MainEvent.Exit -> {
                            bottomSheetDoubleActionBottomBar(
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.exit),
                                    Color.Red,
                                    textInverse
                                ), onFirstButtonClick = {
                                    viewModel.updateState(MainEvent.Default)
                                    scope.launch {
                                        scaffoldState.bottomSheetState.collapse()
                                    }
                                }, onSecondButtonClick = {
                                    ExitApp()
                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.ShowAcceptTicketDialog -> {
                            bottomSheetDoubleActionBottomBar(
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.accept),
                                    surfaceBrandDefault,
                                    textInverse
                                ), onFirstButtonClick = {
                                    scope.launch {
                                        BackgroundServiceApp.updateServiceState(ServiceState.Normal)
                                        viewModel.updateState(MainEvent.Default)
                                        viewModel.updateShowAcceptDialog(false)
                                    }
                                }, onSecondButtonClick = {
                                    scope.launch {
                                        viewModel.selectedTask.value?.let {
                                            viewModel.updateEdited(true)
                                            viewModel.updateIsEditedTicket(true) // Immediately update state
                                            viewModel.updateState(MainEvent.Default)
                                            viewModel.updateShowAcceptDialog(false)
                                        }
                                    }
                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }


                        else -> {}
                    }
                },
                bottomSheetContent = {
                    when (eventsState) {
                        MainEvent.ActionFilter -> {

                            if (viewModel.filterSectionItems.isNotEmpty()) {
                                CustomFilterSectionPreview(viewModel.filterSectionItems)
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            } else {

                                Box(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "No filters to show.",
                                        style = MaterialTheme.typography.h6,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }

                        MainEvent.Default -> {

                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }

                        MainEvent.Logout -> {
                            Column(
                                modifier = Modifier.padding(
                                    vertical = spacing2X, horizontal = spacing2X
                                )
                            ) {
                                Text(
                                    text = stringResource(MR.strings.are_you_logout),
                                    style = body_large,
                                    modifier = Modifier.padding(bottom = spacing2X)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(MR.images.warning),
                                        contentDescription = "warning",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(spacing1X))
                                    Text(
                                        text = stringResource(MR.strings.logout_warning),
                                        style = body_small,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = spacing1X)
                                    )
                                }
                            }

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
                                viewModel.updateState(MainEvent.CancelReason)
                            }, onCompleted = {
                                viewModel.enableCancelSubmit.value = it
                            })
                        }

                        MainEvent.MoreOptions -> {
                            MoreOptions(onTicketInfoClick = {

                                navigator.push(
                                    TicketStructureInfoScreen(
                                        ticketId = viewModel.ticketId.value,
                                        ticket_number = viewModel.ticketNumber.value
                                    )
                                )


                            /*    navigator.push(
                                    TicketInfoScreen(
                                        ticketId = viewModel.ticketId.value,
                                        ticket_number = viewModel.ticketNumber.value
                                    )
                                )*/
                            },
                                onCancelClick = {
                                viewModel.updateState(MainEvent.CancelTicket)
                            }, onSuspendClick = {
                                viewModel.photoDomainList.clear()
                                viewModel.loadSuspendTask()
                                viewModel.updateState(MainEvent.SuspendTicket)
                            },
                                onOpenInMapClick = {
                                    viewModel.updateState(MainEvent.OpenInMap)
                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.SuspendTicket -> {
                            SuspendTicketContentComponent(
                                ticketNumber = viewModel.selectedTask.value!!.ticket_number
                                    ?: "0",
                                photoDomainList = viewModel.photoDomainList,
                                suspendTaskDomain = suspendTaskState,
                                onSelectReason = {
                                    viewModel.updateState(MainEvent.SuspendReason)
                                },
                                onDescription = { description ->
                                    viewModel.updateSuspendTicketDescription(description)
                                }, onCameraClick = {
                                    viewModel.openCamera()
                                }, onImageClick = {
                                    indexPhotoSelected = it
                                    viewModel.updateState(MainEvent.PhotoPreview)
                                }
                            )

                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.PhotoPreview -> {
                            PhotoPreviewComponent(viewModel.photoDomainList,
                                indexPhotoSelected,
                                onEditPhotoClick = { position ->
                                    indexPhotoSelected = position
                                    viewModel.updatePositionSelected(position)
                                    viewModel.updateState(MainEvent.EditPhoto)
                                },
                                onDeletePhoto = {
                                    viewModel.updatePositionSelected(it)
                                    viewModel.updateState(MainEvent.DeletePhoto)
                                }, onSaveChangeAngle =  {updatedList ->
                                    viewModel.updateState(MainEvent.SuspendTicket)
                                })

                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.EditPhoto -> {
                            EditPhotoComponent(
                                angle = 0.0F,
                                id = "Suspend",
                                key = "Suspend",
                                path = InternalStorage.getSuspendRouteEdited(provideAppContext()),
                                photoDomain = viewModel.photoDomainList[positionSelectedPhotoForEdit],
                                onEditUri = { editUri, originUri ->

                                    viewModel.photoDomainList.getOrNull(positionSelectedPhotoForEdit)
                                        ?.let {
                                            viewModel.photoDomainList[positionSelectedPhotoForEdit] =
                                                it.copy(edited_uri = editUri)
                                        }
                                    viewModel.updateState(MainEvent.PhotoPreview)
                                })
                        }

                        MainEvent.DeletePhoto -> {
                            Text(
                                text = stringResource(MR.strings.sure_delete_photo),
                                style = body_large,
                                modifier = Modifier.padding(start = spacing2X)
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
                                viewModel.updateSuspendTicketReason(selectableItem.text)
                                viewModel.updateState(MainEvent.SuspendTicket)
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
                            SelectableComponentPreview(viewModel.cancelItems,
                                { index, selectableItem ->
                                    cancelItems[index] = selectableItem
                                    cancelItems.filter { it.id != selectableItem.id }.map {
                                        it.isSelected = false
                                        it.isSelectedState.value = false
                                    }
                                    viewModel.cancelReason.value = selectableItem.text
                                    viewModel.updateState(MainEvent.CancelTicket)
                                },
                                onSearch = { searchQuery ->
                                    cancelItems.clear()
                                    cancelItems.addAll(viewModel.cancelItems.filter {
                                        it.text.lowercase().contains(searchQuery.lowercase())
                                    })
                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.DiscardSuspendTicket -> {
                            Text(
                                text = stringResource(MR.strings.sure_discard_change),
                                style = body_large,
                                modifier = Modifier.padding(start = spacing2X)
                            )
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.Exit -> {

                            Text(
                                text = stringResource(MR.strings.sure_exit_app),
                                style = body_large,
                                modifier = Modifier.padding(start = spacing2X)
                            )
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.AcceptTicket -> {

                        }

                        MainEvent.ShowAcceptTicketDialog -> {
                            Text(
                                text = stringResource(MR.strings.continue_flow_message),
                                style = body_large,
                                modifier = Modifier.padding(start = spacing2X)
                            )
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }

                        }


                        else -> {}
                    }

                },
                content = {

                    when (ticketListStatus) {
                        TicketListStatus.Empty -> {
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(MR.images.ic_empty),
                                    contentDescription = "empty",
                                    modifier = Modifier.width(150.dp).height(120.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(MR.strings.empty_list),
                                    style = TextStyle(
                                        fontSize = 16.sp, fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.wrapContentSize()
                                )
                            }
                        }
                        TicketListStatus.Loading->{
                            CircularProgressIndicator()

                        }

                        else -> {}
                    }
                    if (eventsState == MainEvent.NoLocationFound) {
                        val noLocationFoundMessage = stringResource(MR.strings.noLocationFound)
                        LaunchedEffect(Unit) {
                            scaffoldState.bottomSheetState.collapse()
                            scaffoldState.snackbarHostState.showSnackbar(noLocationFoundMessage)
                        }

                    }

                    if (eventsState == MainEvent.OpenInMap)
                        viewModel.openInMapHandler()

                    // Handle navigation to ticket process screen
                    val navigationRequest by viewModel.navigationRequest.collectAsState()
                    navigationRequest?.let { (ticketId, ticketNumber) ->
                        LaunchedEffect(ticketId, ticketNumber) {
                            // Clear the navigation request immediately to prevent re-triggering
                            viewModel.clearNavigationRequest()
                            viewModel.updateIsEditedTicket(false)
                            viewModel.updateState(MainEvent.Default)
                            
                            // Navigate to the ticket process screen
                            try {
                                navigator.push(
                                    TicketProcessScreen(ticketId, ticketNumber)
                                )
                            } catch (e: Exception) {
                                Napier.e("Navigation error: ${e.message}", e)
                            }
                        }
                    }

                    if (viewModel.events.value == MainEvent.PhotoPreview) {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "drawerState",
                            message = drawerState.isOpen.toString()
                        )

                        scope.launch {
                            drawerState.close()
                        }
                    }

                    if (openCamera) {
                        viewModel.selectedTask.value?.let {
                            InternalStorage.createWorkItemImages(
                                provideAppContext(), "", ""
                            )

                            Camera.launchCamera(
                                null, InternalStorage.getSuspendRouteOriginal(
                                    provideAppContext()
                                ), "Suspend"
                            )

                            viewModel.updateCameraStatus(false)
                        }
                    }
                   if (connectivityState.value != NetworkConnection.NONE) {
                    if (availability) {
                        if (reloadState) {
                            viewModel.refreshTasks()
                           viewModel.getActivityList()
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (ticketListStatus == TicketListStatus.UnRecognized) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.5.dp),
                                    color = if (networkState == NetworkStates.NetworkConnectionNONE) Color.Red else surfaceBrandDefault
                                )
                            }
                            TicketListScreen(
                                searchText = "",
                                onEvent = { mainEvent: MainEvent, task: TaskDomain? ->
                                    viewModel.updateTicketId(task?.ticket_id.toString())
                                    viewModel.updateTicketNumber(task?.ticket_number.toString())
                                    viewModel.updateState(mainEvent)
                                    viewModel.selectedTask.value = task
                                    viewModel.resetSuspendTask()
                                },
                                tasks = viewModel.tasks,
                                tasksActivityListFilter = viewModel.tasksActivityList,
                                onAccept = {
                                    viewModel.checkIfTicketIsEdited()
                                    viewModel.selectedTask.value = it
                                    viewModel.resetSuspendTask()
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                  } else {
                       if (reloadState) {
                           viewModel.refreshTasks()

                       }
                       Column(
                           modifier = Modifier
                               .fillMaxWidth(),
                           horizontalAlignment = Alignment.CenterHorizontally
                       ) {
                           if (ticketListStatus == TicketListStatus.UnRecognized) {
                               LinearProgressIndicator(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .height(1.5.dp),
                                   color = if (networkState == NetworkStates.NetworkConnectionNONE) Color.Red else surfaceBrandDefault
                               )
                           }
                           TicketListScreen(
                               searchText = "",
                               onEvent = { mainEvent: MainEvent, task: TaskDomain? ->
                                   viewModel.updateTicketId(task?.ticket_id.toString())
                                   viewModel.updateTicketNumber(task?.ticket_number.toString())
                                   viewModel.updateState(mainEvent)
                                   viewModel.selectedTask.value = task
                                   viewModel.resetSuspendTask()
                               },
                               tasks = viewModel.tasks,
                               tasksActivityListFilter = viewModel.tasksActivityList,
                               onAccept = {
                                   viewModel.checkIfTicketIsEdited()
                                   viewModel.selectedTask.value = it
                                   viewModel.resetSuspendTask()
                               },
                               viewModel = viewModel
                           )
                       }
                    }
                    if (viewModel.showAcceptDialog.value && !isTicketEditedState) {
                        BackgroundServiceApp.updateServiceState(ServiceState.Suspend)
                        viewModel.updateState(MainEvent.ShowAcceptTicketDialog)
                    }
                },
                onCloseBottomSheet = {

                    when (viewModel.events.value) {
                        MainEvent.PhotoPreview -> {
                            viewModel.updateState(MainEvent.SuspendTicket)
                        }

                        MainEvent.EditPhoto -> {
                            DrawController.reset()
                            viewModel.updateState(MainEvent.PhotoPreview)
                        }

                        MainEvent.DeletePhoto -> {
                            viewModel.updateState(MainEvent.PhotoPreview)
                        }

                        MainEvent.SuspendTicket -> {
                            viewModel.updateState(MainEvent.DiscardSuspendTicket)
                        }

                        MainEvent.ShowAcceptTicketDialog -> {

                            viewModel.updateState(MainEvent.Default)

                        }

                        else -> {
                            viewModel.updateState(MainEvent.Default)
                        }
                    }
                },
                onBackPressed = {
                    if (viewModel.events.value == MainEvent.Default) {
                        scope.launch {
                            Napier.log(
                                LogLevel.ASSERT,
                                tag = "drawerState",
                                message = drawerState.isOpen.toString()
                            )
                            if (drawerState.isOpen)
                                drawerState.close()
                            viewModel.updateState(MainEvent.Exit)
                        }

                    } else {
                        when (viewModel.events.value) {
                            MainEvent.PhotoPreview -> {
                                viewModel.updateState(MainEvent.SuspendTicket)
                            }

                            MainEvent.EditPhoto -> {
                                DrawController.reset()
                                viewModel.updateState(MainEvent.PhotoPreview)
                            }

                            MainEvent.DeletePhoto -> {
                                viewModel.updateState(MainEvent.PhotoPreview)
                            }

                            MainEvent.SuspendTicket -> {
                                viewModel.updateState(MainEvent.DiscardSuspendTicket)
                            }

                            else -> {
                                viewModel.updateState(MainEvent.Default)
                            }
                        }
                    }
                }
            )
        }
    }
}

