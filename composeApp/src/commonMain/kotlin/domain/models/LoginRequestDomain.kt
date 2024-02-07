package domain.models

data class LoginRequestDomain(val username : String,val password : String){
    override fun toString(): String {
        return "LoginRequestDomain(username='$username', password='$password')"
    }
}

