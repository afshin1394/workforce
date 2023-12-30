package com.irancell.nwg.wfm.presentation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State

data class View(
    val hasAppBar: Boolean?,
    val hasSearch: Boolean?,
    val hasAvailability: Boolean?,
    val searchState: String,
    val content: @Composable () -> Unit
)
