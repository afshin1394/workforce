package domain.models

data class VerifyResponseDomain(val auth_token : String){
    override fun toString(): String {
        return "VerifyResponseDomain(auth_token='$auth_token')"
    }
}