package di

import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import data.GeneralLocationRepositoryImpl
import domain.usecase.usecase.GetAvailabilityUseCase
import domain.usecase.usecase.GetGeneralLocationListUseCase
import domain.usecase.usecase.SendLocationToServerUseCase
import domain.usecase.usecase.StoreAvailabilityUseCase
import domain.usecase.usecase.StoreLocationDataUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest

import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import irancell.nwg.wfm.DatabaseDriverFactory
import irancell.nwg.wfm.db.WFMDatabase
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.viewModelDefinition
import org.koin.dsl.module
import presentation.screens.main.viewmodel.AboutScreenVM
import presentation.screens.main.viewmodel.GpsTrackingReportScreenVM
import utils.Token


fun repositoryModule() = module {
    //Repositories
    single { WFMDatabase(DatabaseDriverFactory.createDriver())}
    single { GeneralLocationRepositoryImpl(get(),get()) }
}

fun useCaseModule() = module {
    //UseCases
    single { GetGeneralLocationListUseCase(get()) }
    single { GetAvailabilityUseCase() }
    single { StoreAvailabilityUseCase() }
    single { SendLocationToServerUseCase(get()) }
    single { StoreLocationDataUseCase(get()) }
}


fun httpModule() = module {
    single {
        HttpClient {

            expectSuccess = true
            headers {
                append(HttpHeaders.Authorization, "Token ${getSharedPref().getString(Token)}")
                append(HttpHeaders.ContentType, "application/json")
            }
            defaultRequest {
                url("https://uat.ios.mtnirancell.ir/api/")
            }

//            install(ContentNegotiation) {
//                json(json = Json { ignoreUnknownKeys = true })
//            }
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
    viewModelDefinition { MainScreenVM(get(),get()) }
    viewModelDefinition { SettingScreenVM() }
    viewModelDefinition { GpsTrackingReportScreenVM(get()) }
}
