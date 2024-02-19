package presentation.screens.ticket_process.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import com.irancell.nwg.wfm.presentation.components.MenuItemsTopBar
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import presentation.model.BottomSheetDoubleActionModel
import presentation.screens.main.compose.BaseScreen
import presentation.screens.ticket_process.components.bottomDoubleActionSheet
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.theme.body_large_strong

class TicketInfoScreen(
    private val title: String
) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow
        val ticketProcessScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.TicketProcess.TicketProcessScreen)


        val viewModel: TicketProcessVM = koinInject()

            BaseScreen(
            viewModel =viewModel,
            title = title,
            scaffoldState = scaffoldState,
            hasDrawer = false,
            bottomSheetHasHeader = false,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.ticket_info)) {
                    navigator.pop()
                }
            },
            bottomSheetTitle = "",
            bottomSheetContent = {
                bottomDoubleActionSheet(
                    BottomSheetDoubleActionModel(
                        stringResource(MR.strings.more_options),
                        surfaceDefault,
                        textPrimary,
                        stringResource(MR.strings.resume),
                        surfaceBrandDefault,
                        textInverse
                    ), onFirstButtonClick = {

                    }, onSecondButtonClick = {
                        navigator.push(ticketProcessScreen)
                    })

                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }

            },
            content = {
                Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
                    TicketInfoItem(
                        stringResource(MR.strings.general_information),
                        false,
                        content = {
                            Box(modifier = Modifier.height(200.dp))
                        })
                    TicketInfoItem(
                        stringResource(MR.strings.resource_information),
                        false,
                        content = {
                            Box(modifier = Modifier.height(200.dp))
                        })
                    TicketInfoItem(stringResource(MR.strings.fault_information), false, content = {
                        Box(modifier = Modifier.height(2000.dp))
                    })
                }
            })
    }

}


@Composable
private fun TicketInfoItem(text: String, expanded: Boolean, content: @Composable () -> Unit) {
    var expandedState by remember {
        mutableStateOf(expanded)
    }

    Column {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing15X, horizontal = spacing2X)
                .clickable {
                    expandedState = !expandedState
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, style = body_large_strong, modifier = Modifier)
            Spacer(modifier = Modifier.padding(spacing2X))
            if (expandedState) {
                Image(
                    painter = painterResource(MR.images.chevron_up),
                    contentDescription = ""
                )
            } else {
                Image(
                    painter = painterResource(MR.images.chevron_down),
                    contentDescription = ""
                )
            }

        }
        AnimatedVisibility(
            visible = expandedState,
        ) {
            content()
        }
    }

}


