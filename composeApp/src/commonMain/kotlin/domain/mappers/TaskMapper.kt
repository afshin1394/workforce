package domain.mappers

import data.network.response.task.task.Detail
import data.network.response.task.task.TasksNetworkResponse
import database.entity.TaskEntity
import domain.models.task.BasicInfoDomain
import domain.models.task.TaskDomain

import utils.TaskState


fun Detail.toTaskEntity(): TaskEntity {
    return   TaskEntity(
        ticket_number = this.basic_info.ticket_number?:"",
        ticket_state = this.basic_info.ticket_state?:"",
        city = this.basic_info.city?:"",
        site = this.basic_info.site?:"",
        level = this.basic_info.level?:"",
        region = this.basic_info.region?:"",
        location = this.basic_info.location?:"",
        province = this.basic_info.province?:""
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
    return  TaskDomain(
         initial_form = null,
         basic_info = BasicInfoDomain(
             ticket_number =this.ticket_number,
             ticket_state = this.ticket_state,
             level = this.level,
             location= this.location,
             site = this.site,
             region = this.region,
             province = this.province,
             city= this.city,
             instanceStateId = checkForInstanceStateId(this.ticket_state)
         )
        )

}


fun List<TaskEntity>.toTaskDomainList(): List<TaskDomain> {
    return map {
       it.toTaskDomain()
    }
}


fun checkForInstanceStateId(ticketInstanceState: String): Int {

   return when(ticketInstanceState){
        TaskState.Cancelled.title->{
            TaskState.Cancelled.id
        }
        TaskState.Draft.title->{
            TaskState.Draft.id
        }
        TaskState.Parked.title->{
            TaskState.Parked.id
        }
        TaskState.Running.title->{
            TaskState.Running.id
        }
        TaskState.FPA_RollBack.title ->{
            TaskState.FPA_RollBack.id
        }
        TaskState.Suspended.title->{
            TaskState.Suspended.id
        }
        TaskState.Completed.title->{
            TaskState.Completed.id
        }

       else -> {
           TaskState.All.id
       }
   }

}