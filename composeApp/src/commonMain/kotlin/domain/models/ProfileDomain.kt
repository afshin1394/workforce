package domain.models


data class ProfileDomain(
    val user: UserDomain?,
    val role : List<RoleDomain>?,
    val firstName : String?,
    val lastName : String?,
    val company : String?,
    val organization : String?,
    val nationalId : String?,
    val phoneNumber : String?,
)
