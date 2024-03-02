package domain.usecase.usecase.profile

import domain.mappers.toProfileDomain
import domain.mappers.toRoleDomain
import domain.models.ProfileDomain
import domain.repository.IProfileRepository
import domain.usecase.BaseUseCase

class GetProfileUseCase(
  private val  iProfileRepository: IProfileRepository
) : BaseUseCase<ProfileDomain,Unit>() {


    override suspend fun run(params: Unit): ProfileDomain {
       return iProfileRepository.getProfile().toProfileDomain(iProfileRepository.getRoles())
    }
}