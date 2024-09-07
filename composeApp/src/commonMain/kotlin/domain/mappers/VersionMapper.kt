package domain.mappers


import data.network.response.version.GetVersionNetworkResponse
import data.network.response.version.SendVersionNetworkResponse
import domain.models.version.GetVersionDomain
import domain.models.version.SendVersionDomain

fun SendVersionNetworkResponse.toVersionDomain(): SendVersionDomain {
    return SendVersionDomain(
        current_version_code = current_version_code,
        current_version_name = current_version_name,
        device_model = device_model,
        os = os,
        os_version = os_version


    )
}

fun GetVersionNetworkResponse.toGetVersionDomain(): GetVersionDomain {
    return GetVersionDomain(
        id=id,
        version_name=version_name,
        version_code=version_code,
        os=os,
        apk_file=apk_file,
        ipa_link=ipa_link?:"",
        title=title,
        description=description,
        force_update=force_update)
}