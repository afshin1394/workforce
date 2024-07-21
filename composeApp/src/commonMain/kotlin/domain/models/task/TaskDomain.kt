package domain.models.task

import domain.models.initialForm.InitialFormDomain

data class TaskDomain(
    val basic_info: BasicInfoDomain,
    val initial_form: InitialFormDomain? = null,
){
    override fun toString(): String {
        return "TaskDomain(basic_info=$basic_info, initial_form=$initial_form)"
    }
}
