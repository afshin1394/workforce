package domain.usecase.usecase.initialForm

import data.network.response.task.InitialForm
import data.network.response.task.Layout
import domain.mappers.toInitialFormDomain
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ConditionalDomain
import domain.models.initialForm.InitialFormDomain
import domain.models.initialForm.InitialFormStructureDomain
import domain.models.initialForm.LayoutDomain
import domain.models.initialForm.LogicDomain
import domain.models.initialForm.ValidateDomain
import domain.models.initialForm.ValueDomain
import domain.repository.IInitialFormRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import utils.FormViewerTypes

class GetInitialFormByTask(
    private val iIInitialFormRepository: IInitialFormRepository
) : BaseUseCase<InitialFormDomain, Long>() {
    override suspend fun run(params: Long): InitialFormDomain {

        val initial1 = iIInitialFormRepository.getInitialFormByTaskId(params).toInitialFormDomain()

  /*      val  initial=  InitialFormDomain(
            wi_id = 22969, structure = InitialFormStructureDomain(
                id = "create_wo1", hide = null, type = "default", components = listOf(


                    ComponentDomain(
                    id = "Field_127fkjr",
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
                            id = "Field_1xswkgr",
                            hide = null,
                            type = "image",
                            label = "Image view",
                            layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                            subType = null,
                            validate = null,
                            values = null ,
                            conditional = ConditionalDomain(string = null),
                            components = null,
                            logics = null
                        ),ComponentDomain(
                        id = "Field_10siih0",
                        hide = null,
                        type = "datetime",
                        label =" Required Finish Time",
                        layout = null,
                        subType = null,
                        validate = null,
                        values = null,
                        conditional = null,
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_03lk6nb",
                        hide = null,
                        type = "datetime",
                        label = "Call Out Time",
                        layout = null,
                        subType = null,
                        validate = null,
                        values = null,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_1jgz2o5",
                        hide = null,
                        type = "select",
                        label = "Network Type",
                        layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                        subType = null,
                        validate = null,
                        values = listOf(ValueDomain(label = "2 G", value = "2G"), ValueDomain(
                            label = "3 G",
                            value = "3G"
                        )) ,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_13mxyio",
                        hide = null,
                        type = "select",
                        label = "Product Type",
                        layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                        subType = null,
                        validate = null,
                        values = listOf(ValueDomain(label = "Value", value = "value")),
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_1q2vhtq",
                        hide = null,
                        type = "select",
                        label = "Fault Level",
                        layout = LayoutDomain(row = "Row_1encgwu", columns = null),
                        subType = null,
                        validate = null,
                        values = listOf(ValueDomain(
                            label = "level1",
                            value = "level 1"
                        ), ValueDomain(
                            label = "level2",
                            value = "level 2"
                        ), ValueDomain(label = "level 3", value =" level 3")),
                conditional = ConditionalDomain(string = null),
                components = null,
                logics = null)
                    , ComponentDomain(
                        id = "Field_0v9wkrw",
                        hide = null,
                        type = "select",
                        label = "Region",
                        layout = LayoutDomain(row = "Row_1encgwu", columns = null),
                        subType = null,
                        validate = ValidateDomain(
                            id = "Field_0v9wkrw",
                            key = "region",
                            hide = "Field_0v9wkrw",
                            layout = Layout(row = "Row_1encgwu", columns = null),
                            subtype = null,
                            required = false
                        ),
                        values = null,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_0c97tub",
                        hide = null,
                        type = "select",
                        label =" Affected Service",
                        layout = LayoutDomain(row = "Row_19gvnwv", columns = null),
                        subType = null,
                        validate = null,
                        values = listOf(ValueDomain(label = "Value", value = "value")) ,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_0hiho38",
                        hide = null,
                        type = "select",
                        label = "Task Type",
                        layout = LayoutDomain(row = "Row_19gvnwv", columns = null),
                        subType = null,
                        validate = ValidateDomain(
                            id = "Field_0hiho38",
                            key = "task_type",
                            hide = "Field_0hiho38",
                            layout = Layout(row = "Row_19gvnwv", columns = null),
                            subtype = null,
                            required = false
                        ),
                        values = listOf(ValueDomain(
                            label = "change request",
                            value = "change request"
                        ), ValueDomain(
                            label = "site acceptance",
                            value = "site acceptance"
                        ), ValueDomain(label = "CM", value = "cm"), ValueDomain(
                            label = "PM",
                            value = "pm"
                        )) ,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_071y3pv",
                        hide = null,
                        type = "radio",
                        label = "Send To WFM",
                        layout = LayoutDomain(row = "Row_0oop9pg", columns = null),
                        subType = null,
                        validate = null,
                        values = listOf(ValueDomain(label = "Yes", value = "yes"), ValueDomain(
                            label = "No",
                            value = "no"
                        )) ,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_1k73ge2",
                        hide = null,
                        type = "textarea",
                        label = "Task Description",
                        layout = LayoutDomain(row = "Row_1y3zgom", columns = null),
                        subType = null,
                        validate = ValidateDomain(
                            id = "Field_1k73ge2",
                            key = "task_description",
                            hide = "Field_1k73ge2",
                            layout = Layout(row = "Row_1y3zgom", columns = null),
                            subtype = null,
                            required = false
                        ),
                        values = null,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_133tzg7",
                        hide = null,
                        type = "textarea",
                        label = "Task Requirements",
                        layout = LayoutDomain(row = "Row_1wgv3as", columns = null),
                        subType = null,
                        validate = null,
                        values = null,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_1yizgdx",
                        hide = null,
                        type = "radio",
                        label = "Operation Mode",
                        layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                        subType = null,
                        validate = ValidateDomain(
                            id = "Field_1yizgdx",
                            key = "operation_mode",
                            hide = "Field_1yizgdx",
                            layout = Layout(row = "Row_1fi2zro", columns = null),
                            subtype = null,
                            required = false
                        ),
                        values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
                            label = "Submit to Approve",
                            value = "submit_to_approve"
                        )),
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_1gxcwfu",
                        hide = null,
                        type = "select",
                        label = "Reviewer",
                        layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                        subType = null,
                        validate = null,
                        values = listOf(ValueDomain(label = "Value", value = "value")),
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    ), ComponentDomain(
                        id = "Field_1ed1wj5",
                        hide = null,
                        type = "select",
                        label = "Assign To",
                        layout = LayoutDomain(row = "Row_0qw2myh", columns = null),
                        subType = null,
                        validate = ValidateDomain(
                            id = "Field_1ed1wj5",
                            key = "assign_to",
                            hide = "Field_1ed1wj5",
                            layout = Layout(row = "Row_0qw2myh", columns = null),
                            subtype = null,
                            required = false
                        ),
                        values = null,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null



                    ), ComponentDomain(
                        id = "Field_0d9qry7",
                        hide = null,
                        type = "fileupload",
                        label = "Attachment",
                        layout = LayoutDomain(row = "Row_021wo56", columns = null),
                        subType = null,
                        validate = null,
                        values = null,
                        conditional = ConditionalDomain(string = null),
                        components = null,
                        logics = null
                    )),
                    repeatable= true,
                    logics = null
                ),
                    ComponentDomain(
                        id = "Field_127fkjr",
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
                                id = "Field_4444xswkgr",
                                hide = null,
                                type = "image",
                                label = "Image view",
                                layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                                subType = null,
                                validate = null,
                                values = null ,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ),


                            ComponentDomain(
                            id = "Field_10siih0",
                            hide = null,
                            type = "datetime",
                            label =" Required Finish Time",
                            layout = null,
                            subType = null,
                            validate = null,
                            values = null,
                            conditional = null,
                            components = null,
                            logics = null
                        ), ComponentDomain(
                            id = "Field_03lk6nb",
                            hide = null,
                            type = "datetime",
                            label = "Call Out Time",
                            layout = null,
                            subType = null,
                            validate = null,
                            values = null,
                            conditional = ConditionalDomain(string = null),
                            components = null,
                            logics = null
                        ), ComponentDomain(
                            id = "Field_1jgz2o5",
                            hide = null,
                            type = "select",
                            label = "Network Type",
                            layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                            subType = null,
                            validate = null,
                            values = listOf(ValueDomain(label = "2 G", value = "2G"), ValueDomain(
                                label = "3 G",
                                value = "3G"
                            )) ,
                            conditional = ConditionalDomain(string = null),
                            components = null,
                            logics = null
                        ), ComponentDomain(
                            id = "Field_13mxyio",
                            hide = null,
                            type = "select",
                            label = "Product Type",
                            layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
                            subType = null,
                            validate = null,
                            values = listOf(ValueDomain(label = "Value", value = "value")),
                            conditional = ConditionalDomain(string = null),
                            components = null,
                            logics = null
                        ), ComponentDomain(
                            id = "Field_1q2vhtq",
                            hide = null,
                            type = "select",
                            label = "Fault Level",
                            layout = LayoutDomain(row = "Row_1encgwu", columns = null),
                            subType = null,
                            validate = null,
                            values = listOf(ValueDomain(
                                label = "level1",
                                value = "level 1"
                            ), ValueDomain(
                                label = "level2",
                                value = "level 2"
                            ), ValueDomain(label = "level 3", value =" level 3")),
                            conditional = ConditionalDomain(string = null),
                            components = null,
                            logics = null)
                            , ComponentDomain(
                                id = "Field_0v9wkrw",
                                hide = null,
                                type = "select",
                                label = "Region",
                                layout = LayoutDomain(row = "Row_1encgwu", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_0v9wkrw",
                                    key = "region",
                                    hide = "Field_0v9wkrw",
                                    layout = Layout(row = "Row_1encgwu", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_0c97tub",
                                hide = null,
                                type = "select",
                                label =" Affected Service",
                                layout = LayoutDomain(row = "Row_19gvnwv", columns = null),
                                subType = null,
                                validate = null,
                                values = listOf(ValueDomain(label = "Value", value = "value")) ,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_0hiho38",
                                hide = null,
                                type = "select",
                                label = "Task Type",
                                layout = LayoutDomain(row = "Row_19gvnwv", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_0hiho38",
                                    key = "task_type",
                                    hide = "Field_0hiho38",
                                    layout = Layout(row = "Row_19gvnwv", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(
                                    label = "change request",
                                    value = "change request"
                                ), ValueDomain(
                                    label = "site acceptance",
                                    value = "site acceptance"
                                ), ValueDomain(label = "CM", value = "cm"), ValueDomain(
                                    label = "PM",
                                    value = "pm"
                                )) ,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_071y3pv",
                                hide = null,
                                type = "radio",
                                label = "Send To WFM",
                                layout = LayoutDomain(row = "Row_0oop9pg", columns = null),
                                subType = null,
                                validate = null,
                                values = listOf(ValueDomain(label = "Yes", value = "yes"), ValueDomain(
                                    label = "No",
                                    value = "no"
                                )) ,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_1k73ge2",
                                hide = null,
                                type = "textarea",
                                label = "Task Description",
                                layout = LayoutDomain(row = "Row_1y3zgom", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1k73ge2",
                                    key = "task_description",
                                    hide = "Field_1k73ge2",
                                    layout = Layout(row = "Row_1y3zgom", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_133tzg7",
                                hide = null,
                                type = "textarea",
                                label = "Task Requirements",
                                layout = LayoutDomain(row = "Row_1wgv3as", columns = null),
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_1yizgdx",
                                hide = null,
                                type = "radio",
                                label = "Operation Mode",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1yizgdx",
                                    key = "operation_mode",
                                    hide = "Field_1yizgdx",
                                    layout = Layout(row = "Row_1fi2zro", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
                                    label = "Submit to Approve",
                                    value = "submit_to_approve"
                                )),
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_1gxcwfu",
                                hide = null,
                                type = "select",
                                label = "Reviewer",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = null,
                                values = listOf(ValueDomain(label = "Value", value = "value")),
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_1ed1wj5",
                                hide = null,
                                type = "select",
                                label = "Assign To",
                                layout = LayoutDomain(row = "Row_0qw2myh", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1ed1wj5",
                                    key = "assign_to",
                                    hide = "Field_1ed1wj5",
                                    layout = Layout(row = "Row_0qw2myh", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_0m5gm4v",
                                hide = null,
                                type = "select",
                                label = "Copy To",
                                layout = LayoutDomain(row = "Row_0qw2myh", columns = null),
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ), ComponentDomain(
                                id = "Field_0d9qry7",
                                hide = null,
                                type = "fileupload",
                                label = "Attachment",
                                layout = LayoutDomain(row = "Row_021wo56", columns = null),
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            )),
                        logics = null
                    )



            ),conditional = ConditionalDomain(string = null), schemaVersion = 11)
        )*/



        val  initial=  InitialFormDomain(
            wi_id = 22969, structure = InitialFormStructureDomain(
                id = "create_wo1", hide = null, type = "default", components = listOf(


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
                                id = "Field_10siih0",
                                hide = null,
                                type = "datetime",
                                label =" Required Finish Time",
                                layout = null,
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = null,
                                components = null,
                                logics = null
                            ),

                            ComponentDomain(
                                id = "Field_10siih0",
                                hide = null,
                                type = "time",
                                label =" Required Finish Time",
                                layout = null,
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = null,
                                components = null,
                                logics = null
                            ),
                            ComponentDomain(
                                id = "Field_10siih0",
                                hide = null,
                                type = "date",
                                label =" Required Finish Time",
                                layout = null,
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = null,
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
                                validate = null,
                                values = null ,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ),


                            ComponentDomain(
                                id = "Field_1yizgdx44",
                                hide = null,
                                type = "checklist",
                                label = "Operation Mode",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1yizgdx",
                                    key = "operation_mode",
                                    hide = "Field_1yizgdx",
                                    layout = Layout(row = "Row_1fi2zro", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
                                    label = "Submit to Approve",
                                    value = "submit_to_approve"
                                )),

                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ),


                            ComponentDomain(
                                id = "Field_1yizgdx44",
                                hide = null,
                                type = "checklist",
                                label = "Operation Mode",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1yizgdx",
                                    key = "operation_mode",
                                    hide = "Field_1yizgdx",
                                    layout = Layout(row = "Row_1fi2zro", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(label = "Submit to Approve", value = "submit_to_approve"), ValueDomain(label = "Testtt23", value = "Testtt23"), ValueDomain(label = "Testtt344", value = "Testtt344")),

                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ),


                            ComponentDomain(
                                id = "Field_1yizgdx44",
                                hide = null,
                                type = "select",
                                label = "Operation Mode",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1yizgdx",
                                    key = "operation_mode",
                                    hide = "Field_1yizgdx",
                                    layout = Layout(row = "Row_1fi2zro", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
                                    label = "Submit to Approve",
                                    value = "submit_to_approve"
                                )),

                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            ),


                            ComponentDomain(
                                id = "Field_1yizgdx44",
                                hide = null,
                                type = "select",
                                label = "Operation Mode",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1yizgdx",
                                    key = "operation_mode",
                                    hide = "Field_1yizgdx",
                                    layout = Layout(row = "Row_1fi2zro", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
                                    label = "Submit to Approve",
                                    value = "submit_to_approve"
                                )),
                                isMulti = true ,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            )),
                        repeatable= true,
                        logics = null
                    ),


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
                                id = "Field_0d9qry75",
                                hide = null,
                                type = "fileupload",
                                label = "Attachment",
                                layout = LayoutDomain(row = "Row_021wo56", columns = null),
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            )
                            ),
                        repeatable= false,
                        logics = null
                    ),
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
                                id = "Field_0d9qry75",
                                hide = null,
                                type = "fileupload",
                                label = "Attachment",
                                layout = LayoutDomain(row = "Row_021wo56", columns = null),
                                subType = null,
                                validate = null,
                                values = null,
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            )
                        ),
                        repeatable= true,
                        logics = null
                    ),


                    ComponentDomain(
                        id = "Field_127fkjr",
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
                                id = "Field_1yizgdx",
                                hide = null,
                                type = "radio",
                                label = "Operation Mode",
                                layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
                                subType = null,
                                validate = ValidateDomain(
                                    id = "Field_1yizgdx",
                                    key = "operation_mode",
                                    hide = "Field_1yizgdx",
                                    layout = Layout(row = "Row_1fi2zro", columns = null),
                                    subtype = null,
                                    required = false
                                ),
                                values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
                                    label = "Submit to Approve",
                                    value = "submit_to_approve"
                                )),
                                conditional = ConditionalDomain(string = null),
                                components = null,
                                logics = null
                            )),
                        repeatable= true,
                        logics = null
                    ),



                ),conditional = ConditionalDomain(string = null), schemaVersion = 11)
        )




        return initial
    }
}