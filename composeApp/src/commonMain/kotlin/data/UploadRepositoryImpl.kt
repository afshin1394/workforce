package data

import data.network.request.upload.UploadRequest
import data.network.response.task.UploadNetworkResponse
import domain.repository.IUploadRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import irancell.nwg.wfm.GetFile

class UploadRepositoryImpl(
    private val httpClient: HttpClient,
): IUploadRepository {

    override suspend fun fetchUpload(uploadRequest:UploadRequest) : List<UploadNetworkResponse> {
        val request  = httpClient.submitFormWithBinaryData(
            url = "workforce_management/user/upload/",
            formData = formData {
                append("extra_info", uploadRequest.extraInfo, Headers.build {
                    append(HttpHeaders.ContentType, "application/json")
                })
                append("total_part", uploadRequest.totalPart.toString())
                append("current_part", uploadRequest.currentPart.toString())
                append("name", uploadRequest.name)
                append("custom_id", uploadRequest.customId)

                val platformFile = GetFile(uploadRequest.file.path)
                if (platformFile != null) {
                    append("file", platformFile.readBytes(), Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=${uploadRequest.file.name}"
                        )
                        append(HttpHeaders.ContentType, "application/zip")
                    })
                }
            })

        if (request.status == HttpStatusCode.OK) {
            return try {

                request.body<List<UploadNetworkResponse>>()

            } catch (exception: Exception) {
                emptyList()
            }
        }
        throw Exception()
    }
}