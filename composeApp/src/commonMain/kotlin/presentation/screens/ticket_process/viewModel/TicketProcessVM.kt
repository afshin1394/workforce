package presentation.screens.ticket_process.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import com.irancell.nwg.wfm.presentation.model.ProcessLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import utils.BaseViewModel

class TicketProcessVM : BaseViewModel() {
    private val _currentLevel = MutableStateFlow(0)
    val currentLevel = _currentLevel.asStateFlow()




    val listSample = mutableStateListOf( ProcessLevel("242340=-23043-2=", level = 1, levelName = "HSE check 1", isActive = false),
        ProcessLevel("9423492=394=249", level = 2, levelName = "Routing", isActive = false),
        ProcessLevel("iewjckplpwelde", level = 3, levelName = "HSE check 2", isActive = false),
        ProcessLevel(
            "gkewj-ri-0wdsair0-3i0",
            level = 4,
            levelName = "Job report",
            isActive = false
        ),
        ProcessLevel(
            "gkewj-ri-frg0wir0-3i0",
            level = 5,
            levelName = "finalize",
            isActive = false
        ),)
    init {
        listSample.map {
            if (_currentLevel.value == it.level)
                it.isActive = true
        }
    }
    fun updateLevel(level : Int){
        _currentLevel.update { level }
    }




}