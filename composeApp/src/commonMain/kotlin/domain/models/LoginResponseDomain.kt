package domain.models

data class LoginResponseDomain( val session_id: String,val phone_number: String?){
    override fun toString(): String {
        return "LoginResponseDomain(session_id='$session_id', phone_number=$phone_number)"
    }
}
