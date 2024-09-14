package presentation.screens.ticket_process.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBar
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import presentation.screens.ticket_process.components.processBar
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.CompleteFlowDialog
import presentation.model.BottomSheetDoubleActionModel
import presentation.model.SingleButtonActionModel
import presentation.nav.Screen.Main.Menu
import presentation.screens.main.components.EditPhotoComponent
import presentation.screens.main.components.PhotoPreviewComponent
import presentation.screens.main.compose.BaseScreen
import presentation.screens.main.events.TicketProcessEvent
import presentation.screens.ticket_process.components.bottomSingleActionComponent
import presentation.screens.ticket_process.events.StepEvent
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary
import utils.FormViewerTypes
import utils.PROCEED
import utils.ServiceState
import utils.ViewStates
import utils.initialize
import utils.validateComponents
import kotlin.random.Random


class TicketProcessScreen(
    private val ticketNumber: String
) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TicketProcessVM = koinInject()
        val currentLevelState by viewModel.currentLevel.collectAsState()
        var indexPhotoSelected by remember { mutableStateOf(0) }
        var componentKey by remember { mutableStateOf("0") }
        var componentId by remember { mutableStateOf("0") }

        val positionSelectedPhotoForEdit by viewModel.positionSelected.collectAsState()
        var isBottomSheetOpen by remember { mutableStateOf(true) }
        val events by viewModel.events
        var changeState = MutableStateFlow(0)
        val stepDetails by viewModel.stepDetails.collectAsState()
        val stepEvent by viewModel.stepEvent.collectAsState()
        val reloadState by viewModel.reloadState.collectAsState()
        val state by viewModel.state.collectAsState()
        val savedIndex by viewModel.savedIndex.collectAsState()
        val savedParentIndex by viewModel.savedParentIndex.collectAsState()

        var isClickable by remember { mutableStateOf(true) }
        val mainScreen = rememberScreen(Menu.MyTickets)

        val ticketFlowCompletedState = viewModel.ticketFlowCompleted.collectAsState()





        LaunchedEffect(Unit) {
            viewModel.updateTicketNumber(ticketNumber)
            viewModel.getMokStepsForm(PROCEED.INITIAL)
        }

        LaunchedEffect(stepEvent) {
            when (stepEvent) {
                StepEvent.END -> {

                }

                StepEvent.IN_PROCESS -> {

                }

                StepEvent.START -> {
                    navigator.pop()
                }

                StepEvent.INITIAL -> {

                }
            }
        }
        val bottomSheetTitle: String =
            when (events) {

                TicketProcessEvent.PhotoPreview -> {
                    stringResource(MR.strings.photo_preview)
                }

                TicketProcessEvent.EditPhoto -> {
                    stringResource(MR.strings.edit_photo)

                }

                TicketProcessEvent.DeletePhoto -> {
                    stringResource(MR.strings.delete_photo)

                }

                TicketProcessEvent.Default -> {
                    ""

                }

                TicketProcessEvent.InProgress -> {
                    ""
                }
            }


        fun backClick() {

            if (viewModel.events.value == TicketProcessEvent.Default) {

                scope.launch {
                    async { viewModel.saveAndDeletePhotoByComponentKey() }.await()
                    if (state is ViewStates.Success) {
                        viewModel.updateLevel(PROCEED.PREVIOUS)
                    }
                }
            } else {
                when (viewModel.events.value) {
                    TicketProcessEvent.PhotoPreview -> {
                        viewModel.events.value = TicketProcessEvent.Default
                    }

                    TicketProcessEvent.EditPhoto -> {
                        DrawController.reset()
                        viewModel.events.value = TicketProcessEvent.PhotoPreview
                    }

                    TicketProcessEvent.DeletePhoto -> {
                        viewModel.events.value = TicketProcessEvent.PhotoPreview
                    }

                    else -> {
                        viewModel.events.value = TicketProcessEvent.Default

                    }
                }

            }
        }

        BaseScreen(
            viewModel = viewModel,
            title = stringResource(MR.strings.ticket_process),
            scaffoldState = scaffoldState,
            hasDrawer = false,
            bottomSheetHasHeader = viewModel.events.value != TicketProcessEvent.Default,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.ticket_process)) {
                    Napier.log(
                        LogLevel.ASSERT,
                        tag = "backButtonEvent",
                        message = isClickable.toString()
                    )
                    if (isClickable) {
                        isClickable = false
                        backClick()
                        scope.launch {
                            delay(500)
                            isClickable = true
                        }
                    }
                }
            },
            bottomSheetTitle = bottomSheetTitle,

            bottomBarBottomSheetContent = {

                when (events) {

                    TicketProcessEvent.PhotoPreview -> {

                    }

                    TicketProcessEvent.EditPhoto -> {

                    }

                    TicketProcessEvent.DeletePhoto -> {

                        bottomSheetDoubleActionBottomBar(
                            BottomSheetDoubleActionModel(
                                stringResource(MR.strings.cancel),
                                surfaceDefault,
                                textPrimary,
                                stringResource(MR.strings.delete),
                                Color.Red,
                                textInverse
                            ), onFirstButtonClick = {
                                viewModel.events.value = TicketProcessEvent.PhotoPreview
                            }, onSecondButtonClick = {
                                viewModel.updateImageUriForDeletePhoto(
                                    positionSelectedPhotoForEdit,
                                    componentKey,
                                    componentId
                                )


                            })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }


                    }

                    TicketProcessEvent.Default -> {

                        bottomSingleActionComponent(
                            SingleButtonActionModel(
                                stringResource(MR.strings.resume),
                                surfaceBrandDefault,
                                textInverse
                            ), onClick = {
                                scope.launch {
                                    val errors = validateComponents(viewModel.tempComponentList) {
                                        viewModel.updateTempComponentList(it)
                                    }

                                    if (errors.isEmpty()) {
                                        async { viewModel.saveAndDeletePhotoByComponentKey() }.await()
                                        if (state is ViewStates.Success) {
                                            viewModel.updateLevel(PROCEED.NEXT)
                                        }
                                    }
                                }


                            })

                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }

                    TicketProcessEvent.InProgress -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }

            },
            bottomSheetContent = {

                when (events) {

                    TicketProcessEvent.PhotoPreview -> {
                        PhotoPreviewComponent(viewModel.findPhotosByComponentId(
                            componentId,
                            componentKey
                        ),
                            indexPhotoSelected,
                            onEditPhotoClick = { position ->
                                indexPhotoSelected = position
                                viewModel.updatePositionSelected(position)
                                viewModel.events.value = TicketProcessEvent.EditPhoto
                            },
                            onDeletePhoto = {

                                viewModel.updatePositionSelected(it)
                                viewModel.events.value = TicketProcessEvent.DeletePhoto


                            },
                            onSaveChangeAngle = {

                                viewModel.events.value = TicketProcessEvent.Default

                            })

                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }

                    TicketProcessEvent.EditPhoto -> {

                        EditPhotoComponent(
                            angle = 0.0F,
                            id = componentId,
                            key = componentKey,
                            path = InternalStorage.getProcessRouteEdited(provideAppContext()),
                            photoDomain = viewModel.photoDomainList[viewModel.findPhotoIndexByIdAndPosition(
                                componentKey,
                                componentId,
                                positionSelectedPhotoForEdit
                            ) ?: 0],
                            onEditUri = { editUri, originUri ->
                                viewModel.updateImageUriForEditPhoto(
                                    editUri,
                                    positionSelectedPhotoForEdit,
                                    componentKey,
                                    componentId
                                )

                            })

                    }

                    TicketProcessEvent.DeletePhoto -> {

                        Text(
                            text = stringResource(MR.strings.sure_delete_photo),
                            style = body_large,
                            modifier = Modifier.padding(start = spacing2X)
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }

                    TicketProcessEvent.Default -> {

                    }

                    TicketProcessEvent.InProgress -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }


            },
            content = {
                Column {
                    processBar(stepDetails, currentLevelState)
                    if (reloadState) {
                        initialize(
                            savedIndex = savedIndex,
                            savedParentIndex = savedParentIndex,
                            taskID = currentLevelState.toString(),
                            modifier = Modifier,
                            photoDomainList = viewModel.photoDomainList,
                            components = viewModel.tempComponentList,
                            onClickImage = { index, key, id ->
                                componentKey = key
                                componentId = id
                                indexPhotoSelected = index
                                viewModel.events.value = TicketProcessEvent.PhotoPreview

                            },


                            onChanges = { component, listValueDomain ->

                                viewModel.handleLogics()

                                listValueDomain?.let { listValues ->
                                    if (component.type == FormViewerTypes.ImageView) {
                                        viewModel.updateUriPhotoComponent(component, listValues)
                                    }
                                }
                            },
                            onAddItem = { listComponent, listValueDomain, listIndexParent, indexChild ->
                                viewModel.addOrRemoveComponentDomainRepeatableToList(
                                    listComponent,
                                    indexChild
                                )
                            },
                            onRemoveItem = { listComponent, listValueDomain, listIndexParent, indexChild ->
                                viewModel.addOrRemoveComponentDomainRepeatableToList(
                                    listComponent,
                                    indexChild
                                )
                            },

                            )
                    }
                }
                CompleteFlowDialog(
                    showDialog = ticketFlowCompletedState.value,
                    message = MR.strings.data_sent_complete,
                    titleButton = MR.strings.submit,
                    onDismiss = {
                        viewModel.updateTicketFlowState(false)
                        navigator.popAll()
                        navigator.push(mainScreen)
                    },
                    onConfirm = {
                        viewModel.updateTicketFlowState(false)
                        navigator.popAll()
                        navigator.push(mainScreen)
                    })

            }, onCloseBottomSheet = {
                when (viewModel.events.value) {
                    TicketProcessEvent.PhotoPreview -> {
                        viewModel.events.value = TicketProcessEvent.Default
                    }

                    TicketProcessEvent.EditPhoto -> {
                        DrawController.reset()
                        viewModel.events.value = TicketProcessEvent.PhotoPreview
                    }

                    TicketProcessEvent.DeletePhoto -> {
                        viewModel.events.value = TicketProcessEvent.PhotoPreview
                    }

                    else -> {
                        viewModel.events.value = TicketProcessEvent.Default
                    }

                }
                isBottomSheetOpen = false

            }, onBackPressed = {

                Napier.log(
                    LogLevel.ASSERT,
                    tag = "backButtonEvent",
                    message = isClickable.toString()
                )
                if (isClickable) {
                    isClickable = false
                    backClick()
                    scope.launch {
                        delay(500)
                        isClickable = true
                    }
                }


            })


    }


}

