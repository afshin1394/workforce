package domain.repository

import data.network.response.task.step.TaskStepResponse


interface IStepsRepository {
    suspend fun fetch(query : String) : TaskStepResponse
}