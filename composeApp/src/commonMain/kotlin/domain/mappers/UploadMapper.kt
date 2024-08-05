package domain.mappers

import data.network.response.task.UploadNetworkResponse
import domain.models.UploadDomain




fun UploadNetworkResponse.toUploadDomain() : UploadDomain {
    return UploadDomain(
        key = key,
        value=value

    )
}
fun List<UploadNetworkResponse>.toUploadDomainList() : List<UploadDomain>{
    return map {
        it.toUploadDomain()
    }
}