package domain.usecase.usecase.mokSteps

import data.network.response.task.Layout
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ConditionalDomain
import domain.models.form_struct.FormStructDomain
import domain.models.form_struct.LayoutDomain
import domain.models.form_struct.ValidateDomain
import domain.models.form_struct.ValueDomain
import domain.usecase.BaseUseCase

import presentation.model.StepModel

class GetMokStepFormUseCase : BaseUseCase<List<StepModel>, Unit>() {

    override suspend fun run(params: Unit): List<StepModel> {

        val stepForm=  listOf(
            StepModel(
                activityID = 1,
                activityTitle = "Initial Setup",
                isActive = true,
                form_structure = FormStructDomain(
                    id = "create_wo133", hide = null, type = "default", components = listOf(


                        ComponentDomain(
                            id = "Field_125fkjr",
                            hide = null,
                            type = "group",
                            label ="",
                            layout = null,
                            subType = null,
                            validate = null,
                            values = null,
                            conditional = null,
                            components = listOf(

                                ComponentDomain(
                                    id = "Field_1ka1qac33",
                                    hide = null,
                                    type ="textfield",
                                    label =" Short Text",
                                    layout = null,
                                    subType = null,
                                    validate =  ValidateDomain( id = "Field_1ka1qac33",required = true, maxLength = 12, minLength = 8),
                                    values = null,
                                    conditional = null,
                                    components = null,
                                    logics = null,

                                    ),


                                ComponentDomain(
                                    id = "Field_0d9qry754",
                                    hide = null,
                                    type = "fileupload",
                                    label = "Attachment",
                                    layout = LayoutDomain(row = "Row_021wo56", columns = null),
                                    subType = null,
                                    validate = ValidateDomain(id ="Field_0d9qry754", required = true, maxTotalSize = 1, maxFileNumber = 1, blacklistAttachment = "pdf,json", attachedValidationType = "Blacklist"),
                                    values = null,
                                    conditional = ConditionalDomain(string = null),
                                    components = null,
                                    logics = null
                                ),
                                ComponentDomain(
                                    id = "Field_1xswkgr",
                                    hide = null,
                                    type = "image",
                                    label = "Image view",
                                    layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                                    subType = null,
                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
                                    values = null ,
                                    conditional = ConditionalDomain(string = null),
                                    components = null,
                                    logics = null
                                ),
                            )

                        ),



                        ), conditional = ConditionalDomain(string = null), schemaVersion = 11
                )
            ),
            StepModel(
                activityID = 2,
                activityTitle = "Review",
                isActive = false,
                form_structure =FormStructDomain(
                    id = "create_wo126552", hide = null, type = "default", components = listOf(


                        ComponentDomain(
                            id = "Field_125f45675kjr",
                            hide = null,
                            type = "group",
                            label ="",
                            layout = null,
                            subType = null,
                            validate = null,
                            values = null,
                            conditional = null,
                            components = listOf(

                                ComponentDomain(
                                    id = "Field_1ka1qac3366",
                                    hide = null,
                                    type ="textfield",
                                    label =" Short Text",
                                    layout = null,
                                    subType = null,
                                    validate =  ValidateDomain( id = "Field_1ka1qac33",required = true, maxLength = 12, minLength = 8),
                                    values = null,
                                    conditional = null,
                                    components = null,
                                    logics = null,

                                    ),
                            )

                        ),



                        ), conditional = ConditionalDomain(string = null), schemaVersion = 11
                )
            ),
            StepModel(
                activityID = 3,
                activityTitle = "Finalization",
                isActive = false,
                form_structure =FormStructDomain(
                    id = "create_wo555", hide = null, type = "default", components = listOf(


                        ComponentDomain(
                            id = "Field_12744fkjr",
                            hide = null,
                            type = "group",
                            label ="",
                            layout = null,
                            subType = null,
                            validate = null,
                            values = null,
                            conditional = null,
                            components = listOf(
                                ComponentDomain(
                                    id = "Field_1ka1qac",
                                    hide = null,
                                    type = mapSubtypeToType("datetime"),
                                    label =" Required Finish Time",
                                    layout = null,
                                    subType = "datetime",
                                    validate =  ValidateDomain( id = "Field_1ka1qac",required = true),
                                    values = null,
                                    conditional = null,
                                    components = null,
                                    logics = null,),
                                ComponentDomain(
                                    id = "Field_10siih0",
                                    hide = null,
                                    type = mapSubtypeToType("date"),
                                    label =" Required Finish Time",
                                    layout = null,
                                    subType = "date",
                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
                                    values = null,
                                    conditional = null,
                                    components = null,
                                    logics = null,),
                                ComponentDomain(
                                    id = "Field_10siih0",
                                    hide = null,
                                    type = mapSubtypeToType("time"),
                                    label =" Required Finish Time",
                                    layout = null,
                                    subType = "time",
                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
                                    values = null,
                                    conditional = null,
                                    components = null,
                                    logics = null,
                                ),
                            )

                        ),



                        ), conditional = ConditionalDomain(string = null), schemaVersion = 11
                )
            )
        )

        return stepForm
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