package di

import presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import presentation.screens.ticket_process.viewModel.TicketProcessVM
import data.AuthRepositoryImpl
import data.AvailabilityRepositoryImpl
import data.GeneralLocationRepositoryImpl
import data.InitialFormRepositoryImpl
import data.PhotoRepositoryImpl
import data.ProfileRepositoryImpl
import data.SendStepRepositoryImpl
import data.StepPointerRepositoryImpl
import data.StepsRepositoryImpl
import data.SuspendTaskRepositoryImpl
import data.TaskRepositoryImpl
import data.UploadRepositoryImpl
import domain.repository.IAuthRepository
import domain.repository.IAvailabilityRepository
import domain.repository.IGeneralLocationRepository
import domain.repository.IInitialFormRepository
import domain.repository.IPhotoRepository
import domain.repository.IProfileRepository
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.repository.ISuspendTaskRepository
import domain.repository.ITaskRepository
import domain.repository.IUploadRepository
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
import domain.usecase.usecase.location.StoreLocationDataUseCase
import domain.usecase.usecase.steps.CheckForEditedTicketUseCase
import domain.usecase.usecase.steps.StoreStepFormUseCase
import domain.usecase.usecase.steps.UpdateStepFormUseCase
import domain.usecase.usecase.photo.DeleteByComponentKeyUseCase
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.photo.InsertPhotoUseCase

import domain.usecase.usecase.profile.GetProfileUseCase
import domain.usecase.usecase.profile.StoreProfileUseCase
import domain.usecase.usecase.steps.SendStepsOfTicketToServerUseCase
import domain.usecase.usecase.steps.StoreKeyValueUseCase
import domain.usecase.usecase.steps.UpdateIsEditedTicketUseCase
import domain.usecase.usecase.steps.UpdateStepsUseCase
import domain.usecase.usecase.suspendTask.DeleteByTaskIdUseCase
import domain.usecase.usecase.suspendTask.GetSuspendTaskByIdUseCase
import domain.usecase.usecase.suspendTask.StoreSuspendTaskUseCase
import domain.usecase.usecase.ticket.UpdateTaskUseCase
import domain.usecase.usecase.ticket.GetTasksUseCase
import domain.usecase.usecase.upload.SendFileToServerUseCase
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

import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
import presentation.screens.ticket_process.viewModel.TicketInfoVM
import utils.Token


fun repositoryModule() = module {
    //Repositories
    single<IGeneralLocationRepository> { GeneralLocationRepositoryImpl(get(), get(named("tokenized"))) }
    single<IAvailabilityRepository> { AvailabilityRepositoryImpl(get(named("tokenized"))) }
    single<ISuspendTaskRepository> { SuspendTaskRepositoryImpl(get(), get(named("tokenized"))) }
    single<IAuthRepository> { AuthRepositoryImpl(get(named("noToken")),get(named("tokenized")),get ()) }
    single<ITaskRepository> { TaskRepositoryImpl(get(named("tokenized")),get()) }
    single<IProfileRepository>{ ProfileRepositoryImpl(get(named("tokenized")),get()) }
    single<IInitialFormRepository>{ InitialFormRepositoryImpl(get()) }
    single<IPhotoRepository>{ PhotoRepositoryImpl(get()) }
    single<IStepsRepository>{ StepsRepositoryImpl(get(named("tokenized")),get()) }
    single<IStepPointerRepository>{ StepPointerRepositoryImpl(get()) }
    single<ISendStepsRepository>{ SendStepRepositoryImpl(get(),get(named("tokenized"))) }
    single<IUploadRepository>{ UploadRepositoryImpl(get(named("tokenized"))) }
}

fun useCaseModule() = module {
    //UseCases
    single { GetGeneralLocationListUseCase(get()) }
    single { GetAvailabilityUseCase(get()) }
    single { StoreAvailabilityUseCase() }
    single { SendLocationToServerUseCase(get()) }
    single { StoreLocationDataUseCase(get()) }
    single { ChangeServerAvailabilityUseCase(get()) }
    single { GetAvailabilityObjectIdUseCase() }
    single { LoginUseCase(get()) }
    single { GetTasksUseCase(get()) }
    single { UpdateTaskUseCase(get(),get()) }
    single { LoginUseCase(get()) }
    single { VerifyUseCase(get()) }
    single { ResendUseCase(get()) }
    single { StoreSuspendTaskUseCase(get()) }
    single { GetSuspendTaskByIdUseCase(get()) }
    single { StoreProfileUseCase(get()) }
    single { GetProfileUseCase(get()) }
    single { DeleteByTaskIdUseCase(get()) }
    single { GetInitialFormByTask(get())}
    single { LogoutUseCase(get()) }
    single { InsertPhotoUseCase(get()) }
    single { GetPhotoByComponentKeyUseCase(get()) }
    single { DeleteByComponentKeyUseCase(get()) }
    single { UpdateStepsUseCase(get(),get(),get(),get()) }
    single { UpdateStepFormUseCase(get(),get(),get(),get()) }
    single { StoreStepFormUseCase(get(),get(),get(),get()) }
    single { CheckForEditedTicketUseCase(get()) }
    single { StoreKeyValueUseCase(get(),get(),get()) }
    single { SendFileToServerUseCase(get()) }
    single { StoreKeyValueUseCase(get(),get(),get()) }
    single { SendStepsOfTicketToServerUseCase(get(),get()) }
    single { UpdateIsEditedTicketUseCase(get())}
}

fun httpModule() = module {
    single(named("tokenized")) {
        HttpClient {
            expectSuccess = true
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
                        "Token ${getSharedPref().getString(Token)}"
                    )
                    append("Content-Type", "application/json")
                    append("accept", "application/json")
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 15000
            }
            addDefaultResponseValidation()
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
    single(named("noToken")) {
        HttpClient {
            expectSuccess = true
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

                    append("Content-Type", "application/json")
                    append("accept", "application/json")
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 15000
            }
            addDefaultResponseValidation()

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
}


fun viewModelModule() = module {
    viewModelDefinition { AboutScreenVM() }
    viewModelDefinition { MainScreenVM(get(), get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get()) }
    viewModelDefinition { SettingScreenVM() }
    viewModelDefinition { GpsTrackingReportScreenVM(get()) }
    viewModelDefinition { LoginScreenVM(get()) }
    viewModelDefinition { VerifyScreenVM(get(),get(),get()) }
    viewModelDefinition { TicketInfoVM(get(),get(),get(),get()) }
    viewModelDefinition { TicketProcessVM(get(),get(),get(),get(),get(),get()) }
    viewModelDefinition { FormViewerScreenVM(get(),get()) }
    viewModelDefinition { MapVM() }
    viewModelDefinition { AccountScreenVM(get()) }
    viewModelDefinition { NotificationScreenVM() }
}
