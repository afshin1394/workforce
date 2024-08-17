package presentation.screens.main.viewmodel

import domain.models.ProfileDomain
import domain.models.RoleDomain
import domain.models.UserDomain
import domain.usecase.usecase.profile.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class AccountScreenVM(
    private val getProfileUseCase: GetProfileUseCase
) : BaseViewModel() {
    init {
        getProfile()
    }

    private val _profileDomain = MutableStateFlow<ProfileDomain>(
        ProfileDomain(
            UserDomain("", "", ""),
            role = listOf(RoleDomain(0, "")),
            firstName = "",
            lastName = "",
            company = "",
            organization = "",
            nationalId = "",
            phoneNumber = ""
        )
    )
    val profileDomain = _profileDomain.asStateFlow()

    private val _roles = MutableStateFlow("")
    val roles = _roles.asStateFlow()

    private fun getProfile(){
        viewModelScope.launch {
            getProfileUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }
                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }
                    AsyncStatus.EMPTY->{
                    }
                    AsyncStatus.SUCCESS -> {
                        it.data?.let { profileDomain->
                            _profileDomain.update { profileDomain }
                            profileDomain.role?.map {roleDomain->
                               _roles.update { it.plus("${roleDomain.name},") }
                            }
                        }
                        updateState(ViewStates.Success())

                    }
                }
            }
        }
    }

}