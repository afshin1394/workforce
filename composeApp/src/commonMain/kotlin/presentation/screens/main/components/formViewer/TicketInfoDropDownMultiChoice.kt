

package presentation.screens.main.components.formViewer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.InitProcessLogicDomain
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import presentation.theme.iconPrimary
import presentation.theme.strokeDefaultDark
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDark
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.surfaceDefaultLight
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary


@Composable
fun TicketInfoDropDownMultiChoice(
    readOnly: Boolean,
    disable: Boolean,
    processLogicDomain: InitProcessLogicDomain,
    titleDropDown: String,
    searchText: String,
    itemList: List<String>,
    selectedItems: List<String>,
    onItemSelected: (List<String>, String?) -> Unit,
    onSearchButtonClicked: (query: String) -> Unit
) {
    val disableLogic = processLogicDomain.disabled || disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageLogic = processLogicDomain.errorMessage
    val hasInitialMessageLogic = processLogicDomain.hasInitialMessage

    val backgroundColor = if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        surfaceDefaultLight
    }

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var searchedText by remember { mutableStateOf(searchText) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    val selectedState = remember { mutableStateListOf<String>().apply { addAll(selectedItems) } }

    LaunchedEffect(hideLogic) {
        if (hideLogic) selectedState.clear()
    }

    if (!hideLogic) {
        Column(Modifier.padding(16.dp)) {
            val styledString = buildAnnotatedString {
                withStyle(SpanStyle(
                    color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                    fontSize = 14.sp
                )) {
                    append(titleDropDown)
                }
                if (requiredLogic || validateLogic) {
                    withStyle(SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                        append(" *")
                    }
                }
            }

            Text(text = styledString, style = TextStyle(color = textSecondary, fontSize = 14.sp))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, backgroundColor, RoundedCornerShape(15.dp))
                    .background(White, RoundedCornerShape(15.dp))
                    .onGloballyPositioned { textFieldSize = it.size.toSize() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedState.isNotEmpty()) {
                    LazyRow(modifier = Modifier.weight(1f).height(56.dp)) {
                        items(selectedState) { selected ->
                            Surface(
                                modifier = Modifier.padding(5.dp),
                                tonalElevation = 3.dp,
                                shape = MaterialTheme.shapes.medium,
                                color = subtleDefault
                            ) {
                                Text(
                                    text = selected,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            if (!(disableLogic || readOnlyLogic)) {
                                                expanded = !expanded
                                                searchedText = ""
                                            }
                                        },
                                    style = TextStyle(
                                        color = if (disableLogic || readOnlyLogic)
                                            textInverseDisabled else textSecondary
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "",
                        modifier = Modifier
                            .weight(1f)
                            .padding(19.dp)
                            .clickable {
                                if (!(disableLogic || readOnlyLogic)) {
                                    expanded = !expanded
                                    searchedText = ""
                                }
                            },
                        style = TextStyle(
                            color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                            fontSize = 14.sp
                        )
                    )
                }

                Icon(
                    painter = painterResource(MR.images.close),
                    tint = iconPrimary,
                    contentDescription = "deleteAllSelected",
                    modifier = Modifier
                        .width(26.dp)
                        .height(26.dp)
                        .padding(end = 4.dp)
                        .clickable {
                            selectedState.clear()
                            onItemSelected(selectedState, null)
                        }
                )

                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier
                        .width(34.dp)
                        .height(34.dp)
                        .padding(end = 10.dp)
                        .clickable {
                            if (!(disableLogic || readOnlyLogic)) {
                                expanded = !expanded
                                searchedText = ""
                            }
                        },
                    tint = iconPrimary
                )
            }

            if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
                errorMessageLogic?.localized()?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(White)
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchedText,
                        onValueChange = {
                            searchedText = it
                            onSearchButtonClicked(it)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(MR.images.search),
                                contentDescription = null,
                                tint = textSecondary
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(MR.strings.search),
                                style = TextStyle(fontSize = 14.sp, color = strokeDefaultDark)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.padding(top = 12.dp))

                val filteredList = itemList.filter {
                    it.contains(searchedText, ignoreCase = true)
                }

                filteredList.forEach { label ->
                    val isSelected = selectedState.contains(label)
                    DropdownMenuItemCustom(
                        modifier = Modifier,
                        interactionSource = remember { MutableInteractionSource() }
                            .also {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) {
                                               // selectedItems.remove(label)
                                            } else {
                                              //  selectedItems.add(label)
                                            }
                                            onItemSelected(selectedItems.toList(), label)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked_ ->
                                            if (checked_) {
                                                if (!selectedItems.contains(label)) {
                                                  //  selectedItems.add(label)
                                                }
                                            } else {
                                               // selectedItems.remove(label)
                                            }
                                            onItemSelected(selectedItems.toList(), label)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = surfaceBrandDefault,
                                            uncheckedColor = strokeDefaultLight
                                        )
                                    )
                                    Text(
                                        text = label,
                                        style = TextStyle(color = textSecondary)
                                    )
                                }
                            }
                    )
                }
            }
        }
    }
}
