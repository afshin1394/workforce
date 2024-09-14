package com.irancell.nwg.wfm.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.model.StateFilter
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.body_large_strong


@Composable
fun CustomFilterSection(
    modifier: Modifier = Modifier,
    filterSectionItem: FilterSectionItem,
    onSelect: (stateFilter: StateFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val chevronIcon = if (expanded) MR.images.chevron_up else MR.images.chevron_down
    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = spacing15X)
                .clickable {
                    expanded = !expanded
                }
        ) {
            androidx.compose.material3.Text(
                text = filterSectionItem.title,
                style = body_large_strong,
                modifier = modifier.weight(.9f)
            )
            Image(
                painter = painterResource(chevronIcon),
                contentDescription = "chevron_up",
                modifier = modifier.weight(.1f)
            )
        }
        AnimatedVisibility(
            visible = expanded,
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 500.dp)
            ) {
                items(filterSectionItem.filterStates) { filter ->
                    CustomCheckbox(filter) { updatedFilter ->
                        onSelect(updatedFilter)
                    }
                }
            }
        }
    }
}



@Composable
fun CustomFilterSectionPreview(filterSectionItems: MutableList<FilterSectionItem>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        filterSectionItems.forEach { filterSectionItem ->

            CustomFilterSection(
                filterSectionItem = filterSectionItem, modifier = Modifier.padding(
                    horizontal = spacing1X
                )
            ) { stateFilter ->

                filterSectionItem.filterStates.forEach { it ->

                    if (it.id != stateFilter.id) {
                        it.isActive = false
                        it.isActiveState = false
                    }
                }

            }
        }
    }
}


data class FilterSectionItem(val title: String, val filterStates: ArrayList<StateFilter>)
