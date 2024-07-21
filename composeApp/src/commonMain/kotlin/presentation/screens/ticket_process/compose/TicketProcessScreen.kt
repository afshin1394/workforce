package presentation.screens.ticket_process.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.model.ProcessLevel
import dev.icerock.moko.resources.StringResource
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import presentation.screens.ticket_process.components.processBar
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.SingleButtonActionModel
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.compose.SplashScreen
import presentation.screens.ticket_process.components.bottomSingleActionComponent
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse


class TicketProcessScreen(
    private val ticketNumber : String
) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel : TicketProcessVM = koinInject()

        val currentLevelState = viewModel.currentLevel.collectAsState()
        val listOfSteps = viewModel.listSample.toMutableList()
        val formViewerScreen = rememberScreen(presentation.nav.Screen.Main.Menu.FormViewer)


        BaseScreen(
            viewModel = viewModel,
            title = stringResource(MR.strings.ticket_process),
            scaffoldState = scaffoldState,
            hasDrawer = false,
            bottomSheetHasHeader = false,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.ticket_process)) {

                    navigator.pop()

                }
            },
            bottomSheetTitle = "",
            bottomSheetContent = {

                bottomSingleActionComponent(
                    SingleButtonActionModel(
                        stringResource(MR.strings.resume),
                        surfaceBrandDefault,
                        textInverse
                    ), onClick = {



                        if (currentLevelState.value < listOfSteps.size-1) {
                            viewModel.updateLevel(currentLevelState.value + 1 )

                            listOfSteps.map {
                                it.isActive = false
                            }
                            listOfSteps[currentLevelState.value].isActive = true
                        }

                    })



                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }

            },
            content = {

                Column() {
                    processBar(listOfSteps,currentLevelState.value)
                    formViewerScreen.Content()
//                    navigator.push(formViewerScreen)
//                    formView(listOfSteps[currentLevelState.value])
                }
            }, onBackPressed = {
                navigator.pop()
            })

    }
    @Composable
    fun formView(level: ProcessLevel) {
        Row( modifier = Modifier.fillMaxSize(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(level.levelName)
        }
    }

}

