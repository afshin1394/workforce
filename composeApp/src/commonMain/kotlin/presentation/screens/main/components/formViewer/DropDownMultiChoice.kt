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
import androidx.compose.material3.rememberModalBottomSheetState
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import dev.icerock.moko.resources.compose.stringResource
import presentation.theme.strokeDefaultDark
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary


@Composable
fun DropDownMultiChoice(
    titleDropDown: String,
    searchText: String,
    itemList: List<String>,
    onItemSelected: (List<String>) -> Unit,
    onSearchButtonClicked: (query: String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<String>() }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var searchedText by remember { mutableStateOf(searchText) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(16.dp)) {


        Row(

            modifier = Modifier
                .fillMaxWidth(1f)
                .border(width = 1.dp, color = strokeDefaultLight, shape = RoundedCornerShape(15.dp))
                .background(color = White, shape = RoundedCornerShape(15.dp))
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                }
                .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically
        ) {


            if (selectedItems.isNotEmpty()) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(color = White, shape = RoundedCornerShape(15.dp))
                        .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically
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
                                    modifier = Modifier.toggleable(
                                        value = false,
                                        onValueChange = {}
                                    )
                                ) {
                                    Text(
                                        text = selectedItem,
                                        modifier = Modifier.padding(all = 8.dp),
                                        style = TextStyle(color = textSecondary)
                                    )
                                }
                            }
                        }
                    }

                }


            } else {
                TextField(
                    value = titleDropDown,
                    textStyle = TextStyle(color = textSecondary),
                    shape = RoundedCornerShape(15.dp),
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,


                        ),

                    )

            }


            Spacer(modifier = Modifier.padding(2.dp))

            Icon(
                painter = painterResource(MR.images.close),
                tint = textSecondary,
                contentDescription = "deleteAllSelected",
                modifier = Modifier.width(26.dp).height(26.dp).padding(end = 4.dp).clickable {

                    selectedItems.clear()

                })

            Spacer(modifier = Modifier.padding(2.dp))
            Icon(
                icon,
                contentDescription = "contentDescription",
                modifier = Modifier.width(34.dp).height(34.dp).padding(end = 10.dp)
                    .clickable {
                        expanded = !expanded
                        searchedText = ""
                    },
                tint = textSecondary,
            )


        }



        Spacer(modifier = Modifier.padding(top = 12.dp))




        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
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



            val filteredList = itemList.filter { lable->
                lable.lowercase().trim().contains(searchedText.lowercase())
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
                                    } else {
                                        //expanded = false
                                        selectedItems.add(label)
                                        onItemSelected(selectedItems)
                                    }
                                })
                                Text(text = label, style = TextStyle(color = textSecondary))
                            }

                        }
                )
            }
        }

    }
}
