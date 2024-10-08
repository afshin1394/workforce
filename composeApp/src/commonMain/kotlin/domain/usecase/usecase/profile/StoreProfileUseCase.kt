package domain.usecase.usecase.profile

import domain.mappers.toProfileDomain
import domain.mappers.toProfileEntity
import domain.mappers.toRoleEntityList
import domain.models.ProfileDomain
import domain.repository.IProfileRepository
import domain.usecase.BaseUseCase

class StoreProfileUseCase(
    private val iProfileRepository: IProfileRepository
) : BaseUseCase<ProfileDomain, Unit>() {
    override suspend fun run(params: Unit): ProfileDomain {
        val profileNetworkResponse = iProfileRepository.fetch()
        iProfileRepository.deleteAllRoles()
        iProfileRepository.deleteAllProfile()
        iProfileRepository.insertProfile(profileNetworkResponse.toProfileEntity())
        profileNetworkResponse.user?.pk?.let {
            profileNetworkResponse.role?.toRoleEntityList(it)
                ?.let { it1 -> iProfileRepository.insertRoles(it1) }
        }
        return iProfileRepository.getProfile().toProfileDomain(iProfileRepository.getRoles())
    }
}