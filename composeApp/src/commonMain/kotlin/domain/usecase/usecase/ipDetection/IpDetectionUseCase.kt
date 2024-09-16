package domain.usecase.usecase.ipDetection

import data.AuthRepositoryImpl
import domain.mappers.toGetVersionDomain
import domain.mappers.toLoginNetworkRequest
import domain.mappers.toLoginResponseDomain
import domain.models.LoginRequestDomain
import domain.models.version.GetVersionDomain
import domain.repository.IAuthRepository
import domain.repository.IIpDetectionRepository
import domain.repository.IVersionRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.getSharedPref
import utils.Password
import utils.PhoneNumber
import utils.SessionId
import utils.UserName

class IpDetectionUseCase(private val iIpDetectionRepository: IIpDetectionRepository) :
    BaseUseCase<String, Unit>() {
    override suspend fun run(params: Unit): String {

        val detectedIpResponse = iIpDetectionRepository.detectIp()

        return detectedIpResponse.country

    }


}