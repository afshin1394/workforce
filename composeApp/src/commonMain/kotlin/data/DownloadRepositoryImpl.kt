package data

import data.network.BaseRepository
import data.network.NetworkResult
import data.network.addAuthHeader
import data.network.addStandardHeaders
import data.network.response.download.ChunkResponse
import data.network.response.download.StartDownloadResponse
import data.network.response.download.StepsResponse
import domain.repository.IDownloadRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.url
import utils.LoggingConfig

class DownloadRepositoryImpl(
    private val httpClient: HttpClient
) : BaseRepository(), IDownloadRepository {

    override suspend fun startDownload(): StartDownloadResponse {
        LoggingConfig.logNetwork("startDownload", "Starting download process")
        
        return when (val result = httpClient.postWithResult<StartDownloadResponse>("workforce_management/user/v5/tasks/start-download") {
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                LoggingConfig.logNetwork("startDownload", "Download started with job ID: ${result.data.jobId}")
                result.data
            }
            is NetworkResult.Error -> {
                Napier.e("Failed to start download: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }

    override suspend fun getChunk(jobId: String, chunkIndex: Int): ChunkResponse {
        LoggingConfig.logNetwork("getChunk", "Getting chunk $chunkIndex for job $jobId")
        
        return when (val result = httpClient.getWithResult<ChunkResponse>("workforce_management/user/v5/tasks/get-chunk/$jobId/$chunkIndex") {
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                val response = result.data
                // Log response details based on status
                when {
                    response.isPending() -> {
                        LoggingConfig.logNetwork("getChunk", "Chunk $chunkIndex is PENDING - Status: ${response.status}, Message: ${response.message}")
                    }
                    response.hasError() -> {
                        LoggingConfig.logNetwork("getChunk", "Chunk $chunkIndex has ERROR - Error: ${response.error}")
                    }
                    response.isReady() -> {
                        LoggingConfig.logNetwork("getChunk", "Chunk $chunkIndex is READY - JobId: ${response.jobId}, Tasks: ${response.data.size}, PartId: ${response.partId}, TotalParts: ${response.totalParts}")
                    }
                    else -> {
                        LoggingConfig.logNetwork("getChunk", "Chunk $chunkIndex has unknown status - JobId: ${response.jobId ?: "null"}, Tasks: ${response.data.size}, Status: ${response.status}")
                    }
                }
                response
            }
            is NetworkResult.Error -> {
                Napier.e("Failed to get chunk $chunkIndex: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }

    override suspend fun getStepsByTicketNumber(ticketNumber: String): StepsResponse {
        LoggingConfig.logNetwork("getStepsByTicketNumber", "Getting steps for ticket: $ticketNumber")
        
        return when (val result = httpClient.getWithResult<StepsResponse>("workforce_management/user/v1/steps/?ticket_number=$ticketNumber") {
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                LoggingConfig.logNetwork("getStepsByTicketNumber", "Got ${result.data.steps.size} steps for ticket $ticketNumber")
                result.data
            }
            is NetworkResult.Error -> {
                Napier.e("Failed to get steps for ticket $ticketNumber: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}