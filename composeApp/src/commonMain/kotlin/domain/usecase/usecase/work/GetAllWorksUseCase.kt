package domain.usecase.usecase.work

import data.WorkRepositoryImpl
import domain.mappers.toTaskDomainList
import domain.models.WorkDomain
import domain.usecase.BaseUseCase

class GetAllWorksUseCase(
    private val workRepositoryImpl: WorkRepositoryImpl
) : BaseUseCase<List<WorkDomain>,Unit>() {
    override suspend fun run(params: Unit): List<WorkDomain> {
      return  workRepositoryImpl.fetchWorks().toTaskDomainList()
    }
}