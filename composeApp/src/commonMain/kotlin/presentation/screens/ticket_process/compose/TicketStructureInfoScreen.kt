package presentation.screens.ticket_process.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.CustomSearchBar
import presentation.components.TicketInfoTopBar
import presentation.screens.main.components.formViewer.SimpleEditable
import presentation.screens.main.compose.BaseScreen

import presentation.screens.ticket_process.viewModel.TicketStructureInfoVM
import utils.FormViewerTypes
import utils.LogicType
import utils.initialize
import utils.ticketInfoInitialize
import utils.validateComponent

class TicketStructureInfoScreen(
    private val ticketId: String,
    private val ticket_number: String
) : Screen {


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow

        val viewModel: TicketStructureInfoVM = koinInject()
        val viewState by viewModel.state.collectAsState()





        LaunchedEffect(Unit) {
            viewModel.getInitialStructureForm(ticket_number)
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
                TicketInfoTopBar(ticket_number) {
                    navigator.pop()
                }
            },
            bottomSheetTitle = "",
            bottomBarBottomSheetContent = {},
            bottomSheetContent = {

            },
            content = {

                Column(modifier = Modifier.fillMaxSize()) {



                    ticketInfoInitialize(

                            modifier = Modifier,
                            components = viewModel.tempInitComponentList,



                        )
                    }


            },
            onCloseBottomSheet = {},
            onBackPressed = {
                navigator.pop()
            },
            shouldBlurOnBottomSheetExpansion = false
        )
    }
}




