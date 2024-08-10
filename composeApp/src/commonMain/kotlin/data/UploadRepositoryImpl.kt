package data

import data.network.request.upload.UploadRequest
import data.network.response.task.UploadNetworkResponse
import domain.repository.IUploadRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json



class UploadRepositoryImpl(
    private val httpClient: HttpClient,
): IUploadRepository {

    override suspend fun fetchUpload(uploadRequest:UploadRequest) : List<UploadNetworkResponse> {
        val jsonExtraInfo = Json.encodeToString(uploadRequest.extraInfo)

        val response: HttpResponse = httpClient.post("workforce_management/user/upload/") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("extra_info", jsonExtraInfo)
                        append("total_part", uploadRequest.totalPart.toString())
                        append("current_part", uploadRequest.currentPart.toString())
                        append("name", uploadRequest.name)
                        append("custom_id", uploadRequest.customId)

                        // Adding file part
                        append("file", uploadRequest.file, Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=file; filename=\"files.zip\"")
                            append(HttpHeaders.ContentType, "application/zip")
                        })
                    }
                )
            )
        }

        if (response.status == HttpStatusCode.OK) {
            return try {

                response.body<List<UploadNetworkResponse>>()

            } catch (exception: Exception) {
                emptyList()
            }
        }
        throw Exception()
    }
}

