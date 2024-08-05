package domain.usecase.usecase.upload

import data.network.request.upload.UploadRequest
import domain.mappers.toUploadDomainList
import domain.models.UploadDomain
import domain.repository.IUploadRepository
import domain.usecase.BaseUseCase

class SendFileToServerUseCase (
    private val iUploadRepository: IUploadRepository)
    :BaseUseCase<List<UploadDomain>, UploadRequest>() {
    override suspend fun run(params: UploadRequest): List<UploadDomain> {

        val fileList = iUploadRepository.fetchUpload(params)

        return fileList.toUploadDomainList()

    }


}