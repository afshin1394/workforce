package domain.repository

import data.network.response.profile.ProfileNetworkResponse
import irancell.nwg.wfm.db.ProfileEntity
import irancell.nwg.wfm.db.RoleEntity

interface IProfileRepository {
    suspend fun fetch() : ProfileNetworkResponse
    suspend fun insertProfile(profileEntity: ProfileEntity)
    suspend fun insertRoles(roleEntity : List<RoleEntity>)
    suspend fun getRoles() : List<RoleEntity>
    suspend fun getProfile() : ProfileEntity
    suspend fun deleteAllRoles()
    suspend fun deleteAllProfile()

}