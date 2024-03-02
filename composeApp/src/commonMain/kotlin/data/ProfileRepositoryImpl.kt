package data

import data.network.response.profile.ProfileNetworkResponse
import domain.repository.IProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import irancell.nwg.wfm.db.ProfileEntity
import irancell.nwg.wfm.db.RoleEntity
import irancell.nwg.wfm.db.WFMDatabase

class ProfileRepositoryImpl(
    private val httpClient: HttpClient,
    private val wfmDatabase: WFMDatabase
) : IProfileRepository {
    override suspend fun fetch(): ProfileNetworkResponse {
        return httpClient.get("profile/").body<ProfileNetworkResponse>()
    }

    override suspend fun insertProfile(profileEntity: ProfileEntity) {

        wfmDatabase.profileEntityQueries.insert(
            profileEntity.pk,
            profileEntity.username,
            profileEntity.email,
            profileEntity.first_name,
            profileEntity.last_name,
            profileEntity.company,
            profileEntity.organization,
            profileEntity.national_id,
            profileEntity.phone_number
        )
    }

    override suspend fun insertRoles(roleEntities: List<RoleEntity>) {
        roleEntities.map {
            wfmDatabase.roleEntityQueries.insert(it.pk, it.code, it.name)

        }
    }

    override suspend fun getRoles(): List<RoleEntity> {
       return wfmDatabase.roleEntityQueries.selectAll().executeAsList()
    }

    override suspend fun getProfile(): ProfileEntity {
       return wfmDatabase.profileEntityQueries.selectAll().executeAsOne()
    }

    override suspend fun deleteAllRoles() {
        wfmDatabase.roleEntityQueries.deleteAll()
    }

    override suspend fun deleteAllProfile() {
        wfmDatabase.profileEntityQueries.deleteAll()
    }
}