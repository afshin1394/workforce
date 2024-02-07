package utils

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.http.HttpMessage
import kotlinx.coroutines.flow.MutableStateFlow
sealed interface ViewStates {
    data object Default : ViewStates
    data object Loading : ViewStates
    data object Error : ViewStates
    data object Success : ViewStates
}
open class BaseViewModel : ViewModel() {
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow("")
    val state = MutableStateFlow<ViewStates>(ViewStates.Default)
}