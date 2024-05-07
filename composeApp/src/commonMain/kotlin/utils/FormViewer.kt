package utils

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.components.CustomCheckbox
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValueDomain
import presentation.components.ImageRowComponent
import presentation.model.UploadFileModel
import presentation.screens.main.components.formViewer.CheckList
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.Editable
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.Radio
import presentation.screens.main.components.formViewer.TypeEditable
import presentation.screens.main.components.formViewer.UploadFileComponent


@Composable
    fun initialize(modifier: Modifier,components : List<ComponentDomain> ,onChanges:(List<ValueDomain>) -> Unit){
        LazyColumn(modifier = modifier) {
            items(components) { it ->
                when(it.type){
                    FormViewerTypes.Group->{
                        it.components?.let {
                            initialize(modifier.heightIn(0.dp,1000.dp),it,onChanges)
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
                        Editable(TypeEditable.EMAIL,"", ImeAction.None, keyboardType =  KeyboardType.Email, readOnly =  false, maxLines =  1){

                        }
                    }
                    FormViewerTypes.FileUpload->{

                        val uploadDomainList = remember { mutableStateOf(it.values ?: emptyList()) }

                        UploadFileComponent(
                            modifier = Modifier,
                            uploadList = uploadDomainList.value,
                            onSelected = { valueDomain ->
                                val oldList = uploadDomainList.value
                                val newValues = valueDomain.map {valueMap->
                                    ValueDomain("${it.id}/${valueMap.label}", valueMap.value)
                                }
                                val newList = (oldList + newValues).distinct()
                                uploadDomainList.value = newList
                                it.values=newList
                            }
                        )


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
                        Editable(TypeEditable.PHONE,"", ImeAction.None, keyboardType =  KeyboardType.Phone, readOnly =  false, maxLines =  1){

                        }

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

    Spacer(modifier = Modifier.height(100.dp))

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