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
import domain.models.PhotoDomain
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.MR
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
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
import utils.AsyncStatus
import utils.ViewStates


import utils.initialize
import utils.validateComponents
import kotlin.random.Random

class TicketInfoScreen(
    private val ticket_number: String
) : Screen {
    @OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow

        val ticketProcessScreen =
            rememberScreen(presentation.nav.Screen.TicketProcess.TicketProcessScreen(ticket_number))

        val viewModel: TicketInfoVM = koinInject()
        val taskIDValue by viewModel.ticketNumber.collectAsState()
        val positionSelectedPhotoForEdit by viewModel.positionSelected.collectAsState()
        var indexPhotoSelected by remember { mutableStateOf(0) }
        var componentId by remember { mutableStateOf("0") }
        var isBottomSheetOpen by remember { mutableStateOf(true) }
        var changeState = MutableStateFlow(0)

        val events by viewModel.events
        val viewState by viewModel.state.collectAsState()



        LaunchedEffect(Unit) {
            viewModel.getInitialForm(ticket_number)
            viewModel.updateTicketNumber(ticket_number)

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


                                val errors = validateComponents(viewModel.tempComponentList) {
                                    viewModel.updateTempComponentList(it)
                                }

                                scope.launch {
//                                if (errors.isEmpty()) {
                                   async { viewModel.saveAndDeletePhotoByComponentKey() }.await()
                                    if (viewState is ViewStates.Success)
                                        navigator.push(TicketProcessScreen(viewModel.ticketNumber.value))
//                                }
                                }

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
                            key = componentId,
                            photoDomain = viewModel.photoDomainList[viewModel.findPhotoIndexByIdAndPosition(
                                componentId,
                                positionSelectedPhotoForEdit
                            ) ?: 0],
                            path = "",
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
                    taskID = taskIDValue,
                    modifier = Modifier,
                    photoDomainList = viewModel.photoDomainList,
                    components = viewModel.tempComponentList,
                    onClickImage = { index, id ->
                        componentId = id
                        indexPhotoSelected = index
                        viewModel.events.value = TicketInfoEvent.PhotoPreview

                    },
                    onFixChange = { text ->
                        changeState.update { Random.nextInt() }

                    },
                    onAddItem = {listComponent, listValueDomain, listIndexParent, indexChild ->
                        viewModel.addOrRemoveComponentDomainRepeatableToList(
                            listComponent,
                            indexChild
                        )
                    },
                    onRemoveItem = {listComponent, listValueDomain, listIndexParent, indexChild ->
                        viewModel.addOrRemoveComponentDomainRepeatableToList(
                            listComponent,
                            indexChild
                        )
                    },
                    onChanges = { listComponent, listValueDomain, listIndexParent, indexChild ->

                        viewModel.handleLogics()

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



