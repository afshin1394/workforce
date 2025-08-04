package di

import com.benasher44.uuid.uuid4
import io.ktor.client.*
import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import data.AuthRepositoryImpl
import data.AvailabilityRepositoryImpl
import data.GeneralLocationRepositoryImpl
import data.InitialFormRepositoryImpl
import data.IpDetectionRepositoryImpl
import data.PhotoRepositoryImpl
import data.ProfileRepositoryImpl
import data.SendStepRepositoryImpl
import data.StepPointerRepositoryImpl
import data.StepsRepositoryImpl
import data.SuspendTaskRepositoryImpl
import data.TaskRepositoryImpl
import data.TicketRepositoryImpl
import data.UploadRepositoryImpl
import data.VersionRepositoryImpl
import data.DownloadRepositoryImpl
import domain.repository.IAuthRepository
import domain.repository.IDownloadRepository
import domain.repository.IAvailabilityRepository
import domain.repository.IGeneralLocationRepository
import domain.repository.IInitialFormRepository
import domain.repository.IIpDetectionRepository
import domain.repository.IPhotoRepository
import domain.repository.IProfileRepository
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.repository.ISuspendTaskRepository
import domain.repository.ITaskRepository
import domain.repository.ITicketRepository
import domain.repository.IUploadRepository
import domain.repository.IVersionRepository
import domain.usecase.usecase.auth.AutoLogoutUseCase
import domain.usecase.usecase.auth.LoginUseCase
import domain.usecase.usecase.auth.LogoutUseCase
import domain.usecase.usecase.auth.ResendUseCase
import domain.usecase.usecase.auth.VerifyUseCase
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityObjectIdUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.location.GetGeneralLocationListUseCase
import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.availability.StoreAvailabilityUseCase
import domain.usecase.usecase.initialForm.GetInitialFormByTask
import domain.usecase.usecase.ipDetection.IpDetectionUseCase
import domain.usecase.usecase.location.DeleteSendLocationUseCase
import domain.usecase.usecase.location.StoreLocationDataUseCase
import domain.usecase.usecase.location.UpdateUnSendLocationUseCase
import domain.usecase.usecase.steps.CheckForEditedTicketUseCase
import domain.usecase.usecase.steps.StoreStepFormUseCase
import domain.usecase.usecase.steps.UpdateStepFormUseCase
import domain.usecase.usecase.photo.DeleteByComponentKeyUseCase
import domain.usecase.usecase.photo.DeletePhotoByComponentKeyAndIdUseCase
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.photo.InsertPhotoUseCase
import domain.usecase.usecase.profile.GetProfileUseCase
import domain.usecase.usecase.profile.StoreProfileUseCase
import domain.usecase.usecase.steps.SendStepsOfTicketToServerUseCase
import domain.usecase.usecase.steps.StoreKeyValueUseCase
import domain.usecase.usecase.steps.UpdateIsEditedTicketUseCase
import domain.usecase.usecase.suspendTask.DeleteByTaskIdUseCase
import domain.usecase.usecase.suspendTask.GetSuspendTaskByIdUseCase
import domain.usecase.usecase.suspendTask.StoreSuspendTaskUseCase
import domain.usecase.usecase.ticket.GetActivityListUseCase
import domain.usecase.usecase.ticket.UpdateTaskUseCase
import domain.usecase.usecase.ticket.GetTasksUseCase
import domain.usecase.usecase.ticket.GetTasksPaginatedUseCase
import domain.usecase.usecase.ticket.GetTicketDetailsUseCase
import domain.usecase.usecase.upload.SendFileToServerUseCase
import domain.usecase.usecase.version.GetVersionOfServerUseCase
import domain.usecase.usecase.version.SendVersionToServerUseCase
import domain.usecase.usecase.download.CompleteDownloadFlowUseCase
import domain.usecase.usecase.download.StartDownloadUseCase
import domain.usecase.usecase.download.GetChunkUseCase
import domain.usecase.usecase.download.GetStepsByTicketUseCase
import domain.usecase.usecase.database.CheckDatabaseDataUseCase
import domain.usecase.usecase.database.ClearDatabaseUseCase
import io.ktor.client.HttpClient
import data.network.configureNetworking
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
import io.ktor.serialization.kotlinx.json.json
import irancell.nwg.wfm.configureForPlatform
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.viewModelDefinition
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import presentation.screens.auth.viewmodel.LoginScreenVM
import presentation.screens.auth.viewmodel.VerifyScreenVM
import presentation.screens.main.viewmodel.AboutScreenVM
import presentation.screens.main.viewmodel.AccountScreenVM
import presentation.screens.main.viewmodel.FormViewerScreenVM
import presentation.screens.main.viewmodel.GpsTrackingReportScreenVM
import presentation.screens.main.viewmodel.MapVM
import presentation.screens.main.viewmodel.NotificationScreenVM
import presentation.screens.splash.viewmodel.SplashScreenVM
import presentation.screens.ticket_process.viewModel.TicketInfoVM
import presentation.screens.ticket_process.viewModel.TicketStructureInfoVM
import presentation.screens.download.viewmodel.DownloadViewModel
import utils.DeploymentBASEURL
import utils.DevelopmentBASEURL
import utils.Token

fun repositoryModule() = module {
    //Repositories
    factory<IGeneralLocationRepository> {
        GeneralLocationRepositoryImpl(
            get(),
            get(named("tokenized"))
        )
    }

    factory<IGeneralLocationRepository> {
        GeneralLocationRepositoryImpl(
            get(), get(named("tokenized"))
        )
    }
    factory<IAvailabilityRepository> { AvailabilityRepositoryImpl(get(named("tokenized"))) }
    factory<ISuspendTaskRepository> { SuspendTaskRepositoryImpl(get(), get(named("tokenized"))) }
    factory<IAuthRepository> {
        AuthRepositoryImpl(
            get(named("noToken")),
            get(named("tokenized")),
            get()
        )
    }
    factory<ITaskRepository> { TaskRepositoryImpl(get(named("tokenized")), get()) }
    factory<IProfileRepository> { ProfileRepositoryImpl(get(named("tokenized")), get()) }
    factory<IInitialFormRepository> { InitialFormRepositoryImpl(get()) }
    factory<IPhotoRepository> { PhotoRepositoryImpl(get()) }
    factory<IStepsRepository> { StepsRepositoryImpl(get(named("tokenized")), get()) }
    factory<IStepPointerRepository> { StepPointerRepositoryImpl(get()) }
    factory<ISendStepsRepository> { SendStepRepositoryImpl(get(), get(named("tokenized"))) }
    factory<IUploadRepository> { UploadRepositoryImpl(get(named("tokenized"))) }
    factory<IVersionRepository> {
        VersionRepositoryImpl(
            get(named("noToken")),
            get(named("tokenized"))
        )
    }
    factory<ITicketRepository> { TicketRepositoryImpl(get(named("tokenized"))) }
    factory<IIpDetectionRepository> { IpDetectionRepositoryImpl(get(named("ipDetection"))) }
    factory<IDownloadRepository> { DownloadRepositoryImpl(get(named("tokenized"))) }
}

fun useCaseModule() = module {
    //UseCases
    factory { GetGeneralLocationListUseCase(get()) }
    factory { GetAvailabilityUseCase(get()) }
    factory { StoreAvailabilityUseCase() }
    factory { SendLocationToServerUseCase(get()) }
    factory { StoreLocationDataUseCase(get()) }
    factory { ChangeServerAvailabilityUseCase(get()) }
    factory { GetAvailabilityObjectIdUseCase() }
    factory { LoginUseCase(get()) }
    factory { GetTasksUseCase(get()) }
    factory { GetTasksPaginatedUseCase(get()) }
    factory { GetActivityListUseCase(get()) }
    factory { UpdateTaskUseCase(get(), get(), get(), get(), get()) }
    factory { LoginUseCase(get()) }
    factory { VerifyUseCase(get()) }
    factory { ResendUseCase(get()) }
    factory { StoreSuspendTaskUseCase(get()) }
    factory { GetSuspendTaskByIdUseCase(get()) }
    factory { StoreProfileUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { DeleteByTaskIdUseCase(get()) }
    factory { GetInitialFormByTask(get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { InsertPhotoUseCase(get()) }
    factory { GetPhotoByComponentKeyUseCase(get()) }
    factory { DeleteByComponentKeyUseCase(get()) }
    factory { DeletePhotoByComponentKeyAndIdUseCase(get()) }
    factory { UpdateStepFormUseCase(get(), get(), get(), get()) }
    factory { StoreStepFormUseCase(get(), get(), get(), get()) }
    factory { CheckForEditedTicketUseCase(get()) }
    factory { StoreKeyValueUseCase(get(), get(), get()) }
    factory { SendFileToServerUseCase(get()) }
    factory { StoreKeyValueUseCase(get(), get(), get()) }
    factory { SendStepsOfTicketToServerUseCase(get(), get(), get(), get(), get()) }
    factory { UpdateIsEditedTicketUseCase(get()) }
    factory { UpdateUnSendLocationUseCase(get()) }
    factory { DeleteSendLocationUseCase(get()) }
    factory { SendVersionToServerUseCase(get()) }
    factory { GetVersionOfServerUseCase(get()) }
    factory { GetTicketDetailsUseCase(get()) }
    factory { IpDetectionUseCase(get()) }
    factory { AutoLogoutUseCase(get()) }
    factory { StartDownloadUseCase(get()) }
    factory { GetChunkUseCase(get()) }
    factory { GetStepsByTicketUseCase(get()) }
    factory { CompleteDownloadFlowUseCase(get(), get(), get(), get(), get()) }
    factory { CheckDatabaseDataUseCase(get()) }
    factory { ClearDatabaseUseCase(get(), get()) }

}

fun httpModule() = module {

    factory(named("tokenized")) {
        HttpClient {
            expectSuccess = false // Changed to handle errors properly
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            configureNetworking() // Use improved configuration
            configure()
            defaultRequest {
                url(DevelopmentBASEURL)
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        "Authorization", "Token ${getSharedPref().getString(Token)}"
                    )
                    append("uuid", uuid4().toString())
                    append("Content-Type", "application/json")
                    append("accept", "application/json")
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60000 // Reduced from 20 minutes to 1 minute
                connectTimeoutMillis = 10000 // Increased to 10 seconds
                socketTimeoutMillis = 60000 // Reduced from 20 minutes to 1 minute
            }
            addDefaultResponseValidation()
        }
    }
    factory(named("noToken")) {
        HttpClient {
            expectSuccess = false // Changed to handle errors properly
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            configureNetworking() // Use improved configuration
            configure()
            defaultRequest {
                url(DevelopmentBASEURL)
                contentType(ContentType.Application.Json)
                headers {
                    append("uuid", uuid4().toString())
                    append("Content-Type", "application/json")
                    append("accept", "application/json")
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60000 // Reduced from 20 minutes to 1 minute
                connectTimeoutMillis = 10000 // Increased to 10 seconds
                socketTimeoutMillis = 60000 // Reduced from 20 minutes to 1 minute
            }
            addDefaultResponseValidation()
        }
    }
    single(named("ipDetection")) {
        HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            configure()
            defaultRequest {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Accept,
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
                    )
                    append(HttpHeaders.AcceptEncoding, "gzip, deflate, br, zstd")
                    append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9,fa;q=0.8")
                    append(HttpHeaders.CacheControl, "max-age=0")
                    append(HttpHeaders.IfNoneMatch, "W/\"26-UEXP+PpFoBzIgCgT4fN1+cbEM1E\"")
                    append("priority", "u=0, i")
                    append(
                        "sec-ch-ua",
                        "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\""
                    )
                    append("sec-ch-ua-mobile", "?0")
                    append("sec-ch-ua-platform", "\"Windows\"")
                    append("upgrade-insecure-requests", "1")
                    append(
                        HttpHeaders.UserAgent,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
                    )
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000 // Reduced timeout for IP detection
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 30000
            }

            addDefaultResponseValidation()
            configureNetworking() // Use improved configuration
        }
    }
}

internal fun HttpClientConfig<*>.configure() {

    configureForPlatform()
}

fun viewModelModule() = module {
    viewModelDefinition { AboutScreenVM(get()) }
    viewModelDefinition {
        MainScreenVM(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModelDefinition { SettingScreenVM() }
    viewModelDefinition { GpsTrackingReportScreenVM(get()) }
    viewModelDefinition { LoginScreenVM(get()) }
    viewModelDefinition { VerifyScreenVM(get(), get(), get(), get(), get()) }
    viewModelDefinition { SplashScreenVM(get(), get(), get(), get()) }
    viewModelDefinition { TicketInfoVM(get()) }
    viewModelDefinition { TicketStructureInfoVM(get()) }
    viewModelDefinition { TicketProcessVM(get(), get(), get(), get(), get(), get(), get(),get()) }
    viewModelDefinition { FormViewerScreenVM(get(), get()) }
    viewModelDefinition { MapVM() }
    viewModelDefinition { AccountScreenVM(get()) }
    viewModelDefinition { NotificationScreenVM() }
    viewModelDefinition { DownloadViewModel(get(), get()) }
}
