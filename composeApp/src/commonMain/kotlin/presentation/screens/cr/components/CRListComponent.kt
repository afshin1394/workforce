package presentation.screens.cr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.components.CustomSearchBar
import com.irancell.nwg.wfm.presentation.components.FilterRow
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import domain.models.CRDomain
import domain.models.TaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import presentation.components.ticketCard
import presentation.model.StateFilter
import presentation.screens.main.events.MainEvent
import presentation.theme.backgroundBackground3
import utils.CRState
import utils.NotificationState
import utils.TaskState

@Composable
fun CRListScreen(
    crDomains: ArrayList<CRDomain>,
    searchText: String = "",
    onAccept : (item : CRDomain) -> Unit,
) {



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
            },hasFilter = false)
        FilterRow(

            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing2X),
            items = arrayListOf(
                StateFilter(CRState.All.id, stringResource(MR.strings.all), false),
                StateFilter(CRState.CLOSE_CR.id, stringResource(MR.strings.close_cr), false),
                StateFilter(CRState.OPEN_CR.id, stringResource(MR.strings.open_cr), false)),
            itemTitleSelected = stringResource(MR.strings.all)
        ) {

            selectState = if (it.id == CRState.All.id){
                ""
            }else{
                it.id.toString()
            }

        }
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = backgroundBackground3)
                .padding(horizontal = spacing2X)
        ) {
            val filteredList =  crDomains.filter {


                it.ticketInstanceId.toString().lowercase().contains(searchTextState.lowercase()) ||
                        it.workId.toString().contains(searchTextState.lowercase()) ||
                        it.activityId.toString().contains(searchTextState.lowercase()) ||
                        it.ticketTitle.contains(searchTextState.lowercase()) ||
                        it.ticketInstanceNumber.lowercase().contains(searchTextState.lowercase()) ||
                        it.ticketInstanceTitle.lowercase().contains(searchTextState.lowercase()
                        )
            }.filter {  it.activityTitle.contains(selectState.lowercase()) }
            Napier.log(LogLevel.ASSERT,"selectState" , message =  selectState)

            itemsIndexed(items = filteredList) { index: Int, item: CRDomain ->
                CRCard(modifier = Modifier.wrapContentHeight(),
                    crDomain = item,
                    onActionClick = {
                        Napier.log(LogLevel.ASSERT,"onActionClick", message = "onActionClick")
                        onAccept(item)
                    },
                )
            }
        }

    }
}