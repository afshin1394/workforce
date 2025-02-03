package domain.mappers

import data.network.response.task.activity.ActivityListResponse
import data.network.response.task.task.Detail
import data.network.response.task.task.InstanceTicketsBasicInformationValues
import data.network.response.task.task.InstanceTicketsProperties
import database.entity.ActivityListEntity
import database.entity.TaskEntity
import domain.models.task.ActivityListDomain
import domain.models.task.BasicInfoDomain
import domain.models.task.InstanceTicketsBasicInformationValuesDomain
import domain.models.task.PropertiesDomain
import domain.models.task.TaskDomain
import io.ktor.util.reflect.instanceOf
import presentation.model.FilterType
import presentation.model.StateFilter

import utils.TaskState


fun Detail.toTaskEntity(): TaskEntity {
    return TaskEntity(
        ticket_id = this.instance__tickets__id,
        ticket_type_id = this.instance__tickets__ticket_id,
        instancePrefix = "",
        ticket_number = this.instance__tickets__number,
        ticket_state = this.instance__tickets__state ?: "",
        activity__title = this.activity__title,
        activity_id = this.activity_id,
        properties = this.instance__tickets__properties
    )
}

/*fun Detail.toTaskDomainDetail(): TaskDomain {
    return   TaskDomain(
        initial_form = null,
        basic_info = BasicInfoDomain(
        ticket_number = this.basic_info.ticket_number?:"",
        ticket_state = this.basic_info.ticket_state?:"",
        city = this.basic_info.city?:"",
        site = this.basic_info.site?:"",
        level = this.basic_info.level?.toInt()?:-1,
        region = this.basic_info.region?:"",
        location = this.basic_info.location?:"",
        province = this.basic_info.province?:"",
        instanceStateId =null
    )
    )
}


fun List<Detail>.toTaskDomainList(): List<TaskDomain> {
    return map {
        it.toTaskDomainDetail()
    }
}*/

fun List<Detail>.toTaskEntityList(): List<TaskEntity> {
    return map {
        it.toTaskEntity()
    }
}

fun TaskEntity.toTaskDomain(): TaskDomain {
    return TaskDomain(

        ticket_id=this.ticket_id,
        ticket_number=this.ticket_number,
        ticket_type_id = this.ticket_type_id,
        instancePrefix = this.instancePrefix,
        ticket_state=this.ticket_state,
        activity_id = this.activity_id,
        activity__title = this.activity__title,
        instanceStateId = checkForInstanceStateId(this.ticket_state),
        properties=this.properties.toPropertiesDomainList()
    )

}

fun TaskDomain.toTaskEntity(): TaskEntity {
    return TaskEntity(

        ticket_id=this.ticket_id,
        ticket_number=this.ticket_number,
        ticket_type_id = this.ticket_type_id,
        instancePrefix = this.instancePrefix,
        ticket_state=this.ticket_state,
        activity_id = this.activity_id,
        activity__title = this.activity__title,
        properties=this.properties.toInstanceTicketsPropertiesList()
    )

}



fun List<InstanceTicketsProperties>.toPropertiesDomainList(): List<PropertiesDomain> {
    return map {
        PropertiesDomain(it.key, it.value)
    }
}

fun List<PropertiesDomain>.toInstanceTicketsPropertiesList(): List<InstanceTicketsProperties> {
    return map {
        InstanceTicketsProperties(it.key, it.value)
    }
}

fun List<TaskEntity>.toTaskDomainList(): List<TaskDomain> {
    return map {
        it.toTaskDomain()
    }
}


fun checkForInstanceStateId(ticketInstanceState: String): Int {

    return when (ticketInstanceState) {
        TaskState.Cancelled.title -> {
            TaskState.Cancelled.id
        }

        TaskState.Draft.title -> {
            TaskState.Draft.id
        }

        TaskState.Parked.title -> {
            TaskState.Parked.id
        }

        TaskState.Running.title -> {
            TaskState.Running.id
        }

        TaskState.FPA_RollBack.title -> {
            TaskState.FPA_RollBack.id
        }

        TaskState.Suspended.title -> {
            TaskState.Suspended.id
        }

        TaskState.Completed.title -> {
            TaskState.Completed.id
        }

        else -> {
            TaskState.All.id
        }
    }

}

fun ActivityListResponse.toEntity(): ActivityListEntity {
    return ActivityListEntity(
        pk = 0,
        id = this.id,
        title = this.title,


    )
}

/*fun List<ActivityListResponse>.toEntityList(): List<ActivityListEntity> {
    return this.map { it.toEntity() }
}*/

fun ActivityListDomain.domainToEntity(): ActivityListEntity {
    return ActivityListEntity(
        id = this.id,
        title = this.title,
        instancePrefix = this.instancePrefix
    )
}
fun List<ActivityListDomain>.toEntityList(): List<ActivityListEntity> {
    return this.map { it.domainToEntity() }
}

fun ActivityListEntity.toDomain(): ActivityListDomain {
    return ActivityListDomain(
        id = this.id,
        title = this.title,
        instancePrefix=this.instancePrefix
    )
}

fun List<ActivityListEntity>.toDomainList(): List<ActivityListDomain> {
    return this.map { it.toDomain() }
}

fun ActivityListDomain.toStateFilter(): StateFilter {
    return StateFilter(
        id = this.id?.toInt() ?: 0,
        title = this.title ?: "",
        isActive = false,
        type = FilterType.DEFAULT
    )
}

fun List<ActivityListDomain>.toStateFilterList(): List<StateFilter> {
    return this.map { it.toStateFilter() }
}