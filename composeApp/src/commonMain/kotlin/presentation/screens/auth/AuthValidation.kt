package presentation.screens.auth

sealed interface AuthValidation {
    data object Init : AuthValidation
    data object NoPassword : AuthValidation
    data object NotEnoughChar : AuthValidation
    data object NoEmail : AuthValidation
}