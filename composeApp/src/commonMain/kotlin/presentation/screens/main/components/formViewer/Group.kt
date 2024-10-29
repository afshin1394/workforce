@file:Suppress("UNCHECKED_CAST")

package presentation.screens.main.components.formViewer

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.flow.MutableStateFlow
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary
import utils.initialize


@Composable
fun groupComponent(
    disable : Boolean,
    readOnly : Boolean,
    processLogicDomain: ProcessLogicDomain,
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
    nestedItems : List<ComponentDomain>?,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    val disableLogic = processLogicDomain.disabled || disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage

    val backgroundBorder = if (validateLogic) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        strokeDefaultLight
    }
    val styledString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                fontSize = 15.sp
            )
        ) {
            append(item.label)
        }
        if (requiredLogic) {
            withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                append(" *")
            }
        }
    }


   Napier.log(LogLevel.ASSERT, tag = "hideLogictoString", message = hideLogic.toString())
    if(!hideLogic) {
        Column(
            modifier = Modifier
                .border(1.dp, backgroundBorder, RoundedCornerShape(4.dp))
        ) {

            Text(
                text = styledString,
                modifier = Modifier.padding(spacing05X).fillMaxWidth().wrapContentHeight().align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.padding(top = spacing05X))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (item.repeatable) {
                    if (item.removable == false) {
                        IconButton(onClick = onAddClick) {
                            Icon(
                                painter = painterResource(MR.images.add),
                                contentDescription = "Add"
                            )
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
            nestedItems?.let {
                initialize(
                    isChild,
                    scrollingState,
                    parentIndex,
                    savedIndex,
                    taskID,
                    modifier.heightIn(0.dp, 1000.dp),
                    photoDomainList,
                    nestedItems,
                    onChanges as (ComponentDomain, List<ValueDomain>?) -> Unit,
                    onAddItem as (componentDomain: ComponentDomain, indexChild: Int, onComplete: (position: Int) -> Unit) -> Unit,
                    onRemoveItem as (ComponentDomain, Int, onComplete: (position: Int) -> Unit) -> Unit,
                    onClickImage,
                    currentParentIndex,
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
        }
    }

}


