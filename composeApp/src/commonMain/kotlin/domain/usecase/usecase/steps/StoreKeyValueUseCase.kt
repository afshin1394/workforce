package domain.usecase.usecase.steps

import domain.models.form_struct.ComponentDomain
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import domain.mappers.toActivityDomain
import utils.toJson

class StoreKeyValueUseCase(
    private val iStepPointerRepository: IStepPointerRepository,
    private val iStepsRepository: IStepsRepository,
    private val iSendStepsRepository: ISendStepsRepository
) : BaseUseCase<Unit, String>() {
    override suspend fun run(params: String) {
        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params)
        val activityDomain = iStepsRepository.getDataByTicketNumberAndStep(
            stepPointerDomain.ticketNumber,
            stepPointerDomain.activeActivity
        ).toActivityDomain()
        activityDomain.form.form_structure.components.let {
           it?.getKeysAndValues()
        }

        print("dictionaryToJson${dict.toJson()}")
    }
    val dict  = mutableMapOf<String,Any>()

     fun List<ComponentDomain>.getKeysAndValues(){
        this.forEach { componentDomain ->
            componentDomain.values?.let {values->
                if(values.isNotEmpty()){
                    if(values.size>1) {
                        dict[componentDomain.id?:""] = arrayListOf<String>()
                        componentDomain.values?.forEach {value->
                            (dict[componentDomain.id] as ArrayList<String>).add(value.value?:"")
                        }
                    }else{
                        dict[componentDomain.id?:""] = componentDomain.values?.get(0)?.value.toString()
                    }
                }
            }
            componentDomain.components.value?.getKeysAndValues()
        }
    }

}