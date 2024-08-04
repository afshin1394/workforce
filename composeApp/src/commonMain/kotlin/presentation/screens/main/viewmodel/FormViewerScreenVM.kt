package presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import data.network.request.upload.UploadRequest
import domain.models.PhotoDomain
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.upload.SendFileToServerUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import presentation.screens.ticket_process.events.ImageEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class FormViewerScreenVM(
    private val getPhotoByComponentKeyUseCase: GetPhotoByComponentKeyUseCase,
    private val sendFileToServerUseCase: SendFileToServerUseCase
) : BaseViewModel() {


    private val _imageEvent = MutableStateFlow<ImageEvent>(ImageEvent.Default)
    val imageEvent = _imageEvent.asStateFlow()
    val uriList = mutableListOf<String>()

    init {
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_13-20240803-00005/cd893caa-3920-4300-8d64-6d4dc5615c77%40Suspend.jpg")
    }




    private val photoDomain = MutableStateFlow<PhotoDomain>(
        PhotoDomain(
            "--workorder_13-20240731-00002",
            "0",
            0,
            "",
            "",
            "0"
        )
    )
    var photoDomainList = mutableStateListOf<PhotoDomain>()


    fun getPhotoByComponentKey() {
        viewModelScope.launch {
            getPhotoByComponentKeyUseCase(
                "--workorder_13-20240731-00002"
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, "getAllPhotoUseCase", message = "LOADING: ")
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.SUCCESS -> {
                        photoDomainList.clear()
                        updateState(ViewStates.Success())

                        it.data?.let { it1 ->
                            for (i in it1.indices) {
                                photoDomain.value = PhotoDomain(
                                    "--workorder_13-20240731-00002",
                                    "0",
                                    i.toLong(),
                                    it1[i].origin_uri,
                                    it1[i].edited_uri,
                                    it1[i].angle
                                )
                                photoDomainList.add(photoDomain.value)
                            }
                        }




                       photoDomainList.forEach { photoDomain ->
                            uriList.add(photoDomain.origin_uri)
                            uriList.add(photoDomain.edited_uri)
                        }


                        uriList.forEach {
                            println("URI: $it")
                        }
                    }
                }
            }
        }
    }

/*    fun createJsonWithTicketNumber(ticketNumber: String): ExtraInfo {
        return ExtraInfo(ticket_num = ticketNumber)
    }*/






     fun callApiUpload(uploadRequest: UploadRequest) {
        viewModelScope.launch(Dispatchers.Main) {


            sendFileToServerUseCase(uploadRequest).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        println("apiUpload  ${"Error"}")
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                        println("apiUpload  ${"Loading"}")
                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())
                        println("apiUpload  ${it.data?.get(0)?.value}")
                    }

                }
            }
        }
    }

}
