package presentation.screens.main.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import presentation.components.CustomSearchBar
import com.irancell.nwg.wfm.presentation.components.FilterRow
import presentation.components.ticketCard
import presentation.screens.main.events.MainEvent
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import domain.mappers.toStateFilterList
import domain.models.task.ActivityListDomain
import domain.models.task.TaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.launch
import presentation.model.StateFilter
import presentation.screens.main.viewmodel.MainScreenVM
import utils.TaskState
import utils.UpdateTaskListTypes
import utils.UpdateType

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TicketListScreen(
    tasks: List<TaskDomain>,
    tasksActivityListFilter: List<ActivityListDomain>,
    searchText: String = "",
    onEvent: (mainEvents: MainEvent, selectedTask: TaskDomain?) -> Unit = { _: MainEvent, _: TaskDomain? -> },
    onAccept: (item: TaskDomain) -> Unit,
    viewModel: MainScreenVM
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }




    fun refresh() =
        refreshScope.launch {
            println("onStartCommand: CallApi  UpdateUseCase       ${"PullRefresh"}")
            refreshing = true
            viewModel.refreshTasks()
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



    val dynamicFilterList = tasksActivityListFilter.toStateFilterList()

    val finalFilterList = buildList {
        add(
            StateFilter(
                id = TaskState.All.id,
                title = stringResource(MR.strings.all),
                isActive = true
            )
        )
        addAll(dynamicFilterList)
    }

    var selectedFilterTitle by remember {
        mutableStateOf(finalFilterList.firstOrNull()?.title ?: "")
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
            updatedText = { newText ->
                searchTextState = newText
                if (newText.isBlank()) {
                    viewModel.clearSearch()
                } else if (newText.length >= 2) { // Search after typing at least 2 characters
                    viewModel.searchTasks(newText)
                }
            }, onFilterClick = {
                onEvent(MainEvent.ActionFilter, null)
            })


        FilterRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing2X),
            itemTitleSelected = selectedFilterTitle
            ,
            items = finalFilterList
        ) {
            selectedFilterTitle = it.title
            selectState = if (it.id == TaskState.All.id) {
                ""
            } else {
                it.title
            }
        }
       Box(modifier = if (getSharedPref().getString(UpdateType)==UpdateTaskListTypes.Manual) Modifier.pullRefresh(refreshState) else Modifier) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = backgroundBackground3)
                    .padding(horizontal = spacing2X)
            ) {
                val filteredList = tasks.filter { task ->


                    task.properties.any { property ->
                        property.key.lowercase().contains(searchTextState.lowercase()) ||
                                property.value.lowercase().contains(searchTextState.lowercase())||
                                task.ticket_number.contains(searchTextState)
                    }
                }.filter {
                    println("filterChip   ${it.activity__title.lowercase()}   ${selectState.lowercase()}")
                    it.activity__title.lowercase().contains(selectState.lowercase())
                }
                Napier.log(LogLevel.ASSERT, "selectState", message = selectState)
                if (!refreshing) {
                    val keySet = mutableSetOf<Any>()

                    Napier.log(LogLevel.ASSERT, "refreshing", message = refreshing.toString())
                    itemsIndexed(items = filteredList
                    ) { index: Int, item: TaskDomain ->
                        ticketCard(modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(top = spacing15X),
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
                        
                        // Load more items when approaching the end  
                        if (index >= filteredList.size - 2 && index == filteredList.lastIndex) {
                            LaunchedEffect(index) {
                                val hasNextPage = viewModel.hasNextPage.value
                                val isLoadingMore = viewModel.isLoadingMore.value
                                if (!isLoadingMore && hasNextPage) {
                                    viewModel.loadNextPage()
                                }
                            }
                        }
                    }
                    
                    // Loading indicator for pagination inside LazyColumn  
                    item {
                        val isLoadingMore by viewModel.isLoadingMore.collectAsState()
                        if (isLoadingMore) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
           if (getSharedPref().getString(UpdateType)==UpdateTaskListTypes.Manual)
            PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
       }
    }
}