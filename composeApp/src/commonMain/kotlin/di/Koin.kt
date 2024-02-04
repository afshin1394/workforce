package di

import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import data.AuthRepositoryImpl
import data.AvailabilityRepositoryImpl
import data.GeneralLocationRepositoryImpl
import data.SuspendTaskRepositoryImpl
import domain.usecase.usecase.auth.LoginUseCase
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityObjectIdUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.location.GetGeneralLocationListUseCase
import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.availability.StoreAvailabilityUseCase
import domain.usecase.usecase.location.StoreLocationDataUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType

import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import irancell.nwg.wfm.DatabaseDriverFactory
import irancell.nwg.wfm.db.WFMDatabase
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.viewModelDefinition
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import presentation.screens.auth.viewmodel.LoginScreenVM
import presentation.screens.main.viewmodel.AboutScreenVM
import presentation.screens.main.viewmodel.GpsTrackingReportScreenVM
import utils.Token


fun repositoryModule() = module {
    //Repositories
    single { WFMDatabase(DatabaseDriverFactory.createDriver())}
    single { GeneralLocationRepositoryImpl(get(),get()) }
    single { AvailabilityRepositoryImpl(get()) }
    single { SuspendTaskRepositoryImpl(get(),get())}
    single { AuthRepositoryImpl(get()) }
}

fun useCaseModule() = module {
    //UseCases
    single { GetGeneralLocationListUseCase(get()) }
    single { GetAvailabilityUseCase() }
    single { StoreAvailabilityUseCase() }
    single { SendLocationToServerUseCase(get()) }
    single { StoreLocationDataUseCase(get()) }
    single { ChangeServerAvailabilityUseCase(get()) }
    single { GetAvailabilityObjectIdUseCase() }
    single { LoginUseCase(get()) }
}

fun httpModule() = module {
    single {
        HttpClient {

            headers {
                append("Authorization", "Token fSCA7FgmafWcLI4Fmb989rV6VugNkmT8I9DzT0oYEQ3TwmswRQEbVXUDlqdLyrsjkPHObYFVSpBohubRWz0CLg")
                append("Content-Type", "application/json")
                append("accept","application/json")
            }
            install(ContentNegotiation) {
                json()
            }


            defaultRequest {
                url("https://uat.ios.mtnirancell.ir/api/")
                contentType(ContentType.Application.Json)
            }

            install(HttpTimeout){
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 5000
            }

        }
    }
}
fun viewModelModule() = module {
    viewModelDefinition { AboutScreenVM() }
    viewModelDefinition { MainScreenVM(get(),get(),get()) }
    viewModelDefinition { SettingScreenVM() }
    viewModelDefinition { GpsTrackingReportScreenVM(get()) }
    viewModelDefinition { LoginScreenVM(get()) }
}
