package domain.mappers

import data.network.response.profile.ProfileNetworkResponse
import data.network.response.profile.Role
import data.network.response.profile.User
import database.entity.ProfileEntity
import database.entity.RoleEntity
import domain.models.ProfileDomain
import domain.models.RoleDomain
import domain.models.UserDomain


fun RoleEntity.toRoleDomain() : RoleDomain{
    return RoleDomain(
        this.code.toInt(),
        this.name
    )
}


fun  List<RoleEntity>.toRoleDomain() : List<RoleDomain>{
    return map{
        RoleDomain(
            it.code.toInt(),
            it.name
        )
    }
}

fun ProfileEntity.toProfileDomain(roles : List<RoleEntity>) : ProfileDomain {

    return ProfileDomain(
         user = UserDomain(this.pk,this.username,this.email),
         role = roles.toRoleDomain(),
        firstName = first_name,
        lastName= last_name,
        company = company,
        organization= organization,
        nationalId = national_id,
        phoneNumber = phone_number,
    )
}

fun ProfileNetworkResponse.toProfileEntity() : ProfileEntity {

    return ProfileEntity(
        pk = this.user?.pk.toString(),
        username = this.user?.username.toString(),
        email = this.user?.email.toString(),
        first_name = this.firstName.toString(),
        last_name = this.lastName.toString(),
        company = this.company.toString(),
        organization = this.organization.toString(),
        national_id = this.nationalId.toString(),
        phone_number = this.phoneNumber.toString(),
    )
}

fun List<Role>.toRoleEntityList(pk : String) : List<RoleEntity> {

    return map {
        RoleEntity(
            profilePk = pk,
           code =  it.code?.toLong() ?: 0,
           name =  it.name.toString()
        )
    }
}