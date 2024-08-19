@file:Suppress("UNCHECKED_CAST")

package presentation.screens.main.components.formViewer

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import irancell.nwg.wfm.MR
import kotlinx.coroutines.flow.MutableStateFlow
import utils.initialize


@Composable
fun groupComponent(
    taskID:String,
    modifier: Modifier,
    photoDomainList: MutableList<PhotoDomain>,
    onChanges: (list:List<ComponentDomain>, listValueDomain:List<ValueDomain>, indexParent: List<Int>, indexChild: Int) -> Unit,
    onAddItem: (componentDomain:ComponentDomain, listValueDomain:List<ValueDomain>, indexParent: List<Int>, indexChild: Int) -> Unit,
    onRemoveItem: (componentDomain:ComponentDomain, listValueDomain:List<ValueDomain>, indexParent: List<Int>, indexChild: Int) -> Unit,
    onFixChanges: (text : MutableStateFlow<String>) -> Unit,
    onClickImage:(indexPhotoSelected:Int,componentId:String)->Unit,
    currentParentIndex: List<Int> = listOf(),
    item : ComponentDomain,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit) {




    Column(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
    ) {

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (item.repeatable) {
                if (!item.removable) {
                    IconButton(onClick = onAddClick) {
                        Icon(painter = painterResource(MR.images.add), contentDescription = "Add")
                    }
                } else {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            painter = painterResource(MR.images.delete),
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
        item.components?.let {
            initialize(taskID,modifier.heightIn(0.dp, 1000.dp),photoDomainList,
                it,
                onChanges as (List<ComponentDomain>, List<ValueDomain>?, List<Int>, Int) -> Unit,
                onAddItem  as (ComponentDomain, List<ValueDomain>?, List<Int>, Int) -> Unit,
                onRemoveItem  as (ComponentDomain, List<ValueDomain>?, List<Int>, Int) -> Unit,
                onFixChanges,
                onClickImage, currentParentIndex)
        }
    }

}


