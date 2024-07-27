@file:OptIn(ExperimentalMaterialApi::class)

package presentation.screens.main.compose


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi


import androidx.compose.material.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBar
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.ImageRowComponent
import presentation.model.BottomSheetDoubleActionModel
import presentation.screens.main.components.formViewer.DropDownMultiChoice
import presentation.screens.main.components.formViewer.Editable
import presentation.screens.main.components.formViewer.ModalDatePicker
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.ModalTimePicker
import presentation.screens.main.components.formViewer.TypeEditable
import presentation.screens.main.components.formViewer.UploadFileComponent
import presentation.screens.main.viewmodel.FormViewerScreenVM
import presentation.screens.ticket_process.events.ImageEvent
import presentation.theme.body_small
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary


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
                            BottomSheetDoubleActionModel(
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

                    ImageRowComponent(itemsList = emptyList(), modifier = Modifier.padding(16.dp).fillMaxWidth(), onCameraClick =  {
                       viewModel.openCamera()
                    }, onImageClick =  {
                       viewModel.openPreview(it)
                    })
                }

            }, onBackPressed = {
               // navigator.pop()
            }

        )


    }

}


