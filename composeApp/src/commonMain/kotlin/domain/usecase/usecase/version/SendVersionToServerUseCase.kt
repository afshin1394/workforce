package domain.usecase.usecase.version


import data.network.request.version.VersionRequest
import domain.mappers.toVersionDomain
import domain.models.version.SendVersionDomain
import domain.repository.IVersionRepository
import domain.usecase.BaseUseCase

class SendVersionToServerUseCase(private val iVersionRepository: IVersionRepository) :
    BaseUseCase<SendVersionDomain, VersionRequest>() {
    override suspend fun run(params: VersionRequest): SendVersionDomain {

        val versionResponse = iVersionRepository.fetchSendVersion(params)

        return versionResponse.toVersionDomain()

    }


}