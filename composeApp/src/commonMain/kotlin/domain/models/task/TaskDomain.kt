package domain.models.task

import domain.models.form_struct.InitialFormDomain



data class TaskDomain(
    val ticket_id:Int,
    val ticket_type_id:Int,
    val instancePrefix :String,
    val ticket_number: String,
    val ticket_state: String,
    val activity_id: Long,
    val activity__title: String,
    val instanceStateId: Int?,
    val properties: List<PropertiesDomain>
){
    override fun toString(): String {
        return "$instancePrefix"
    }
}
