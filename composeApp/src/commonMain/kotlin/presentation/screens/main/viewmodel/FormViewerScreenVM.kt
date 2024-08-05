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
import utils.formatUploadDomainList

class FormViewerScreenVM(
    private val getPhotoByComponentKeyUseCase: GetPhotoByComponentKeyUseCase,
    private val sendFileToServerUseCase: SendFileToServerUseCase
) : BaseViewModel() {


    private val _imageEvent = MutableStateFlow<ImageEvent>(ImageEvent.Default)
    val imageEvent = _imageEvent.asStateFlow()
    val uriList = mutableListOf<String>()

    init {

        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/fbb5143e-531d-4151-9384-7fc768865c36%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/c62a2c98-0aa9-4ef6-ab7e-788b599fe39a%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/e2a0e4f0-489c-42ca-b9f3-3e48ee05f48e%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/0dc84bd5-6055-4a09-9948-83d9e9fb3a2e%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/67ad54c0-665b-4215-bc49-04dd0d85ee0d%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/ea45ed54-d024-45b8-b569-ad7ceca1925e%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/5bba3d0f-5e61-4815-afb2-178758814586%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/988307f0-4568-40f8-b671-44756dea6983%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/6c01a2c5-2d33-4e50-8aa9-252ed2ae7508%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/5c70027f-6cfd-4fe6-805a-37b0d8d82fa1%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/c248354c-5235-4ba3-b897-8cb5194864b5%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/6767cd79-2d04-4ec9-8958-53a3bdfc8663%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/4a446cfd-d6f6-402c-91d3-5a398a306924%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/b16455f9-e630-4ef3-be9b-ddafc2135312%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_19-20240805-00008/65e251f6-3851-42d3-8474-4f34a10bcf64%40Suspend*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/6dafeaa2-229e-413e-bb18-90cfef800448%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/a4a75c65-cc0d-4320-b241-a54d6c104fa8%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/297eb3a1-36a3-4e92-a292-45e5a5b352ed%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/3f1d6ac3-62ee-46d3-91bf-364558c5aadd%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/8e2c5021-f125-47a2-8346-57de0220e4b4%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/33e49d09-6c4b-44ff-b955-02d350fd1274%40image_ixz2q*4096*.jpg")
        uriList.add("content://irancell.nwg.wfm.provider/wfmImages/Process/Original/0/3e3c7866-e7d7-4a95-8622-835128826f17%40image_ixz2q*4096*.jpg")


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
                        println("apiUpload  ${it.data?.toString()}")
                        val formattedList = formatUploadDomainList(it.data!!)
                        formattedList.forEach { println("apiUploadResponse  ${it}") }
                    }

                }
            }
        }
    }

}
