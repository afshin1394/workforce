@file:OptIn(ExperimentalMaterialApi::class)

package presentation.screens.main.compose


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi


import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBar
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.BottomSheetActionModel
import presentation.screens.main.viewmodel.FormViewerScreenVM
import presentation.screens.ticket_process.events.ImageEvent
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary
import utils.convertToZip


@OptIn(ExperimentalMaterialApi::class)
class FormViewerScreen() : Screen {


    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()


        val items = listOf(
            "Item1",
            "Item2",
            "Item3",
            "Item4",

            )
        var selectedItem by remember { mutableStateOf("") }
        val viewModel: FormViewerScreenVM = koinInject()
        val imageState = viewModel.imageEvent.collectAsState()




        BaseScreen(
            viewModel = viewModel,

            title = stringResource(MR.strings.form),
            scaffoldState = scaffoldState,
            topBar = {

            },

            bottomSheetTitle = when (imageState.value) {
                ImageEvent.Default -> {
                    ""
                }
                ImageEvent.DeletePhoto -> {
                    stringResource(MR.strings.delete_photo)

                }
                ImageEvent.EditPhoto -> {
                    stringResource(MR.strings.edit_photo)

                }
                ImageEvent.OpenCamera -> {
                    ""

                }
                ImageEvent.PhotoPreview -> {
                    stringResource(MR.strings.photo_preview)

                }
            }


            ,

            bottomSheetContent = {
                when(imageState.value){
                    ImageEvent.Default -> {
                    }
                    ImageEvent.DeletePhoto -> {
                        bottomSheetDoubleActionBottomBar(
                            BottomSheetActionModel(
                                stringResource(MR.strings.cancel),
                                surfaceDefault,
                                textPrimary,
                                stringResource(MR.strings.delete),
                                Color.Red,
                                textInverse
                            ), onFirstButtonClick = {
//                                viewModel.openPreview()
                            }, onSecondButtonClick = {
//                                viewModel.updateDeletedPhoto(
//                                    atta,
//                                    positionSelectedPhotoForEdit
//                                )


                            })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                    ImageEvent.EditPhoto -> {

                    }
                    ImageEvent.OpenCamera -> {

                    }
                    ImageEvent.PhotoPreview -> {

                    }
                }


            }, onCloseBottomSheet = {

            },


            content = {

             if (imageState.value is ImageEvent.OpenCamera){

             }
                Column(
                    modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Button(
                        onClick = {


                                  viewModel.getPhotoByComponentKey()

                          //  println("checkArry      ${ viewModel.getFileList()}")

                        },
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(containerColor = surfaceBrandDefault),
                        modifier = Modifier.fillMaxHeight().padding( 32.dp).height(48.dp).width(100.dp)

                    ) {
                        Text(
                            text = "convert to uri",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    }




                    Button(
                        onClick = {



                          viewModel.callApiUpload(convertToZip(viewModel.uriList,"testtt","--workorder_13-20240731-00002"))

                        },
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(containerColor = surfaceBrandDefault),
                        modifier = Modifier.fillMaxHeight().padding( 32.dp).height(48.dp).width(100.dp)

                    ) {
                        Text(
                            text = "convert to zip",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    }




//                    DropDownSingleChoice(
//                        titleDropDown = stringResource(MR.strings.select_an_item),
//                        itemList = items,
//                        selectItem = selectedItem,
//                        searchText = "",
//                        onItemSelected = {
//                            selectedItem = it
//                            println("selectSingleChoice>>>>${it}")
//                        },
//                        onSearchButtonClicked = {
//                            println("searchSingleChoice>>>>${it}")
//                        })

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

            /*        DropDownMultiChoice(
                        titleDropDown = stringResource(MR.strings.select_an_item),
                        searchText = "",
                        itemList = items,
                        onItemSelected = {

                        },
                        onSearchButtonClicked = {
                        })*/





/*                    ModalDateTimePicker(
                        stringResource(MR.strings.selected_date_time),
                        stringResource(MR.strings.date_time_picker),
                        onDateSelected = {

                        },
                        onTimeSelected = {})*/

//
//                    Radio(title=stringResource(MR.strings.select_an_item),itemList = items, selectItem = "", onItemSelected = {
//
//                        println("multiChoooice>>>>${it}")
//
//                    })

//                    CheckList(
//
//                        title=stringResource(MR.strings.choose_more),
//
//                        itemList = items,
//                        onItemSelected = {
//
//                            println("multiChoooice>>>>${it.size}")
//
//                        })

//                    Editable(TypeEditable.EMAIL, placeholder = "email", imeAction = ImeAction.Next, leadingIcon =null , trailingIcon =null, keyboardType = KeyboardType.Text, maxLines = 2, readOnly = false,onValueChange = {} )


                }

            }, onBackPressed = {
               // navigator.pop()
            }

        )


    }

}


