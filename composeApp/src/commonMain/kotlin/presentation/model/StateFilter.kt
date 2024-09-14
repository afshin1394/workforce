package presentation.model

import androidx.compose.runtime.*

sealed class FilterType(type: String) {
    object DEFAULT : FilterType("Default")
    object SEVERITY_LEVEL : FilterType("Severity Level")
    object REGION : FilterType("Region")
    object STATE : FilterType("State")
}
data class StateFilter(
    val id: Int,
    val title: String = "Completed",
    var isActive: Boolean,
    var type: FilterType = FilterType.DEFAULT
) {
    var isActiveState by mutableStateOf(isActive)
    override fun toString(): String {
        return "StateFilter(id=$id, title='$title', isActive=$isActive, isActiveState=$isActiveState)"
    }
}



