package data.network.response.download

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartDownloadResponse(
    @SerialName("job_id")
    val jobId: String,
    @SerialName("status") 
    val status: String = "PENDING"
)