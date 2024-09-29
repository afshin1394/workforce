package domain.models.ticket


data class TicketDetailResponseDomain(val events: Map<String, ObjectsDomain>)

data class ObjectsDomain(
    val attributes: Map<String, String?>
)
