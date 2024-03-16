package utils

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.components.CustomCheckbox
import domain.models.initialForm.ComponentDomain
import presentation.screens.main.components.formViewer.CheckList
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.Editable
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.Radio
import presentation.screens.main.components.formViewer.TypeEditable



    @Composable
    fun initialize(modifier: Modifier,components : List<ComponentDomain>){
        LazyColumn(modifier = modifier) {
            items(components) { it ->
                when(it.type){
                    FormViewerTypes.Group->{
                        it.components?.let {
                            initialize(modifier.heightIn(0.dp,500.dp),it)
                        }
                    }
                    FormViewerTypes.Checklist->{
                        val componentLabel = it.label
                        it.values?.let {
                            CheckList(componentLabel.toString(), it) {

                            }
                        }
                    }
                    FormViewerTypes.Datetime->{
                        ModalDateTimePicker(it.label.toString(),it.label.toString(),{},{})

                    }
                    FormViewerTypes.Email->{

                    }
                    FormViewerTypes.FileUpload->{

                    }
                    FormViewerTypes.GridField->{

                    }
                    FormViewerTypes.ImageView->{

                    }
                    FormViewerTypes.LatLong->{

                    }
                    FormViewerTypes.Radio->{

                        val componentLabel = it.label
                        it.values?.let {
                            Radio(componentLabel.toString(), it,it[0].label?:"") {

                            }
                        }
                    }
                    FormViewerTypes.Phone->{

                    }
                    FormViewerTypes.TextField->{
                        Editable(TypeEditable.LONG_TEXT,"", ImeAction.None, keyboardType =  KeyboardType.Text, readOnly =  false, maxLines =  4){

                        }

                    }
                    FormViewerTypes.Select->{
                        DropDownSingleChoice(it.label.toString(),it.values?: arrayListOf(),"","",{},{})
                    }

                }
            }
        }
}
class FormViewer {

    companion object{
        @Composable
        fun initialize(modifier: Modifier,components : List<ComponentDomain>){
            LazyColumn(modifier = modifier) {
                items(components) { it ->
                    when(it.type){
                        FormViewerTypes.Group->{
                            it.components?.let {
                                initialize(modifier.heightIn(0.dp,500.dp),it)
                            }
                        }
                        FormViewerTypes.Checklist->{
                            val componentLabel = it.label
                            it.values?.let {
                                CheckList(componentLabel.toString(), it) {

                                }
                            }
                        }
                        FormViewerTypes.Datetime->{
                            ModalDateTimePicker(it.label.toString(),it.label.toString(),{},{})

                        }
                        FormViewerTypes.Email->{

                        }
                        FormViewerTypes.FileUpload->{

                        }
                        FormViewerTypes.GridField->{

                        }
                        FormViewerTypes.ImageView->{

                        }
                        FormViewerTypes.LatLong->{

                        }
                        FormViewerTypes.Radio->{

                            val componentLabel = it.label
                            it.values?.let {
                                Radio(componentLabel.toString(), it,it[0].label?:"") {

                                }
                            }
                        }
                        FormViewerTypes.Phone->{

                        }
                        FormViewerTypes.TextField->{
                            Editable(TypeEditable.LONG_TEXT,"", ImeAction.None, keyboardType =  KeyboardType.Text, readOnly =  false, maxLines =  4){

                            }

                        }
                        FormViewerTypes.Select->{
                           DropDownSingleChoice(it.label.toString(),it.values?: arrayListOf(),"","",{},{})
                        }

                    }
                }
            }

        }
    }
}