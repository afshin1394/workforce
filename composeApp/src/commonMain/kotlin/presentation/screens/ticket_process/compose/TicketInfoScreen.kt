package presentation.screens.ticket_process.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSingleActionComponent
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import presentation.components.MenuItemsTopBar
import presentation.screens.main.compose.BaseScreen
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.SingleButtonActionModel
import presentation.screens.main.components.formViewer.SimpleEditable

import presentation.screens.ticket_process.viewModel.TicketInfoVM
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse


class TicketInfoScreen(
    private val ticketId: String,
    private val ticket_number: String
) : Screen {
    @OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
    @Composable
    override fun Content() {


        Napier.log(
            LogLevel.ASSERT,
            tag = "Infoooo",
            message = ticketId
        )

        Napier.log(
            LogLevel.ASSERT,
            tag = "Infoooo",
            message = ticket_number
        )

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow

        val ticketProcessScreen =
            rememberScreen(
                presentation.nav.Screen.TicketProcess.TicketProcessScreen(
                    ticketId,
                    ticket_number
                )
            )

        val viewModel: TicketInfoVM = koinInject()

        val viewState by viewModel.state.collectAsState()
        var isClickable by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            viewModel.getInitialForm(ticket_number)
            viewModel.updateTicketNumber(ticket_number)
            viewModel.updateTicketId(ticketId)
        }

        BaseScreen(
            viewModel = viewModel,
            title = stringResource(MR.strings.ticket_info),
            scaffoldState = scaffoldState,
            hasDrawer = false,
            bottomSheetHasHeader = false,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.ticket_info)) {
                    navigator.pop()
                }
            },
            bottomSheetTitle = "",

            bottomBarBottomSheetContent = {


            },
            bottomSheetContent = {
                bottomSingleActionComponent(
                    SingleButtonActionModel(
                        stringResource(MR.strings.resume),
                        surfaceBrandDefault,
                        textInverse
                    ), onClick = {
                        if (isClickable) {
                            isClickable = false
                            navigator.pop()
                            scope.launch {
                                delay(500)
                                isClickable = true
                            }
                        }
                    })

                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            },
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing15X),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(viewModel.initFormsState) { item ->

                        SimpleEditable(item.key, item.value)
                    }
                }

            },
            onCloseBottomSheet = {

            }, onBackPressed = {
                navigator.pop()
            }, shouldBlurOnBottomSheetExpansion = false
        )
    }
}



