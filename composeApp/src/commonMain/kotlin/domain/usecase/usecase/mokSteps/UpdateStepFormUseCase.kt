package domain.usecase.usecase.mokSteps


import data.network.response.task.FormStruct
import domain.mappers.toComponent
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.FormStructDomain
import domain.models.steps.ActivityDomain
import domain.models.steps.FormDomain
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import presentation.screens.ticket_process.events.StepEvent
import toActivityDomain
import toActivityDomainList
import utils.PROCEED

data class StructureActivity(
    val stepCounter: Int,
    val stepTitle: String,
    val activityDomain: ActivityDomain,
    val stepDetails: List<StepDetail>,
)

data class StepDetail(val id: Int, val name: String)
class UpdateStepFormUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository
) : BaseUseCase<StructureActivity, Triple<String, String,List<ComponentDomain>>>() {

    override suspend fun run(params: Triple<String, String,List<ComponentDomain>>): StructureActivity {
        val stepList: List<ActivityDomain> =
            iStepsRepository.getStepsByTicketNumber(params.first).toActivityDomainList()

        val stepListSorted = stepList.sortedBy { it.id }
        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)

        if(params.second != PROCEED.INITIAL)
        iStepsRepository.updateFormStructure(params.first,stepPointerDomain.activeActivity, Json.encodeToString(FormStruct.serializer(), FormStruct(components =  (params.third.toComponent()))) )

        val stepDetails = stepListSorted.mapIndexed { int, step ->
            StepDetail(int, step.title)
        }
        var index =  stepListSorted.indexOfFirst { it.id == stepPointerDomain.activeActivity }
        if(index == -1) index = 0

        val nextIndex =
            if (params.second == PROCEED.INITIAL) {
                index
            } else if (params.second == PROCEED.NEXT) {
                if (index <= stepListSorted.size) {
                    index + 1
                }
                else {
                    index
                }
            } else if (params.second == PROCEED.PREVIOUS) {
                if (index > 0) {

                    index - 1
                }else {
                    index
                }
            } else {
                index
            }
        Napier.log(LogLevel.ASSERT,tag="nextIndex", message = nextIndex.toString())

        iStepPointerRepository.updateActiveActivity(
            ticketNumber = stepPointerDomain.ticketNumber,
            activeActivity = stepListSorted[nextIndex].id
        )


        return StructureActivity(
            nextIndex,
            stepListSorted.get(nextIndex).title,
            iStepsRepository.getDataByTicketNumberAndStep(
                stepPointerDomain.ticketNumber,
                stepListSorted.get(nextIndex).id
            ).toActivityDomain(),
            stepDetails,
        )

//        val stepForm=  listOf(
//            StepModel(
//                activityID = 1,
//                activityTitle = "Initial Setup",
//                isActive = true,
//                form_structure = FormStructDomain(
//                    id = "create_wo133", hide = null, type = "default", components = listOf(
//
//
//                        ComponentDomain(
//                            id = "Field_125fkjr",
//                            hide = null,
//                            type = "group",
//                            label ="",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//
//                                ComponentDomain(
//                                    id = "Field_1ka1qac33",
//                                    hide = null,
//                                    type ="textfield",
//                                    label =" Short Text",
//                                    layout = null,
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_1ka1qac33",required = true, maxLength = 12, minLength = 8),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//
//
//                                ComponentDomain(
//                                    id = "Field_0d9qry754",
//                                    hide = null,
//                                    type = "fileupload",
//                                    label = "Attachment",
//                                    layout = LayoutDomain(row = "Row_021wo56", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(id ="Field_0d9qry754", required = true, maxTotalSize = 1, maxFileNumber = 1, blacklistAttachment = "pdf,json", attachedValidationType = "Blacklist"),
//                                    values = null,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//                                ComponentDomain(
//                                    id = "Field_1xswkgr",
//                                    hide = null,
//                                    type = "image",
//                                    label = "Image view",
//                                    layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
//                                    values = null ,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//                            )
//
//                        ),
//
//
//
//                        ), conditional = ConditionalDomain(string = null), schemaVersion = 11
//                )
//            ),
//            StepModel(
//                activityID = 2,
//                activityTitle = "Review",
//                isActive = false,
//                form_structure =FormStructDomain(
//                    id = "create_wo126552", hide = null, type = "default", components = listOf(
//
//
//                        ComponentDomain(
//                            id = "Field_12744fkjr",
//                            hide = null,
//                            type = "group",
//                            label ="",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//
//                                ComponentDomain(
//                                    id = "Field_1sxaasq33333333",
//                                    hide = null,
//                                    type ="number",
//                                    label ="Number",
//                                    layout = null,
//                                    subType = null,
//                                    validate = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null),
//
//                                ComponentDomain(
//                                    id = "Field_09k11tu",
//                                    hide = null,
//                                    type ="phone",
//                                    label ="Phone",
//                                    layout = null,
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_09k11tu",required = true, validationType = "phone"),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null),
//
//                                ComponentDomain(
//                                    id = "Field_0lnr8u0",
//                                    hide = null,
//                                    type ="latlong",
//                                    label ="Lat & Long",
//                                    layout = null,
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_1sxaasq",required = true, max = 144, min = 44),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null),
//
//                                ComponentDomain(
//                                    id = "Field_1go3xue",
//                                    hide = null,
//                                    type ="textarea",
//                                    label ="Long Text",
//                                    layout = null,
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_1go3xue",required = true, maxLength = 12, minLength = 8),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null),
//
//                                ComponentDomain(
//                                    id = "Field_1o997cd",
//                                    hide = null,
//                                    type ="email",
//                                    label ="Email",
//                                    layout = null,
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_1ka1qac",required = true, domainList = "gmail", domainType = "Whitelist", validationType = "email"),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null),
//
//                                ComponentDomain(
//                                    id = "Field_1ka1qac",
//                                    hide = null,
//                                    type ="textfield",
//                                    label =" Short Text",
//                                    layout = null,
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_1ka1qac",required = true, maxLength = 12, minLength = 8),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null),
//                                ComponentDomain(
//                                    id = "Field_1ka1qac",
//                                    hide = null,
//                                    type = mapSubtypeToType("datetime"),
//                                    label =" Required Finish Time",
//                                    layout = null,
//                                    subType = "datetime",
//                                    validate =  ValidateDomain( id = "Field_1ka1qac",required = true),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,),
//                                ComponentDomain(
//                                    id = "Field_10siih0",
//                                    hide = null,
//                                    type = mapSubtypeToType("date"),
//                                    label =" Required Finish Time",
//                                    layout = null,
//                                    subType = "date",
//                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,),
//                                ComponentDomain(
//                                    id = "Field_10siih0",
//                                    hide = null,
//                                    type =mapSubtypeToType("time"),
//                                    label =" Required Finish Time",
//                                    layout = null,
//                                    subType = "time",
//                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1xswkgrrrr",
//                                    hide = null,
//                                    type = "image",
//                                    label = "Image view",
//                                    layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
//                                    subType = null,
//                                    validate =  ValidateDomain( id = "Field_10siih0",required = true),
//                                    values = null ,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_0tg5gkk",
//                                    hide = null,
//                                    type = "email",
//                                    label = "Email",
//                                    layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
//                                    subType = null,
//                                    validate = null,
//                                    values = null ,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1yizgdx44",
//                                    hide = null,
//                                    type = "checklist",
//                                    label = "Operation Mode",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = true
//                                    ),
//                                    values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
//                                        label = "Submit to Approve",
//                                        value = "submit_to_approve"
//                                    )),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_1yizgdx44",
//                                    hide = null,
//                                    type = "checklist",
//                                    label = "Operation Mode",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = false
//                                    ),
//                                    values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(label = "Submit to Approve", value = "submit_to_approve"), ValueDomain(label = "Testtt23", value = "Testtt23"), ValueDomain(label = "Testtt344", value = "Testtt344")),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_1yizgdx44",
//                                    hide = null,
//                                    type = "select",
//                                    label = "Operation Mode",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = true
//                                    ),
//                                    values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
//                                        label = "Submit to Approve",
//                                        value = "submit_to_approve"
//                                    )),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_1yizgdx44",
//                                    hide = null,
//                                    type = mapSubtypeToType("multi"),
//                                    label = "Operation Mode",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = "multi",
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = true
//                                    ),
//                                    values = listOf(ValueDomain(label = "Dispatch", value = "dispatch"), ValueDomain(
//                                        label = "Submit to Approve",
//                                        value = "submit_to_approve"
//                                    )),
//                                    isMulti = true ,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                )),
//                            repeatable= true,
//                            logics = null
//                        ),
//
//
//
//
//                        ), conditional = ConditionalDomain(string = null), schemaVersion = 11
//                )
//            ),
//            StepModel(
//                activityID = 3,
//                activityTitle = "Finalization",
//                isActive = false,
//                form_structure =FormStructDomain(
//                    id = "create_wo555", hide = null, type = "default", components = listOf(
//
//
//                        ComponentDomain(
//                            id = "Field_12744fkjr",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//
//                                ComponentDomain(
//                                    id = "Field_1sxaasq",
//                                    hide = null,
//                                    type = "number",
//                                    label = "Number",
//                                    layout = null,
//                                    subType = null,
//                                    validate = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "Field_09k11tu",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "1"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                        LogicDomain(
//                                            logicType = LogicType.ReadOnly,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "Field_09k11tu",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.GreaterThan,
//                                                                OperatorType.GreaterThan,
//                                                            ),
//                                                            value = "5"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "Field_09k11tu",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "23"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//
////                                    LogicDomain(
////                                        logicType = LogicType.Required,
////                                        experssions = listOf(
////                                            ExpressionDomain(
////                                                listOf(
////                                                    ConditionDomain(
////                                                        firstFieldKey = "Field_09k11tu",
//////                                                        firstOperator = OperatorDomain(
////
//////                                                            OperatorType.Equals,
//////                                                            OperatorType.Equals
//////                                                        )
////                                                        secondOperator = OperatorDomain(
////                                                            OperatorType.LessThan,
////                                                            OperatorType.LessThan,
////                                                        ),
////                                                        value = "-40"
////                                                    ),
////
////                                                    )
////                                            ),
////
////
////                                            )
////                                    )
//                                    ),
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_09k11tu",
//                                    hide = null,
//                                    type = "number",
//                                    label = "Number 1",
//                                    layout = null,
//                                    subType = null,
//                                    validate = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_0lnr8u0",
//                                    hide = null,
//                                    type = "number",
//                                    label = "Number 2",
//                                    layout = null,
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1sxaasq",
//                                        required = true,
//                                        max = 144,
//                                        min = 44
//                                    ),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1go3xue",
//                                    hide = null,
//                                    type = "number",
//                                    label = "Number 3",
//                                    layout = null,
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1go3xue",
//                                        required = true,
//                                        maxLength = 12,
//                                        minLength = 8
//                                    ),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Validate,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "Field_0lnr8u0",
//                                                            firstOperator = OperatorDomain(
//
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Add,
//                                                                OperatorType.Add,
//                                                            ),
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    )
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1o997cd",
//                                    hide = null,
//                                    type = "email",
//                                    label = "Email",
//                                    layout = null,
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1ka1qac",
//                                        required = true,
//                                        domainList = "gmail",
//                                        domainType = "Whitelist",
//                                        validationType = "email"
//                                    ),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1ka1qac",
//                                    hide = null,
//                                    type = "textfield",
//                                    label = " Short Text",
//                                    layout = null,
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1ka1qac",
//                                        required = true,
//                                        maxLength = 12,
//                                        minLength = 8
//                                    ),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics =
//                                    listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Hide,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "Field_1o997cd",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                ),
//                                ComponentDomain(
//                                    id = "Field_1ka1qac1",
//                                    hide = null,
//                                    type = mapSubtypeToType("time"),
//                                    label = " Required Finish Time(time logic)",
//                                    layout = null,
//                                    subType = "time",
//                                    validate = ValidateDomain(id = "Field_1ka1qac", required = true),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics =
//                                    listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "47",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                ),
//                                ComponentDomain(
//                                    id = "Field_0tg5gkk",
//                                    hide = null,
//                                    type = "number",
//                                    label = "number datetime logic",
//                                    layout = null,
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1ka1qac",
//                                        required = true,
//                                        domainList = "gmail",
//                                        domainType = "Whitelist",
//                                        validationType = "email"
//                                    ),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_10siih0",
//                                    hide = null,
//                                    type = mapSubtypeToType("date"),
//                                    label = " Required Finish Time",
//                                    layout = null,
//                                    subType = "date",
//                                    validate = ValidateDomain(id = "Field_10siih0", required = true),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//                                ),
//                                ComponentDomain(
//                                    id = "Field_10siih0",
//                                    hide = null,
//                                    type = mapSubtypeToType("time"),
//                                    label = " Required Finish Time",
//                                    layout = null,
//                                    subType = "time",
//                                    validate = ValidateDomain(id = "Field_10siih0", required = true),
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1xswkgr",
//                                    hide = null,
//                                    type = "image",
//                                    label = "Image view",
//                                    layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(id = "Field_10siih0", required = true),
//                                    values = null,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "47",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_0tg5gkk",
//                                    hide = null,
//                                    type = "email",
//                                    label = "Email",
//                                    layout = LayoutDomain(row = "Row_1d2cdj9", columns = null),
//                                    subType = null,
//                                    validate = null,
//                                    values = null,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//                                ComponentDomain(
//                                    id = "Field_1yizgdx44",
//                                    hide = null,
//                                    type = "checklist",
//                                    label = "Operation Mode (Required Logic)",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = true
//                                    ),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics =
//                                    listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "Field_0tg5gkk",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_1yizgdx44",
//                                    hide = null,
//                                    type = "checklist",
//                                    label = "Operation Mode 1",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = false
//                                    ),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        ),
//                                        ValueDomain(label = "Testtt23", value = "Testtt23"),
//                                        ValueDomain(label = "Testtt344", value = "Testtt344")
//                                    ),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_2123",
//                                    hide = null,
//                                    type = "select",
//                                    label = "Operation Mode 2",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = true
//                                    ),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "47",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                ),
//                                ComponentDomain(
//                                    id = "Field_1ytertret",
//                                    hide = null,
//                                    type = "select",
//                                    label = "Operation Mode 3",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = null,
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "47",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                ),
//
//
//                                ComponentDomain(
//                                    id = "Field_19990",
//                                    hide = null,
//                                    type = mapSubtypeToType("multi"),
//                                    label = "Operation Mode 4",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = "multi",
//                                    validate = ValidateDomain(
//                                        id = "Field_1yizgdx",
//                                        key = "operation_mode",
//                                        hide = "Field_1yizgdx",
//                                        layout = Layout(row = "Row_1fi2zro", columns = null),
//                                        subtype = null,
//                                        required = true
//                                    ),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//                                    isMulti = true,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "47",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    )
//                                )
//                            ),
//                            repeatable = true,
//                            logics = null
//                        ),
//
//
//                        ComponentDomain(
//                            id = "Field_125fkjr",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//
//                                ComponentDomain(
//                                    id = "Field_1ka1qac3367",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text1",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "Field_1ka1qac3644",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "Field_1ka1qac3465",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text3435566",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics =
//                                    listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "9",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        ),
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "12"
//                                                        ),
//
//                                                        )
//                                                ),
//                                            )
//                                        )
//                                    ),
//
//                                    ),
//
//
//                                ComponentDomain(
//                                    id = "Field_0d9qry754",
//                                    hide = null,
//                                    type = "fileupload",
//                                    label = "Attachment  with disable logic",
//                                    layout = LayoutDomain(row = "Row_021wo56", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_0d9qry754",
//                                        required = true,
//                                        maxTotalSize = 1,
//                                        maxFileNumber = 1,
//                                        blacklistAttachment = "pdf,json",
//                                        attachedValidationType = "Blacklist"
//                                    ),
//                                    values = null,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics =
//                                    listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "47",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        )
//                                    ),
//                                )
//                            ),
//                            repeatable = false,
//                            logics = null
//                        ),
//                        ComponentDomain(
//                            id = "Field_125fkjr",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "Field_0d9qry75",
//                                    hide = null,
//                                    type = "fileupload",
//                                    label = "Attachment",
//                                    layout = LayoutDomain(row = "Row_021wo56", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(
//                                        id = "Field_0d9qry75",
//                                        required = true,
//                                        maxTotalSize = 2,
//                                        maxFileNumber = 2,
//                                        blacklistAttachment = "pdf,json",
//                                        attachedValidationType = "Blacklist"
//                                    ),
//                                    values = null,
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                )
//                            ),
//                            repeatable = true,
//                            logics = null
//                        ),
//
//
//                        ComponentDomain(
//                            id = "Field_127fkjr",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "Field_1yizgdx",
//                                    hide = null,
//                                    type = "radio",
//                                    label = "Operation Mode (Validate Logic)",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(id = "Field_1yizgdx", required = true),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = null
//                                )
//                            ),
//                            repeatable = true,
//                            logics =
//                            listOf(
//                                LogicDomain(
//                                    logicType = LogicType.Validate,
//                                    experssions = listOf(
//                                        ExpressionDomain(
//                                            listOf(
//                                                ConditionDomain(
//                                                    firstFieldKey = "Field_1ka1qac3644",
//                                                    firstOperator = OperatorDomain(
//                                                        OperatorType.Equals,
//                                                        OperatorType.Equals
//                                                    ),
//                                                    secondOperator = OperatorDomain(
//                                                        OperatorType.Add,
//                                                        OperatorType.Add,
//                                                    ),
//                                                    value = "2"
//                                                ),
//
//                                                )
//                                        ),
//
//
//                                        )
//                                )
//                            ),
//                        ),
//
//                        ComponentDomain(
//                            id = "Field_127fkjr",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "Field_1yizgdx",
//                                    hide = null,
//                                    type = "radio",
//                                    label = "Operation Mode (Validate Logic458)",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(id = "Field_1yizgdx", required = true),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Hide,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "8",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "12"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    )
//                                )
//                            ),
//                            repeatable = true,
//                            logics =
//                            listOf(
//                                LogicDomain(
//                                    logicType = LogicType.Validate,
//                                    experssions = listOf(
//                                        ExpressionDomain(
//                                            listOf(
//                                                ConditionDomain(
//                                                    firstFieldKey = "Field_1ka1qac3644",
//                                                    firstOperator = OperatorDomain(
//                                                        OperatorType.Equals,
//                                                        OperatorType.Equals
//                                                    ),
//                                                    secondOperator = OperatorDomain(
//                                                        OperatorType.Add,
//                                                        OperatorType.Add,
//                                                    ),
//                                                    value = "2"
//                                                ),
//
//                                                )
//                                        ),
//
//
//                                        )
//                                )
//                            ),
//                        ),
//                        ComponentDomain(
//                            id = "7",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "8",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Required onkj)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "9",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "1"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "9",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Required)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "Field_1yizgdxuj",
//                                    hide = null,
//                                    type = "radio",
//                                    label = "Operation Mode (Validate Logic458)",
//                                    layout = LayoutDomain(row = "Row_1fi2zro", columns = null),
//                                    subType = null,
//                                    validate = ValidateDomain(id = "Field_1yizgdx", required = true),
//                                    values = listOf(
//                                        ValueDomain(label = "Dispatch", value = "dispatch"),
//                                        ValueDomain(
//                                            label = "Submit to Approve",
//                                            value = "submit_to_approve"
//                                        )
//                                    ),
//                                    conditional = ConditionalDomain(string = null),
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.ReadOnly,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "9",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "12"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    )
//                                )
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "10",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "11",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Required)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Required,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "12",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.LessThan,
//                                                                OperatorType.LessThan,
//                                                            ),
//                                                            value = "-40"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "12",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Required)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "13",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "14",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Hide)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Hide,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "15",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.LessThan,
//                                                                OperatorType.LessThan,
//                                                            ),
//                                                            value = "-20"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "15",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Hide)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "16",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "17",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Hide)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Hide,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "18",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Contains,
//                                                                OperatorType.Contains,
//                                                            ),
//                                                            value = "salam"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "18",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Hide)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//
//                        ComponentDomain(
//                            id = "19",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "20",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Disable)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "21",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "5"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "21",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Disable)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//
//                        ComponentDomain(
//                            id = "22",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "23",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Disable)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "24",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.GreaterThan,
//                                                                OperatorType.GreaterThan,
//                                                            ),
//                                                            value = "500"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "24",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Disable)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "25",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "26",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(ReadOnly)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.ReadOnly,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "27",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.GreaterThan,
//                                                                OperatorType.GreaterThan,
//                                                            ),
//                                                            value = "100"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "27",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(ReadOnly)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "28",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "29",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Readonly)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "30",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Contains,
//                                                                OperatorType.Contains,
//                                                            ),
//                                                            value = "afshin"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "30",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Readonly)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "31",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "32",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Validate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Validate,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "33",
//                                                            firstOperator = OperatorDomain(
//
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals
//                                                            ),
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Add,
//                                                                OperatorType.Add,
//                                                            ),
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "33",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Validate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "34",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "35",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Validate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Validate,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "36",
//                                                            firstOperator = OperatorDomain(
//
//                                                                OperatorType.NotEquals,
//                                                                OperatorType.NotEquals
//                                                            ),
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Multiply,
//                                                                OperatorType.Multiply,
//                                                            ),
//                                                            value = "4"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "36",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Validate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "37",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "38",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Calculate,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "39",
//                                                            secondFieldKey = "40",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Add,
//                                                                OperatorType.Add,
//                                                            ),
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "39",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "40",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "41",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "42",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.LessThan,
//                                                                OperatorType.LessThan,
//                                                            ),
//                                                            value = "-100"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//
//                                        LogicDomain(
//                                            logicType = LogicType.ReadOnly,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.GreaterThan,
//                                                                OperatorType.GreaterThan,
//                                                            ),
//                                                            value = "100"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                        LogicDomain(
//                                            logicType = LogicType.Calculate,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
//                                                            secondFieldKey = "43",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Divide,
//                                                                OperatorType.Divide,
//                                                            ),
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "43",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "44",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//
//
//                                )
//
//                        ),
//                        ComponentDomain(
//                            id = "45",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "46",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(t G) 1",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Hide,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Equals,
//                                                                OperatorType.Equals,
//                                                            ),
//                                                            value = "2"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "47",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(t G) 2",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                            )
//                        ),
//
//                        ComponentDomain(
//                            id = "41",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "42",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Disable,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.LessThan,
//                                                                OperatorType.LessThan,
//                                                            ),
//                                                            value = "-100"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//
//                                        LogicDomain(
//                                            logicType = LogicType.ReadOnly,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
////                                                        firstOperator = OperatorDomain(
//
////                                                            OperatorType.Equals,
////                                                            OperatorType.Equals
////                                                        )
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.GreaterThan,
//                                                                OperatorType.GreaterThan,
//                                                            ),
//                                                            value = "100"
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                        LogicDomain(
//                                            logicType = LogicType.Calculate,
//                                            experssions = listOf(
//                                                ExpressionDomain(
//                                                    listOf(
//                                                        ConditionDomain(
//                                                            firstFieldKey = "44",
//                                                            secondFieldKey = "43",
//                                                            secondOperator = OperatorDomain(
//                                                                OperatorType.Divide,
//                                                                OperatorType.Divide,
//                                                            ),
//                                                        ),
//
//                                                        )
//                                                ),
//
//
//                                                )
//                                        ),
//                                    ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "43",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "44",
//                                    hide = null,
//                                    type = "number",
//                                    label = " Short Text2(Calculate)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//
//
//                                )
//
//                        ),
//                        ComponentDomain(
//                            id = "48",
//                            hide = null,
//                            type = "group",
//                            label = "",
//                            layout = null,
//                            subType = null,
//                            validate = null,
//                            values = null,
//                            conditional = null,
//                            components = listOf(
//                                ComponentDomain(
//                                    id = "49",
//                                    hide = null,
//                                    type = "textfield",
//                                    label = " Short Text(bind)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = listOf(
//                                        LogicDomain(
//                                            logicType = LogicType.Bind,
//                                            bind_logic = BindLogicDomian(
//                                                field_options = listOf(
//                                                    FieldOptionDomain(field_key = "51"),
//                                                    FieldOptionDomain(field_key = "52"),
//                                                    FieldOptionDomain(field_key = "44")
//                                                )
//                                            )
//
//                                        ),
//
//                                        ),
//
//                                    ),
//                                ComponentDomain(
//                                    id = "51",
//                                    hide = null,
//                                    type = "textfield",
//                                    label = " Short Text2(bind)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//                                ComponentDomain(
//                                    id = "52",
//                                    hide = null,
//                                    type = "textfield",
//                                    label = " Short Text2(bind)",
//                                    layout = null,
//                                    subType = null,
//                                    values = null,
//                                    conditional = null,
//                                    components = null,
//                                    logics = null,
//
//                                    ),
//
//
//                                )
//
//                        ),
//                    ), conditional = ConditionalDomain(string = null), schemaVersion = 11
//                )
//            )
//
//        )

//        return stepForm
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