package data.network.response.task.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InitOperator(
    @SerialName("title")
    val title: String? = null,
    @SerialName("icon")
    val icon: String,
    @SerialName("symbol")
    val symbol: String? = null)