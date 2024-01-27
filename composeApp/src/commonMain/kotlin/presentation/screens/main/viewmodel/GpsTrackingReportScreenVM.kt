package presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import domain.usecase.usecase.GetGeneralLocationListUseCase
import irancell.nwg.wfm.db.GeneralLocation
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates


class GpsTrackingReportScreenVM(
   private val  generalLocationListUseCase: GetGeneralLocationListUseCase
) : BaseViewModel()  {
     val generalLocationList = mutableStateListOf<GeneralLocation>()


    init {
        getGeneralLocationList()
    }

    private fun getGeneralLocationList() {

        viewModelScope.launch {
            generalLocationListUseCase(
                Unit,
            ).collect {
                when(it.status){
                    AsyncStatus.ERROR -> {
                        state.update { ViewStates.Loading }
                        val errorMessage = it.message!!
                        error.update { errorMessage }
                    }
                    AsyncStatus.LOADING -> {
                    }
                    AsyncStatus.SUCCESS -> {
                        it.data?.let { locations -> generalLocationList.addAll(locations) }
                        state.update{  ViewStates.Success  }
                    }
                }
            }
        }
    }




}