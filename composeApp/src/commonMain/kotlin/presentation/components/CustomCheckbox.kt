package com.irancell.nwg.wfm.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text


import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import presentation.model.StateFilter
import com.irancell.nwg.wfm.presentation.theme.*
import presentation.theme.body_large
import presentation.theme.strokeDefaultDark
import presentation.theme.surfaceBrandDefault


@Composable
fun CustomCheckbox(stateFilter: StateFilter = StateFilter(1, "Level 1", false), updateStateFilter: (stateFilter: StateFilter) -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = spacing05X)) {
        Checkbox(
            checked = stateFilter.isActiveState,
            colors = CheckboxDefaults.colors(checkedColor = surfaceBrandDefault, uncheckedColor = strokeDefaultDark),
            onCheckedChange = { checked ->
                stateFilter.isActiveState = checked
                stateFilter.isActive = checked
                updateStateFilter(stateFilter)
            }
        )
        Spacer(modifier = Modifier.width(spacing1X))
        Text(
            text = stateFilter.title,
            style = body_large
        )
    }
}






