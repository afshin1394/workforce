package domain.mappers

import data.network.response.task.TasksNetworkResponse
import domain.models.TaskDomain
import irancell.nwg.wfm.db.TaskEntity
import utils.TaskState


fun TasksNetworkResponse.toTaskEntity(): TaskEntity {
    return   TaskEntity(
        wi_id = this.wi_id,
        ticket_instance_id = this.ticket_instance_id,
        ticket_title = this.ticket_title.orEmpty(),
        ticket_instance_number = this.ticket_instance_number.orEmpty(),
        ticket_instance_state = this.ticket_instance_state.orEmpty(),
        ticket_instance_title = this.ticket_instance_title.orEmpty(),
        type = "" /*this.ticket_instance_action.type.orEmpty()*/,
        description = ""/*this.ticket_instance_action.description.orEmpty()*/,
        category = ""/*this.ticket_instance_action.category.orEmpty()*/,
        subCategory = ""/*this.ticket_instance_action.subCategory.orEmpty()*/,
        attachment = "" /*this.ticket_instance_action.attachment.orEmpty()*/
    )
}

fun List<TasksNetworkResponse>.toTaskEntityList(): List<TaskEntity> {
    return map {
       it.toTaskEntity()
    }
}

fun TaskEntity.toTaskDomain(): TaskDomain {
    return  TaskDomain(
            workId = this.wi_id,
            instanceId = this.ticket_instance_id,
            title = this.ticket_title,
            instanceNumber = this.ticket_instance_number,
            instanceState = this.ticket_instance_state,
            instanceStateId = checkForInstanceStateId(this.ticket_instance_state),
            instanceTitle = this.ticket_instance_title,
            status = this.type,
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