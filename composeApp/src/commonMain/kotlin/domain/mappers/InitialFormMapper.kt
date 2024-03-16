package domain.mappers

import data.network.response.task.TasksNetworkResponse
import domain.models.initialForm.InitialFormDomain
import irancell.nwg.wfm.db.InitialFormEntity
import kotlinx.serialization.json.Json

fun TasksNetworkResponse.toInitialFormEntity(): InitialFormEntity {
    return   InitialFormEntity(
        wi_id = this.wi_id,
        structure = this.initial_form.toString()
    )
}

fun List<TasksNetworkResponse>.toInitialFormEntity(): List<InitialFormEntity> {
    return  map{
        InitialFormEntity(
            wi_id = it.wi_id,
            structure = it.initial_form.toString()
        )
    }
}

fun InitialFormEntity.toInitialFormDomain() : InitialFormDomain {
    return InitialFormDomain(
        wi_id = this.wi_id,
        structure = Json.decodeFromString(this.structure)
    )
}

fun  List<InitialFormEntity>.toInitialFormDomain() : List<InitialFormDomain> {
    return map {
        InitialFormDomain(
            wi_id = it.wi_id,
            structure = Json.decodeFromString(it.structure)
        )
    }
}