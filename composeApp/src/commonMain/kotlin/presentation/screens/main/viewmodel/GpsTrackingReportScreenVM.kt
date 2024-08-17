package presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import database.entity.GeneralLocationEntity
import domain.usecase.usecase.location.GetGeneralLocationListUseCase

import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates


class GpsTrackingReportScreenVM(
   private val  generalLocationListUseCase: GetGeneralLocationListUseCase
) : BaseViewModel()  {
     val generalLocationList = mutableStateListOf<GeneralLocationEntity>()


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
                        updateState(ViewStates.Loading)
                    }
                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)

                    }
                    AsyncStatus.EMPTY->{

                    }
                    AsyncStatus.SUCCESS -> {
                        it.data?.let { locations -> generalLocationList.addAll(locations) }
                        updateState(ViewStates.Success())
                    }
                }
            }
        }
    }




}