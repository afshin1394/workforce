package presentation.screens.main.compose

import androidx.compose.foundation.background
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
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.task.TaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackButtonHandler
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.Camera
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.ExitApp
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.checkConnectivity
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import presentation.components.CompleteFlowDialog
import presentation.components.CustomDialog
import presentation.components.CustomDialogDoubleAction
import presentation.components.CustomTopAppBar
import presentation.components.DrawerBody
import presentation.components.DrawerHeader

import presentation.screens.main.components.AvailabilityStatus
import presentation.screens.main.components.CancelTicketBottomBarComponent
import presentation.screens.main.components.CancelTicketComponent
import presentation.screens.main.components.EditPhotoComponent
import presentation.screens.main.components.MoreOptions
import presentation.screens.main.components.PhotoPreviewComponent
import presentation.screens.main.components.SuspendTicketBottomBarComponent
import presentation.screens.main.components.SuspendTicketContentComponent
import presentation.screens.ticket_process.compose.TicketInfoScreen
import presentation.theme.backgroundBackground3
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary
import utils.Availability
import utils.PhoneNumber
import utils.ServiceState
import utils.Token

class MainScreen(

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
        val positionSelectedPhotoForEdit by viewModel.positionSelected.collectAsState()
        var indexPhotoSelected by remember { mutableStateOf(0) }
        val showAcceptDialog by viewModel.showAcceptDialog.collectAsState()
        val isTicketEditedState by viewModel.ticketIsEdited.collectAsState()
        var isClickable by remember { mutableStateOf(true) }


        val reloadState by viewModel.reload.collectAsState()
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
        val drawerState =
            rememberDrawerState(initialValue = DrawerValue.Closed)
        val events by viewModel.events


        var hasDrawer by mutableStateOf(false)
        var showContent by remember { mutableStateOf(false) }

        LaunchedEffect(true) {

            showContent=true


        }


        val suspendItems by lazy {
            viewModel.suspendItems
        }

        val cancelItems by lazy {
            viewModel.cancelItems
        }


        val underDevelopment = stringResource(MR.strings.under_development)

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


            }

        Camera.onResult {obj,uri->
            viewModel.updateSuspendTicketImageUri(uri.toString())
        }


        if (showContent){
            hasDrawer=true
            BaseScreen(
                viewModel = viewModel,
                scaffoldState = scaffoldState,
                drawerState = drawerState,
                hasDrawer = hasDrawer,
                hasSwipeDrawer = viewModel.events.value != MainEvent.PhotoPreview && viewModel.events.value != MainEvent.EditPhoto,
                topBar = {
                    CustomTopAppBar(
                        availability,
                        stringResource(MR.strings.ticket_list),
                        onNavigationItemClick = {
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
                            }
                        },
                        onAvailabilityClick = {
                            viewModel.events.value = MainEvent.AvailabilityStatus
                        },
                        onNotificationClick = {
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment )
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

                            if (drawerState.isOpen)
                                drawerState.close()
                            else
                                drawerState.open()
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
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment )
                                    }
//                                navigator.push(aboutScreen)
                                }

                                Menu.Logout -> {
                                    viewModel.events.value = MainEvent.Logout
                                }

                                Menu.MyTickets -> {

                                }

                                Menu.Settings -> {
                                    navigator.push(settingsScreen)

                                }

                                Menu.GpsTrackingReport -> {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment )
                                    }
//                                navigator.push(gpsTrackingReportScreen)
                                }

                                Menu.FormViewer -> {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment )
                                    }
//                                navigator.push(formViewerScreen)

                                }


                            }

                        }
                    })
                },
                title = stringResource(MR.strings.ticket_list),
                bottomSheetTitle = bottomSheetTitle,
                bottomBarBottomSheetContent = {
                    when (events) {
                        MainEvent.ActionFilter -> {
                            Napier.log(LogLevel.ASSERT, tag = "ActionFilter", message = "ActionFilter")

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
                                    viewModel.logoutCallApi()
                                    navigator.popAll()
                                    navigator.push(loginScreen)

                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.AvailabilityStatus -> {

                        }

                        MainEvent.PhotoPreview -> {

                        }

                        MainEvent.EditPhoto -> {

                        }

                        MainEvent.DeletePhoto -> {


                            bottomSheetDoubleActionBottomBar(
                                BottomSheetDoubleActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.delete),
                                    Color.Red,
                                    textInverse
                                ), onFirstButtonClick = {
                                    viewModel.events.value = MainEvent.PhotoPreview
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
                                    scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment )
                                }
                                viewModel.events.value = MainEvent.Default
                            }
                        }

                        MainEvent.MoreOptions -> {


                        }

                        MainEvent.SuspendTicket -> {
                            SuspendTicketBottomBarComponent(true) {

                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(message = underDevelopment )
                                }
//                            viewModel.saveSuspendTask()
                                viewModel.events.value = MainEvent.Default
                            }
                        }

                        MainEvent.SuspendReason -> {

                        }

                        MainEvent.CancelReason -> {

                        }

                        MainEvent.DiscardSuspendTicket -> {

                            bottomSheetDoubleActionBottomBar(
                                BottomSheetDoubleActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.discard),
                                    surfaceBrandDefault,
                                    textInverse
                                ), onFirstButtonClick = {
                                    scope.launch {
                                        viewModel.events.value = MainEvent.SuspendTicket
                                    }

                                }, onSecondButtonClick = {

                                    scope.launch {
                                        viewModel.events.value = MainEvent.Default
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
                                BottomSheetDoubleActionModel(
                                    stringResource(MR.strings.cancel),
                                    surfaceDefault,
                                    textPrimary,
                                    stringResource(MR.strings.exit),
                                    Color.Red,
                                    textInverse
                                ), onFirstButtonClick = {
                                    viewModel.events.value = MainEvent.Default
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
                            }, onCompleted = { viewModel.enableCancelSubmit.value = it
                            })
                        }

                        MainEvent.MoreOptions -> {


                            MoreOptions(onCancelClick = {
                                viewModel.events.value = MainEvent.CancelTicket
                            }, onSuspendClick = {
                                viewModel.photoDomainList.clear()
                                viewModel.loadSuspendTask()

                                viewModel.events.value = MainEvent.SuspendTicket

                            })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.SuspendTicket -> {


                            SuspendTicketContentComponent(
                                ticketNumber = viewModel.selectedTask.value!!.basic_info.ticket_number
                                    ?: "0",
                                photoDomainList = viewModel.photoDomainList,

                                suspendTaskDomain = suspendTaskState,
                                onSelectReason = {
                                    viewModel.events.value = MainEvent.SuspendReason
                                },
                                onDescription = { description ->
                                    viewModel.updateSuspendTicketDescription(description)
                                },
                                onCameraClick = {
                                    viewModel.openCamera()

                                },
                                onImageClick = {
                                    indexPhotoSelected = it
                                    viewModel.events.value = MainEvent.PhotoPreview
                                }
                            )


                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }

                        MainEvent.PhotoPreview -> {
                            PhotoPreviewComponent(viewModel.photoDomainList, indexPhotoSelected,
                                onEditPhotoClick = { position ->
                                    indexPhotoSelected = position
                                    viewModel.updatePositionSelected(position)
                                    viewModel.events.value = MainEvent.EditPhoto


                                },
                                onDeletePhoto = {

                                    viewModel.updatePositionSelected(it)
                                    viewModel.events.value = MainEvent.DeletePhoto


                                }, onSaveChangeAngle = {

                                    viewModel.events.value = MainEvent.SuspendTicket

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

                                    viewModel.events.value = MainEvent.PhotoPreview

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

                        MainEvent.Default -> {

                        }

                        MainEvent.AcceptTicket -> {

                        }


                    }

                },
                content = {
                    if (isTicketEditedState) {
                        viewModel.updateIsEditedTicket(false)
                        LaunchedEffect(Unit) {
                            navigator.push(
                                TicketInfoScreen(
                                    viewModel.selectedTask.value?.basic_info?.ticket_number.toString()
                                )
                            )
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
                                provideAppContext(),
                                "",
                                ""
                            )

                            Camera.launchCamera(
                                null,
                                InternalStorage.getSuspendRouteOriginal(
                                    provideAppContext()
                                ),
                                "Suspend"
                            )

                            viewModel.updateCameraStatus(false)
                        }
                    }
                    if (availability) {

                        if (reloadState) {
                            viewModel.getTasks()
                        }



                        TicketListScreen(
                            searchText = "",
                            onEvent = { mainEvent: MainEvent, task: TaskDomain? ->
                                if (isClickable) {
                                    isClickable = false
                                    viewModel.events.value = mainEvent
                                    viewModel.selectedTask.value = task
                                    viewModel.resetSuspendTask()
                                    scope.launch {
                                        delay(500)
                                        isClickable = true
                                    }
                                }
                                Napier.i("TicketListScreen")

                            },
                            tasks = ArrayList(viewModel.tasks.toList()), onAccept = {
                                if (isClickable) {
                                    BackgroundServiceApp.updateServiceState(ServiceState.Suspend)
                                    isClickable = false
                                    viewModel.checkIfTicketIsEdited()
                                    viewModel.selectedTask.value = it
                                    viewModel.resetSuspendTask()
                                    scope.launch {
                                        delay(500)
                                        isClickable = true
                                    }
                                }
                            }
                        )
                        if (viewModel.showAcceptDialog.value) {

                            if (isTicketEditedState) {
                                viewModel.selectedTask.value?.let {
                                    viewModel.updateShowAcceptDialog(false)
                                }
                            } else {

                                CustomDialogDoubleAction(
                                    showDialog = showAcceptDialog,
                                    message = MR.strings.continue_flow_message,
                                    title = MR.strings.continue_flow_title,
                                    titleButton = MR.strings.aaccept,
                                    onDismiss = {
                                        viewModel.updateShowAcceptDialog(false)
                                        BackgroundServiceApp.updateServiceState(ServiceState.Normal)
                                    },
                                    onConfirm = {
                                        viewModel.updateShowAcceptDialog(false)
                                        viewModel.selectedTask.value?.let {
                                            viewModel.updateEdited(true)
                                        }
                                    })
                            }

                        }


                    }


                },
                onCloseBottomSheet = {

                    when (viewModel.events.value) {
                        MainEvent.PhotoPreview -> {
                            viewModel.events.value = MainEvent.SuspendTicket
                        }

                        MainEvent.EditPhoto -> {

                            DrawController.reset()
                            viewModel.events.value = MainEvent.PhotoPreview

                        }

                        MainEvent.DeletePhoto -> {
                            viewModel.events.value = MainEvent.PhotoPreview

                        }

                        MainEvent.SuspendTicket -> {
                            viewModel.events.value = MainEvent.DiscardSuspendTicket

                        }

                        else -> {
                            viewModel.events.value = MainEvent.Default

                        }
                    }

                }, onBackPressed = {
                    if (viewModel.events.value == MainEvent.Default) {
                        scope.launch {
                            Napier.log(
                                LogLevel.ASSERT,
                                tag = "drawerState",
                                message = drawerState.isOpen.toString()
                            )
                            if (drawerState.isOpen)
                                drawerState.close()
                            viewModel.events.value = MainEvent.Exit
                        }


                    } else {
                        when (viewModel.events.value) {
                            MainEvent.PhotoPreview -> {
                                viewModel.events.value = MainEvent.SuspendTicket
                            }

                            MainEvent.EditPhoto -> {

                                DrawController.reset()
                                viewModel.events.value = MainEvent.PhotoPreview

                            }

                            MainEvent.DeletePhoto -> {
                                viewModel.events.value = MainEvent.PhotoPreview

                            }

                            MainEvent.SuspendTicket -> {
                                viewModel.events.value = MainEvent.DiscardSuspendTicket

                            }

                            else -> {
                                viewModel.events.value = MainEvent.Default

                            }
                        }

                    }
                }


            )
        }

    }


}

