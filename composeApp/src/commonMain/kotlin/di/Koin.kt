package di

import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import data.AuthRepositoryImpl
import data.AvailabilityRepositoryImpl
import data.GeneralLocationRepositoryImpl
import data.SuspendTaskRepositoryImpl
import data.WorkRepositoryImpl
import domain.usecase.usecase.auth.LoginUseCase
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityObjectIdUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.location.GetGeneralLocationListUseCase
import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.availability.StoreAvailabilityUseCase
import domain.usecase.usecase.location.StoreLocationDataUseCase
import domain.usecase.usecase.work.GetAllWorksUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.ContentType

import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import irancell.nwg.wfm.DatabaseDriverFactory
import irancell.nwg.wfm.db.WFMDatabase
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.httpClient
import irancell.nwg.wfm.viewModelDefinition
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import presentation.screens.auth.viewmodel.LoginScreenVM
import presentation.screens.main.viewmodel.AboutScreenVM
import presentation.screens.main.viewmodel.GpsTrackingReportScreenVM
import utils.Token


fun repositoryModule() = module {
    //Repositories
    single { WFMDatabase(DatabaseDriverFactory.createDriver()) }
    single { GeneralLocationRepositoryImpl(get(), get()) }
    single { AvailabilityRepositoryImpl(get()) }
    single { SuspendTaskRepositoryImpl(get(), get()) }
    single { AuthRepositoryImpl(get()) }
    single { WorkRepositoryImpl(get()) }
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
    single { GetAllWorksUseCase(get()) }
}

fun httpModule() = module {
    single() {
        HttpClient {

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }


            defaultRequest {
                url("https://uat.ios.mtnirancell.ir/api/")
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        "Authorization",
                        "Token eyjt8aZoIgiedTSKYUNLrXd0apPrzQjB5LPqhYV_uv2ZRnkO-2JRU8rIu-BPdr4u0X5YAot8kJ0yqiBQYuzM1w"
                    )
                    append("Content-Type", "application/json")
                    append("accept", "application/json")
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 5000
            }
            addDefaultResponseValidation()
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
}

//fun httpModuleNoAuth() = module {
//    single(named("httpClientNoAuth")) {
//        httpClient() {
//
//            headers {
//                append("Content-Type", "application/json")
//                append("accept","application/json")
//            }
//            install(ContentNegotiation) {
//                json()
//            }
//
//
//            defaultRequest {
//                url("https://uat.ios.mtnirancell.ir/api/")
//                contentType(ContentType.Application.Json)
//            }
//
//            install(HttpTimeout){
//                requestTimeoutMillis = 10000
//                connectTimeoutMillis = 5000
//                socketTimeoutMillis = 5000
//            }
//            addDefaultResponseValidation()
//
//        }
//    }
//}
fun viewModelModule() = module {
    viewModelDefinition { AboutScreenVM() }
    viewModelDefinition { MainScreenVM(get(), get(), get(), get()) }
    viewModelDefinition { SettingScreenVM() }
    viewModelDefinition { GpsTrackingReportScreenVM(get()) }
    viewModelDefinition { LoginScreenVM(get()) }
}
