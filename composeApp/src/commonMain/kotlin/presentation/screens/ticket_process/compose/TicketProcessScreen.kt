package presentation.screens.ticket_process.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBar
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.model.ProcessLevel
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.StringResource
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import presentation.screens.ticket_process.components.processBar
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.DrawController
import irancell.nwg.wfm.MR
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.BottomSheetDoubleActionModel
import presentation.model.SingleButtonActionModel
import presentation.screens.main.components.EditPhotoComponent
import presentation.screens.main.components.PhotoPreviewComponent
import presentation.screens.main.compose.BaseScreen
import presentation.screens.main.events.TicketInfoEvent
import presentation.screens.main.events.TicketProcessEvent
import presentation.screens.splash.compose.SplashScreen
import presentation.screens.ticket_process.components.bottomSingleActionComponent
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary
import utils.PROCEED
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
        var componentId by remember { mutableStateOf("0") }
        val positionSelectedPhotoForEdit by viewModel.positionSelected.collectAsState()
        var isBottomSheetOpen by remember { mutableStateOf(true) }
        val events by viewModel.events
        var changeState = MutableStateFlow(0)
        val stepDetails by viewModel.stepDetails.collectAsState()


        LaunchedEffect(Unit) {
            viewModel.updateTicketNumber(ticketNumber)
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

            }


        fun backClick() {
            if (viewModel.events.value == TicketProcessEvent.Default) {

                scope.launch {

                    viewModel.updateLevel(PROCEED.PREVIOUS)

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

                    backClick()

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
                                    viewModel.updateLevel(PROCEED.NEXT)
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

                    TicketProcessEvent.PhotoPreview -> {
                        PhotoPreviewComponent(viewModel.findPhotosByComponentId(componentId),
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
                }


            },
            content = {

                Column() {
                    processBar(stepDetails, currentLevelState)


                    initialize(
                        taskID = "33333",
                        modifier = Modifier,
                        photoDomainList = viewModel.photoDomainList,
                        components = viewModel.tempComponentList,
                        onClickImage = { index, id ->
                            componentId = id
                            indexPhotoSelected = index
                            viewModel.events.value = TicketProcessEvent.PhotoPreview

                        },
                        onFixChange = { text ->
                            changeState.update { Random.nextInt() }

                        },
                        onChanges = { listComponent, listValueDomain, listIndexParent, indexChild ->

                            viewModel.handleLogics()
                            viewModel.addOrRemoveComponentDomainRepeatableToList(
                                listComponent,
                                indexChild
                            )

                            listValueDomain?.let { it1 ->
                                viewModel.updateUriPhotoComponent(
                                    viewModel.tempComponentList, listIndexParent, indexChild,
                                    it1
                                )
                            }
                        },
                    )
                }
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

                backClick()
            })


    }


}

