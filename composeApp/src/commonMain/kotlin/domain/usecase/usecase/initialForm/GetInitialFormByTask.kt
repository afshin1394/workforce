package domain.usecase.usecase.initialForm

import data.network.response.task.Layout
import data.network.response.task.logic.BindLogicDomian
import data.network.response.task.logic.FieldOptionDomain
import data.network.response.task.logic.LogicDomain
import domain.mappers.toInitialFormDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ConditionalDomain
import domain.models.form_struct.FormStructDomain
import domain.models.form_struct.InitialFormDomain
import domain.models.form_struct.LayoutDomain
import domain.models.form_struct.OperatorDomain
import domain.models.form_struct.ValidateDomain
import domain.models.form_struct.ValueDomain
import domain.models.form_struct.logic.ConditionDomain
import domain.models.form_struct.logic.ExpressionDomain
import domain.repository.IInitialFormRepository
import domain.usecase.BaseUseCase
import utils.LogicType
import utils.OperatorType

class GetInitialFormByTask(
    private val iIInitialFormRepository: IInitialFormRepository
) : BaseUseCase<InitialFormDomain, String>() {
    override suspend fun run(params: String): InitialFormDomain {

        val initialForm =
            iIInitialFormRepository.getInitialFormByTicketNumber(params).toInitialFormDomain()


        val updatedComponents = updateComponentTypes(initialForm.structure.components)


        return initialForm.copy(
            structure = initialForm.structure.copy(
                components = updatedComponents
            )
        )

    }


    private fun updateComponentTypes(components: List<ComponentDomain>?): List<ComponentDomain>? {
        return components?.map { component ->
            val newType = mapSubtypeToType(component.subType) ?: component.type
            component.copy(
                type = newType,
                components = updateComponentTypes(component.components)
            )
        }
    }

    private fun mapSubtypeToType(subtype: String?): String? {
        return when (subtype) {
            "datetime" -> "datetime"
            "date" -> "date"
            "time" -> "time"
            "multi" -> "multi"
            else -> null
        }

    }
}
