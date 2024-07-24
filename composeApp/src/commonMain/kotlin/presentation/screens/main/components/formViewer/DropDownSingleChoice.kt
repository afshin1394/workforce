package presentation.screens.main.components.formViewer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import irancell.nwg.wfm.MR
import presentation.theme.strokeDefaultDark
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDark
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary

@Composable
fun DropDownSingleChoice(
    processLogicDomain: ProcessLogicDomain,
    titleDropDown: String,
    errorMessage: ResourceFormattedStringDesc,
    itemList: List<ValueDomain>,
    selectItem: String,
    searchText: String,
    onItemSelected: (selectItem: String) -> Unit,
    onSearchButtonClicked: (query: String) -> Unit
) {
    val disableLogic = processLogicDomain.disabled
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage
    val backgroundColor = if (errorMessage.localized() != "" || validateLogic){
        Color.Red
    }else if(readOnlyLogic || disableLogic){
        surfaceBrandDisabled
    }else{
        surfaceBrandDark
    }

    var expanded by remember { mutableStateOf(false) }

    var selectedText  by remember { mutableStateOf(selectItem) }
    var searchedText by remember { mutableStateOf(searchText) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown
    println("recomposeeee ${"SingleChoice"}")
    if(!hideLogic) {
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
                if (requiredLogic) {
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


            TextField(value = selectedText,
                textStyle = TextStyle(color = if(disableLogic || readOnlyLogic) textInverseDisabled else textSecondary ),
                onValueChange = { selectedText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (errorMessage.localized() != "") Color.Red else strokeDefaultLight,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                readOnly = true,
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor =  Color.White,
                    disabledContainerColor = Color.White
                ),

                trailingIcon = {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(MR.images.close),
                            "deleteAllSelected",
                            Modifier.width(28.dp).height(28.dp).padding(end = 8.dp)
                                .clickable { selectedText = "" },
                            tint = backgroundColor
                        )
                        Icon(
                            icon,
                            "contentDescription",
                            Modifier.padding(end = 8.dp).clickable {
                                if(!(disableLogic || readOnlyLogic)) {
                                    expanded = !expanded
                                    searchedText = ""
                                }
                            },
                            tint = backgroundColor
                        )

                    }
                })

            if (errorMessage.localized() != "") {
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


            Spacer(modifier = Modifier.padding(top = spacing05X))

            MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(10.dp))) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = strokeDefaultLight,
                        shape = RoundedCornerShape(10.dp)
                    ).background(Color.White)
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
                                onSearchButtonClicked(it)
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .fillMaxWidth()
                                .clickable {

                                }
                                .border(
                                    width = 1.dp,
                                    color = strokeDefaultLight,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            textStyle = TextStyle(
                                color = textSecondary,
                                fontSize = 14.sp
                            ),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(MR.images.search),
                                    contentDescription = "", Modifier.clickable {
                                        onSearchButtonClicked(selectedText)
                                    },
                                    tint = textSecondary
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(MR.strings.search), style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,

                                        fontWeight = FontWeight(400),
                                        color = strokeDefaultDark,
                                        textAlign = TextAlign.Right,
                                    ), modifier = Modifier
                                        .padding(top = 0.dp, bottom = 0.dp)
                                        .height(45.dp)
                                        .padding(top = 0.dp, bottom = 0.dp)
                                )

                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White

                            ),

                            )

                    }
                    Spacer(modifier = Modifier.padding(top = spacing15X))

                    val filteredList = itemList.filter { lable ->
                        lable.label?.trim()!!.contains(searchedText)
                    }

                    filteredList.forEach { label ->
                        val isSelected = selectedText == label.label

                        DropdownMenuItemCustom(
                            modifier = Modifier,
                            interactionSource = remember { MutableInteractionSource() }
                                .also {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                            .clickable { selectedText = label.label ?: "" },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        RadioButton(
                                            selected = isSelected,
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = surfaceBrandDefault
                                            ),
                                            onClick = {
                                                selectedText = label.label ?: ""
                                                expanded = false
                                                onItemSelected(label.label ?: "")
                                            }
                                        )

                                        Text(
                                            text = label.label ?: "",
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
}