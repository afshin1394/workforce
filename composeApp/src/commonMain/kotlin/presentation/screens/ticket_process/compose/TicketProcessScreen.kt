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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBar
import com.irancell.nwg.wfm.presentation.components.bottomSingleActionComponent
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import presentation.screens.ticket_process.components.processBar
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.BottomSheetActionModel
import presentation.model.SingleButtonActionModel
import presentation.nav.Screen.Main.Menu
import presentation.screens.main.components.EditPhotoComponent
import presentation.screens.main.components.PhotoPreviewComponent
import presentation.screens.main.compose.BaseScreen
import presentation.screens.main.events.TicketProcessEvent

import presentation.screens.ticket_process.events.StepEvent
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary
import utils.FormViewerTypes
import utils.PROCEED
import utils.ViewStates
import utils.initialize
import utils.validateComponents


class TicketProcessScreen(
    private val ticketId: String,
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

        val updateTaskCompleteState = viewModel.updateTasksComplete.collectAsState()
        val scrollingState = viewModel.scrollingPosition.collectAsState()





        LaunchedEffect(Unit) {
            viewModel.updateTicketId(ticketId)
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

                TicketProcessEvent.TicketFlowCompleted -> {
                    "SuccessFully!"
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
            isShwCloseBtnBottomSheet = viewModel.events.value != TicketProcessEvent.TicketFlowCompleted,
            typeBottomSheet = if (viewModel.events.value == TicketProcessEvent.TicketFlowCompleted) "Success" else "Default",
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
                            BottomSheetActionModel(
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


                                viewModel.handleLogics {
                                  if (it.size>0){


                                      scope.launch {
                                          val errors = mutableMapOf<String, List<ResourceFormattedStringDesc>>()

                                          it.forEach {
                                              errors[it.componentId] = arrayListOf()
                                          }
                                          viewModel.showFirstError(errors)
                                      }

                                  }else{
                                      scope.launch {
                                          val errors = validateComponents(viewModel.tempComponentList) {
                                              viewModel.updateTempComponentList(it)
                                          }
                                          if (errors.isNotEmpty()) {
                                              viewModel.showFirstError(errors)
                                          }

                                          if (errors.isEmpty()) {
                                              async { viewModel.saveAndDeletePhotoByComponentKey() }.await()
                                              if (state is ViewStates.Success) {
                                                  viewModel.updateLevel(PROCEED.NEXT)
                                              }
                                          }
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

                    TicketProcessEvent.TicketFlowCompleted -> {

                        bottomSingleActionComponent(
                            SingleButtonActionModel(
                                stringResource(MR.strings.submit),
                                surfaceBrandDefault,
                                textInverse
                            ), onClick = {
                                viewModel.updateTicketFlowState(false)
                                viewModel.updateTasks()


                            })

                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
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

                    TicketProcessEvent.TicketFlowCompleted -> {

                        Text(
                            text = stringResource(MR.strings.data_sent_complete),
                            style = body_large,
                            modifier = Modifier.padding(start = spacing2X)
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }
                }


            },
            content = {
                LaunchedEffect(updateTaskCompleteState.value) {
                    if (updateTaskCompleteState.value) {
                        navigator.popAll()
                        navigator.push(mainScreen)
                    }
                }
                Column(
                    if (viewModel.events.value == TicketProcessEvent.TicketFlowCompleted) Modifier.blur(
                        7.dp
                    ) else Modifier
                ) {
                    processBar(stepDetails, currentLevelState)
                    if (reloadState) {
                        initialize(
                            isChild = false,
                            scrollingState = scrollingState.value,
                            savedIndex = savedIndex,
                            savedParentIndex = savedParentIndex,
                            taskID = currentLevelState.toString(),
                            modifier = Modifier,
                            photoDomainList = viewModel.photoDomainList,
                            components = viewModel.tempComponentList,
                            onClickImage = { index, key, id, component ->
                                viewModel.tempComponent.value = component
                                componentKey = key
                                componentId = id
                                indexPhotoSelected = index
                                viewModel.events.value = TicketProcessEvent.PhotoPreview
                            },


                            onChanges = { component, listValueDomain ->

                                viewModel.handleLogics{
                                    viewModel.extractLogicsModel.clear()

                                }

                                listValueDomain?.let { listValues ->
                                    if (component.type == FormViewerTypes.ImageView) {
                                        viewModel.updateUriPhotoComponent(component, listValues)
                                    }
                                }
                            },
                            onAddItem = { component, indexChild, scrollCallback ->
                                scope.launch {
                                    viewModel.addOrRemoveComponentDomainRepeatableToList(
                                        component,
                                        indexChild,
                                        scrollCallback
                                    )
                                }


                            },
                            onRemoveItem = { component, indexChild, scrollCallBack ->
                                scope.launch {
                                    viewModel.addOrRemoveComponentDomainRepeatableToList(
                                        component,
                                        indexChild,
                                        scrollCallBack
                                    )
                                }


                            },
                        )
                    }
                }
                /*    CompleteFlowDialog(
                        showDialog = ticketFlowCompletedState.value,
                        message = MR.strings.data_sent_complete,
                        titleButton = MR.strings.submit,
                        onDismiss = {
                            viewModel.updateTicketFlowState(false)
                            navigator.popAll()
                            navigator.push(mainScreen)
                        },
                        onConfirm = {
    //                        viewModel.updateTicketFlowState(false)
    //                        viewModel.updateTasks()
                        })*/

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


            }, shouldBlurOnBottomSheetExpansion = false)


    }


}

