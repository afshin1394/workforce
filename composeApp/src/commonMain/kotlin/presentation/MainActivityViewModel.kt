//package com.irancell.nwg.wfm.presentation
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.irancell.nwg.wfm.presentation.model.Task
//import com.irancell.nwg.wfm.presentation.model.StateFilter
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.stateIn
//class MainActivityViewModel() : ViewModel() {
//
//
//
//    val filterCards = flow<List<StateFilter>> {
//
//    }.stateIn(
//        viewModelScope, SharingStarted.WhileSubscribed(5000L), listOf(
//            StateFilter(1, "Pending", false),
//            StateFilter(2, "Doing", false),
//            StateFilter(3, "Done", false)
//        )
//    )
//}
//
//fun <T> repeat(times: Int, create: () -> T): List<T> {
//    val list = arrayListOf<T>()
//    for (i in 0..times)
//        list.add(create())
//    return list
//}