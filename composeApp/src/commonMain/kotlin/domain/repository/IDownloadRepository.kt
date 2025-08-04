package domain.repository

import data.network.response.download.ChunkResponse
import data.network.response.download.StartDownloadResponse
import data.network.response.download.StepsResponse

interface IDownloadRepository {
    suspend fun startDownload(): StartDownloadResponse
    suspend fun getChunk(jobId: String, chunkIndex: Int): ChunkResponse
    suspend fun getStepsByTicketNumber(ticketNumber: String): StepsResponse
}