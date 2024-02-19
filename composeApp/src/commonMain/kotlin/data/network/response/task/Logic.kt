package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Logic(val logicType : String?= null,val experssions : List<Expression>?= null)
