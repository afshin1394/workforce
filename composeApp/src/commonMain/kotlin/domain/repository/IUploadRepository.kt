package domain.repository

import data.network.request.upload.UploadRequest
import data.network.response.task.UploadNetworkResponse

interface IUploadRepository {

    suspend fun fetchUpload(uploadRequest: UploadRequest) : List<UploadNetworkResponse>
}