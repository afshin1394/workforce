package data

import data.network.response.profile.ProfileNetworkResponse
import database.AppDatabase
import database.entity.ProfileEntity
import database.entity.RoleEntity
import domain.repository.IProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get



class ProfileRepositoryImpl(
    private val httpClient: HttpClient,
    private val db: AppDatabase

) : IProfileRepository {
    override suspend fun fetch(): ProfileNetworkResponse {
        return httpClient.get("profile/").body<ProfileNetworkResponse>()
    }

    override suspend fun insertProfile(profileEntity:ProfileEntity ) {

        db.profileDao().insert(profileEntity)

    }

    override suspend fun insertRoles(roleEntities: List<RoleEntity>) {
        db.roleDao().insert(roleEntities)
    }

    override suspend fun getRoles(): List<RoleEntity> {

        return db.roleDao().getAllRoles()
    }

    override suspend fun getProfile(): ProfileEntity {

        return db.profileDao().getAllProfiles()
    }

    override suspend fun deleteAllRoles() {
        db.roleDao().deleteAllRoles()


    }

    override suspend fun deleteAllProfile() {
        db.profileDao().deleteAllProfiles()
    }
}