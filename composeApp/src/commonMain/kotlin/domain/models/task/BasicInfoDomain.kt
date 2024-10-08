package domain.models.task

data class BasicInfoDomain(
    val ticket_id: String?,
    val ticket_number: String?,
    val ticket_state: String?,
    val level: String?,
    val location: String?,
    val site: String?,
    val region: String?,
    val province: String?,
    val city: String?,
    val instanceStateId: Int?,
    val pk: Long
)
