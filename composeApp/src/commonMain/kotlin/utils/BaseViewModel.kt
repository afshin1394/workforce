package utils

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
sealed interface ViewStates {
    data object Loading : ViewStates
    data object Error : ViewStates
    data object Success : ViewStates
}
open class BaseViewModel : ViewModel() {
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow("")
    val state = MutableStateFlow<ViewStates>(ViewStates.Loading)
}