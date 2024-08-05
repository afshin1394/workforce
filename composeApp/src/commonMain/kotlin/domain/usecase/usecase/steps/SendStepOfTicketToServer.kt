package domain.usecase.usecase.steps

import data.network.request.step.StepRequest
import data.network.request.step.SubmitAllRequest
import domain.repository.ISendStepsRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import utils.toJson

class SendStepOfTicketToServer(private val iSendStepsRepository: ISendStepsRepository) :
    BaseUseCase<Unit, String>() {
    override suspend fun run(params: String) {
        val sendStepsEntity = iSendStepsRepository.getStepsByTicketNumber(ticketNumber = params)
        val sortedSendSteps = sendStepsEntity.sortedBy { it.activityId }
        val submitAllRequest = SubmitAllRequest(ticket_num = params, steps = arrayListOf())


        sortedSendSteps.forEachIndexed { index, item ->
            if (index != 0)
                submitAllRequest.steps
                    .add(StepRequest(item.activityId.toInt(), item.key_value_structure, 0))
            else
                submitAllRequest.steps.add(
                    StepRequest(
                        item.activityId.toInt(),
                        item.key_value_structure,
                        item.wi.toInt()
                    )
                )
        }
        Napier.log(LogLevel.ASSERT, tag = "sortedSendSteps", message = Json.encodeToString(SubmitAllRequest.serializer(),submitAllRequest).replace("}\"", "}").replace("\"{", "{").replace("\\",""))

        iSendStepsRepository.sendData(Json.encodeToString(SubmitAllRequest.serializer(),submitAllRequest).replace("}\"", "}").replace("\"{", "{").replace("\\",""))

    }
}