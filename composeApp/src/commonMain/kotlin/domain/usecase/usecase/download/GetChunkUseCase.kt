package domain.usecase.usecase.download

import data.network.response.download.ChunkResponse
import domain.repository.IDownloadRepository
import domain.usecase.BaseUseCase

data class GetChunkParams(
    val jobId: String,
    val chunkIndex: Int
)

class GetChunkUseCase(
    private val downloadRepository: IDownloadRepository
) : BaseUseCase<ChunkResponse, GetChunkParams>() {
    
    override suspend fun run(params: GetChunkParams): ChunkResponse {
        return downloadRepository.getChunk(params.jobId, params.chunkIndex)
    }
}