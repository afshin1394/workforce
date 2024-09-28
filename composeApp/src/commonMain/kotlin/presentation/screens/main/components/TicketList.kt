package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

import com.irancell.nwg.wfm.presentation.components.CustomSearchBar
import com.irancell.nwg.wfm.presentation.components.FilterRow
import presentation.components.ticketCard
import presentation.screens.main.events.MainEvent
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import domain.models.task.TaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import presentation.screens.main.viewmodel.MainScreenVM
import utils.TaskState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TicketListScreen(
    tasks: ArrayList<TaskDomain>,
    searchText: String = "",
    onEvent: (mainEvents: MainEvent, selectedTask: TaskDomain?) -> Unit = { _: MainEvent, _: TaskDomain? -> },
    onAccept: (item: TaskDomain) -> Unit,
    viewModel: MainScreenVM
) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() =
        refreshScope.launch {
            refreshing = true
            viewModel.updateTask()
            refreshing = false
        }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

//    val context = LocalContext.current
    var searchTextState by remember {
        mutableStateOf(searchText)
    }
    var selectState by remember {
        mutableStateOf("")
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(color = backgroundBackground3)
    ) {
        Spacer(modifier = Modifier.padding(top = spacing15X, bottom = spacing15X))
        CustomSearchBar(
            modifier = Modifier
                .padding(horizontal = spacing2X)
                .fillMaxWidth(),
            textFieldState = searchTextState,
            updatedText = {
                searchTextState = it
            }, onFilterClick = {
                onEvent(MainEvent.ActionFilter, null)
            })
        FilterRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing2X),
            itemTitleSelected = stringResource(MR.strings.all)
        ) {

            selectState = if (it.id == TaskState.All.id) {
                ""
            } else {
                it.id.toString()
            }
        }
        Box(Modifier.pullRefresh(refreshState)) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = backgroundBackground3)
                    .padding(horizontal = spacing2X)
            ) {
                val filteredList = tasks.filter {
                    it.basic_info.ticket_number?.lowercase()
                        ?.contains(searchTextState.lowercase()) == true ||
                            it.basic_info.ticket_state?.lowercase()
                                ?.contains(searchTextState.lowercase()) == true ||
                            it.basic_info.location?.lowercase()
                                ?.contains(searchTextState.lowercase()) == true ||
                            it.basic_info.city?.lowercase()
                                ?.contains(searchTextState.lowercase()) == true ||
                            it.basic_info.province?.lowercase()
                                ?.contains(searchTextState.lowercase()) == true ||
                            it.basic_info.site?.lowercase()
                                ?.contains(searchTextState.lowercase()) == true ||
                            it.basic_info.region?.lowercase()
                                ?.contains(searchTextState.lowercase()) == true

                }.filter {
                    it.basic_info.instanceStateId.toString().contains(selectState.lowercase())
                }
                Napier.log(LogLevel.ASSERT, "selectState", message = selectState)
                if (!refreshing) {
                    Napier.log(LogLevel.ASSERT, "refreshing", message = refreshing.toString())
                    itemsIndexed(items = filteredList) { _: Int, item: TaskDomain ->
                        ticketCard(modifier = Modifier.wrapContentHeight(),
                            task = item,
                            onActionClick = {
                                Napier.log(
                                    LogLevel.ASSERT,
                                    "onActionClick",
                                    message = "onActionClick"
                                )
                                onAccept(item)
                                onEvent(MainEvent.AcceptTicket, item)
                            },
                            onMoreOptionsClick = {
                                onEvent(MainEvent.MoreOptions, item)
                            }
                        )
                    }
                }
            }
            PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}