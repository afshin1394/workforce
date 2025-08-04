package domain.usecase.usecase.download

import data.network.response.download.StartDownloadResponse
import domain.repository.IDownloadRepository
import domain.usecase.BaseUseCase

class StartDownloadUseCase(
    private val downloadRepository: IDownloadRepository
) : BaseUseCase<StartDownloadResponse, Unit>() {
    
    override suspend fun run(params: Unit): StartDownloadResponse {
        return downloadRepository.startDownload()
    }
}