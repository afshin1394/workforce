package domain.usecase.usecase.download

import data.network.response.download.StepsResponse
import domain.repository.IDownloadRepository
import domain.usecase.BaseUseCase

data class GetStepsParams(
    val ticketNumber: String
)

class GetStepsByTicketUseCase(
    private val downloadRepository: IDownloadRepository
) : BaseUseCase<StepsResponse, GetStepsParams>() {
    
    override suspend fun run(params: GetStepsParams): StepsResponse {
        return downloadRepository.getStepsByTicketNumber(params.ticketNumber)
    }
}