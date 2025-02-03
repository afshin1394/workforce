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
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import presentation.components.bottomSingleActionComponent
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import presentation.screens.main.compose.BaseScreen
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.CustomSearchBar
import presentation.components.TicketInfoTopBar
import presentation.model.SingleButtonActionModel
import presentation.screens.main.components.formViewer.SimpleEditable
import presentation.screens.main.events.MainEvent

import presentation.screens.ticket_process.viewModel.TicketInfoVM
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse


class TicketInfoScreen(
    private val ticketId: String,
    private val ticket_number: String
) : Screen {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow

        val viewModel: TicketInfoVM = koinInject()
        val viewState by viewModel.state.collectAsState()

        val initFormState = viewModel.initFormsState

        var searchQuery by remember { mutableStateOf("") }

        val lazyListState = rememberLazyListState()
        LaunchedEffect(searchQuery) {
            if (searchQuery.isEmpty()) {

                if (initFormState.isNotEmpty()) {
                    lazyListState.scrollToItem(0)
                }
            }
        }

        val filteredItems = initFormState.filter { item ->
            item.key.contains(searchQuery, ignoreCase = true) ||
                    item.value.contains(searchQuery, ignoreCase = true)
        }

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

                    Spacer(modifier = Modifier.padding(10.dp))
                    CustomSearchBar(
                        modifier = Modifier
                            .padding(horizontal = spacing2X)
                            .fillMaxWidth(),
                        textFieldState = searchQuery,
                        hasFilter = false,
                        updatedText = {
                            searchQuery = it
                        }, onFilterClick = {

                        })

                    LazyColumn(
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(spacing15X),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(
                            items = filteredItems,
                            key = { item -> item.key }
                        ) { item ->
                            SimpleEditable(item.key, item.value)
                        }
                    }
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



