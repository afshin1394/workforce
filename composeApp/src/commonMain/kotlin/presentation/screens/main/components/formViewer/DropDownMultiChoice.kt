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
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import presentation.theme.strokeDefaultDark
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDark
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary


@Composable
fun DropDownMultiChoice(
    readOnly: Boolean,
    disable: Boolean,
    showErrorMessageValidation:Boolean,
    processLogicDomain: ProcessLogicDomain,
    titleDropDown: String,
    errorMessage: ResourceFormattedStringDesc,
    searchText: String,
    selectItem: String,
    itemList: List<ValueDomain>,
    onItemSelected: (List<ValueDomain>, ValueDomain?) -> Unit,
    onSearchButtonClicked: (query: String) -> Unit
) {

    val disableLogic = processLogicDomain.disabled ||disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage
    val backgroundColor = if (errorMessage.localized() != "" || validateLogic) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        surfaceBrandDark
    }

    var expanded by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var searchedText by remember { mutableStateOf(searchText) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    val selectedItems = remember {   mutableStateListOf<ValueDomain>().apply { addAll(itemList.filter { it.isSelected }) } }
    LaunchedEffect(hideLogic){
        if(hideLogic){

            selectedItems.clear()

        }
    }

    if (!hideLogic) {
        Column(Modifier.padding(16.dp)) {

            val styledString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                        fontSize = 14.sp
                    )
                ) {
                    append(titleDropDown)
                }
                if (requiredLogic ||errorMessage.localized() != "") {
                    withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                        append(" *")
                    }
                }
            }
            Text(
                text = styledString,
                style = TextStyle(color = textSecondary, fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            )

            Spacer(modifier = Modifier.height(12.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .border(
                        width = 1.dp,
                        color = if (errorMessage.localized() != "") Color.Red else strokeDefaultLight,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .background(color = White, shape = RoundedCornerShape(15.dp))
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    }
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (selectedItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(color = White, shape = RoundedCornerShape(15.dp))
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow {
                            items(selectedItems) { selectedItem ->
                                Surface(
                                    modifier = Modifier.padding(all = 5.dp),
                                    tonalElevation = 3.dp,
                                    shape = MaterialTheme.shapes.medium,
                                    color = subtleDefault
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .toggleable(
                                            value = false,
                                            onValueChange = {}
                                        )
                                    ) {
                                        Text(
                                            text = selectedItem.label!!,
                                            modifier = Modifier.padding(all = 8.dp).clickable {
                                                if (!(disableLogic || readOnlyLogic)) {
                                                    expanded = !expanded
                                                    searchedText = ""
                                                }
                                            },
                                            style = TextStyle(color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {

                        Text(
                            text = "",
                            style = TextStyle(
                                color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.clickable {
                                if (!(disableLogic || readOnlyLogic)) {
                                    expanded = !expanded
                                    searchedText = ""
                                }
                            }
                                .weight(1f)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(color = Color.Transparent)
                                .padding(19.dp),
                            color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary
                        )
                    }


                Spacer(modifier = Modifier.padding(2.dp))

                Icon(
                    painter = painterResource(MR.images.close),
                    tint = backgroundColor,
                    contentDescription = "deleteAllSelected",
                    modifier = Modifier
                        .width(26.dp)
                        .height(26.dp)
                        .padding(end = 4.dp)
                        .clickable {
                            selectedItems.clear()
                            itemList.forEach { it.isSelected = false }
                            onItemSelected(selectedItems, null)

                        }
                )

                Spacer(modifier = Modifier.padding(2.dp))
                Icon(
                    icon,
                    contentDescription = "contentDescription",
                    modifier = Modifier.width(34.dp).height(34.dp).padding(end = 10.dp)
                        .clickable {
                            if (!(disableLogic || readOnlyLogic)) {
                                expanded = !expanded
                                searchedText = ""
                            }
                        },
                    tint = backgroundColor,
                )


            }

            if (errorMessage.localized() != "" && !showErrorMessageValidation) {
                Text(
                    text = errorMessage.localized(),
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (validateLogic) {
                errorMessageValidateLogic?.let {
                    Text(
                        text = errorMessageValidateLogic.localized(),
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 12.dp))




            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(White)
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 8.dp, start = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {


                    TextField(
                        value = searchedText,
                        onValueChange = {
                            searchedText = it
                            onSearchButtonClicked(searchedText)
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .clickable {
                            }
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 14.sp
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(MR.images.search),
                                contentDescription = "", Modifier.clickable {
                                    onSearchButtonClicked(searchedText)
                                },
                                tint = textSecondary
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(MR.strings.search), style = TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 10.sp,

                                    color = strokeDefaultDark,
                                    textAlign = TextAlign.Center,
                                ), modifier = Modifier
                                    .clickable {

                                    }


                            )

                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.Transparent,
                            disabledIndicatorColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.Transparent,
                            unfocusedIndicatorColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.Transparent,
                            focusedContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White,
                            unfocusedContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White,
                            disabledContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White
                        ),

                        )

                }
                Spacer(modifier = Modifier.padding(top = spacing15X))


                val filteredList = itemList.filter { lable ->
                    lable.label!!.trim().contains(searchedText)
                }
                filteredList.forEach { label ->

                    val isSelected = selectedItems.contains(label)
                    DropdownMenuItemCustom(
                        modifier = Modifier,
                        interactionSource = remember { MutableInteractionSource() }
                            .also {
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        if (isSelected) {
                                            selectedItems.remove(label)
                                        } else {
                                            selectedItems.add(label)
                                        }
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = isSelected, colors = CheckboxDefaults.colors(
                                        checkedColor = surfaceBrandDefault,
                                        uncheckedColor = strokeDefaultLight
                                    ), onCheckedChange = { checked_ ->

                                        if (isSelected) {
                                            selectedItems.remove(label)
                                            onItemSelected(selectedItems, label)
                                        } else {
                                            //expanded = false
                                            selectedItems.add(label)
                                            onItemSelected(selectedItems, label)
                                        }
                                    })
                                    Text(
                                        text = label.label!!,
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
