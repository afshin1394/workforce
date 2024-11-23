package domain.usecase.usecase.auth

import data.network.request.ChangeAvailabilityRequest
import domain.repository.IAuthRepository
import domain.repository.IAvailabilityRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext
import utils.Availability
import utils.AvailabilityObjectId
import utils.Language
import utils.UpdateTaskListTypes
import utils.UpdateType


class LogoutUseCase(
    private val iAuthRepository: IAuthRepository,
    private val iAvailabilityRepository: IAvailabilityRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        Napier.log(LogLevel.ASSERT, "Logout", message = "")
        val available = getSharedPref().getBool(Availability, false)
        if (available) {
            try {
                iAvailabilityRepository.changeAvailability(
                    ChangeAvailabilityRequest(
                        false,
                        getSharedPref().getString(AvailabilityObjectId)?.toInt()
                    )
                )
                iAuthRepository.logout()
            }catch (exception : Exception){

            }
            BackgroundServiceApp.stopBackgroundService()
            val language= getSharedPref().getString(Language)
            getSharedPref().deleteAll()
            getSharedPref().put(Language,language?:"en")
            iAuthRepository.deleteAllTableDB()
            Napier.log(LogLevel.ASSERT, "Logout", message = "Availability")
            InternalStorage.clearCache(provideAppContext())
            Napier.log(LogLevel.ASSERT, "Logout", message = "App data folders deleted")


        } else {
            try {
                iAuthRepository.logout()
            }catch (exception : Exception){

            }
            val language= getSharedPref().getString(Language)
            val typeUpdate= getSharedPref().getString(UpdateType)
            getSharedPref().deleteAll()
            getSharedPref().put(Language,language?:"en")
            getSharedPref().put(UpdateType,typeUpdate?:UpdateTaskListTypes.Auto)
            iAuthRepository.deleteAllTableDB()
            InternalStorage.clearCache(provideAppContext())
            Napier.log(LogLevel.ASSERT, "Logout", message = "App data folders deleted")
            Napier.log(LogLevel.ASSERT, "Logout", message = "pairLogout")
        }
    }
}