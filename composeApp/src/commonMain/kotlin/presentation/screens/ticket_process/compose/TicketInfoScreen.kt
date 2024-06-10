package presentation.screens.ticket_process.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBar
import com.irancell.nwg.wfm.presentation.theme.spacing2X

import presentation.components.MenuItemsTopBar
import presentation.screens.main.compose.BaseScreen
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.BottomSheetDoubleActionModel
import presentation.model.SingleButtonActionModel
import presentation.screens.main.components.EditPhotoComponent
import presentation.screens.main.components.PhotoPreviewComponent
import presentation.screens.main.events.TicketInfoEvent
import presentation.screens.ticket_process.components.bottomSingleActionComponent
import presentation.screens.ticket_process.viewModel.TicketInfoVM
import presentation.theme.body_large
import presentation.theme.surfaceDefault
import presentation.theme.textPrimary


import utils.initialize

class TicketInfoScreen(
    private val taskId: Long
) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow

        val ticketProcessScreen =
            rememberScreen(presentation.nav.Screen.TicketProcess.TicketProcessScreen(taskId))

        val viewModel: TicketInfoVM = koinInject()
        val taskIDValue by viewModel.taskId.collectAsState()
        val positionSelectedPhotoForEdit by viewModel.positionSelected.collectAsState()
        var indexPhotoSelected by remember { mutableStateOf(0) }
        var componentId by remember { mutableStateOf("0") }
        var isBottomSheetOpen by remember { mutableStateOf(true) }

        val events by viewModel.events

        LaunchedEffect(Unit) {


            viewModel.getInitialForm(taskId)
            viewModel.updateTaskId(taskId)
        }


        LaunchedEffect(viewModel.events.value) {

        }


        val bottomSheetTitle: String =
            when (events) {

                TicketInfoEvent.PhotoPreview -> {
                    stringResource(MR.strings.photo_preview)
                }

                TicketInfoEvent.EditPhoto -> {
                    stringResource(MR.strings.edit_photo)

                }

                TicketInfoEvent.DeletePhoto -> {
                    stringResource(MR.strings.delete_photo)

                }

                TicketInfoEvent.Default -> {
                    ""

                }

            }


        BaseScreen(
            viewModel = viewModel,
            title = stringResource(MR.strings.ticket_info),
            scaffoldState = scaffoldState,
            hasDrawer = false,
            bottomSheetHasHeader = viewModel.events.value != TicketInfoEvent.Default,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.ticket_info)) {
                    navigator.pop()
                }
            },
            bottomSheetTitle = bottomSheetTitle,

            bottomBarBottomSheetContent = {

                when (events) {

                    TicketInfoEvent.PhotoPreview -> {

                    }

                    TicketInfoEvent.EditPhoto -> {

                    }

                    TicketInfoEvent.DeletePhoto -> {

                        bottomSheetDoubleActionBottomBar(
                            BottomSheetDoubleActionModel(
                                stringResource(MR.strings.cancel),
                                surfaceDefault,
                                textPrimary,
                                stringResource(MR.strings.delete),
                                Color.Red,
                                textInverse
                            ), onFirstButtonClick = {
                                viewModel.events.value = TicketInfoEvent.PhotoPreview
                            }, onSecondButtonClick = {
                                viewModel.updateImageUriForDeletePhoto(
                                    positionSelectedPhotoForEdit,
                                    componentId
                                )


                            })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }


                    }

                    TicketInfoEvent.Default -> {

                        bottomSingleActionComponent(
                            SingleButtonActionModel(
                                stringResource(MR.strings.resume),
                                surfaceBrandDefault,
                                textInverse
                            ), onClick = {
                                navigator.push(ticketProcessScreen)
                            })

                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }

                }

            },
            bottomSheetContent = {


                when (events) {

                    TicketInfoEvent.PhotoPreview -> {
                        PhotoPreviewComponent(viewModel.findPhotosByComponentId(componentId),
                            indexPhotoSelected,
                            onEditPhotoClick = { position ->
                                indexPhotoSelected = position
                                viewModel.updatePositionSelected(position)
                                viewModel.events.value = TicketInfoEvent.EditPhoto


                            },
                            onDeletePhoto = {

                                viewModel.updatePositionSelected(it)
                                viewModel.events.value = TicketInfoEvent.DeletePhoto


                            },
                            onSaveChangeAngle = {

                                viewModel.events.value = TicketInfoEvent.Default

                            })

                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }

                    TicketInfoEvent.EditPhoto -> {

                        EditPhotoComponent(
                            angle = 0.0F,

                            photoDomain = viewModel.photoDomainList[viewModel.findPhotoIndexByIdAndPosition(
                                componentId,
                                positionSelectedPhotoForEdit
                            ) ?: 0],
                            onEditUri = { editUri, originUri ->
                                viewModel.updateImageUriForEditPhoto(
                                    editUri,
                                    positionSelectedPhotoForEdit,
                                    componentId
                                )

                            })

                    }

                    TicketInfoEvent.DeletePhoto -> {

                        Text(
                            text = stringResource(MR.strings.sure_delete_photo),
                            style = body_large,
                            modifier = Modifier.padding(start = spacing2X)
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }
                    TicketInfoEvent.Default -> {

                    }
                }


            },
            content = {


                initialize(
                    taskID = taskIDValue.toString(),
                    modifier = Modifier,
                    photoDomainList = viewModel.photoDomainList,
                    components = viewModel.tempComponentList,
                    onClickImage = { index, id ->
                        componentId = id
                        indexPhotoSelected = index
                        viewModel.events.value = TicketInfoEvent.PhotoPreview

                    },
                    onChanges = { listComponent, listValueDomain, listIndexParent, indexChild ->

                       viewModel.addOrRemoveComponentDomainRepeatableToList(listComponent, indexChild)


                        listValueDomain?.let { it1 ->
                            viewModel.updateUriPhotoComponent(
                                viewModel.tempComponentList, listIndexParent, indexChild,
                                it1
                            )
                        }


                    },
                )


            },


            onCloseBottomSheet = {
                when (viewModel.events.value) {
                    TicketInfoEvent.PhotoPreview -> {
                        viewModel.events.value = TicketInfoEvent.Default
                    }
                    TicketInfoEvent.EditPhoto -> {
                        DrawController.reset()
                        viewModel.events.value = TicketInfoEvent.PhotoPreview
                    }
                    TicketInfoEvent.DeletePhoto -> {
                        viewModel.events.value = TicketInfoEvent.PhotoPreview
                    }
                    else -> {
                        viewModel.events.value = TicketInfoEvent.Default
                    }

                }
                isBottomSheetOpen = false

            }, onBackPressed = {
                if (viewModel.events.value == TicketInfoEvent.Default) {
                    navigator.pop()
                } else {
                    when (viewModel.events.value) {
                        TicketInfoEvent.PhotoPreview -> {
                            viewModel.events.value = TicketInfoEvent.Default
                        }
                        TicketInfoEvent.EditPhoto -> {
                            DrawController.reset()
                            viewModel.events.value = TicketInfoEvent.PhotoPreview
                        }
                        TicketInfoEvent.DeletePhoto -> {
                            viewModel.events.value = TicketInfoEvent.PhotoPreview
                        }
                        else -> {
                            viewModel.events.value = TicketInfoEvent.Default

                        }
                    }

                }
            }

        )

    }

}



