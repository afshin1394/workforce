package domain.usecase.usecase.cr

import domain.mappers.toCRDomainList
import domain.models.CRDomain
import domain.repository.ICRRepository
import domain.usecase.BaseUseCase
import kotlinx.coroutines.delay

class GetAllCR(
    private val icrRepository: ICRRepository
) : BaseUseCase<List<CRDomain>,Unit >() {


    override suspend fun run(params: Unit): List<CRDomain> {
        delay(2000)
       return icrRepository.fetchAllCR().toCRDomainList()
    }
}