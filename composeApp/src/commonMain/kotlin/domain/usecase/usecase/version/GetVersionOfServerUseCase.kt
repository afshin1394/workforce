package domain.usecase.usecase.version

import domain.mappers.toGetVersionDomain
import domain.models.version.GetVersionDomain
import domain.repository.IVersionRepository
import domain.usecase.BaseUseCase

class GetVersionOfServerUseCase (private val iVersionRepository: IVersionRepository) :
    BaseUseCase<GetVersionDomain, Unit>() {
    override suspend fun run(params: Unit): GetVersionDomain {

        val versionResponse = iVersionRepository.fetchGetVersion()

        return versionResponse.toGetVersionDomain()

    }



}