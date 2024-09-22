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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.flow.MutableStateFlow
import utils.initialize


@Composable
fun groupComponent(
    isChild:Boolean,
    scrollingState : Pair<Int,Int>,
    parentIndex: Int,
    savedIndex: Int,
    taskID: String,
    modifier: Modifier,
    photoDomainList: MutableList<PhotoDomain>,
    onChanges: (componentDomain: ComponentDomain, listValueDomain: List<ValueDomain>) -> Unit,
    onAddItem: (componentDomain: ComponentDomain, indexChild: Int, onComplete: (position: Int) -> Unit) -> Unit,
    onRemoveItem: (componentDomain: ComponentDomain, indexChild: Int, onComplete: (position: Int) -> Unit) -> Unit,
    onClickImage: (indexPhotoSelected: Int, componentKey: String, componentId: String,componentDomain: ComponentDomain) -> Unit,
    currentParentIndex: List<Int> = listOf(),
    item: ComponentDomain,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {


    Column(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
    ) {

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (item.repeatable) {
                if (item.removable == false) {
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
            initialize(
                isChild,
                scrollingState,
                parentIndex,
                savedIndex,
                taskID,
                modifier.heightIn(0.dp, 1000.dp),
                photoDomainList,
                it,
                onChanges as (ComponentDomain, List<ValueDomain>?) -> Unit,
                onAddItem as (componentDomain: ComponentDomain, indexChild: Int, onComplete: (position: Int) -> Unit) -> Unit,
                onRemoveItem as (ComponentDomain, Int, onComplete: (position: Int) -> Unit) -> Unit,
                onClickImage,
                currentParentIndex,
            )
        }
    }

}


