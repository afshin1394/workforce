package presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.stringResource
import domain.models.task.PropertiesDomain
import domain.models.task.TaskDomain
import irancell.nwg.wfm.MR
import kotlinx.coroutines.delay
import presentation.theme.body_large
import presentation.theme.body_large_strong
import presentation.theme.body_small
import presentation.theme.body_small_strong
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.surfaceErrorLight
import presentation.theme.textBrand
import presentation.theme.textError
import presentation.theme.textInverse
import presentation.theme.textPrimary
import utils.TaskState
import utils.debounceClick
import kotlin.math.min

@Composable
fun ticketCard(
    modifier: Modifier = Modifier,
    task: TaskDomain,
    onActionClick: () -> Unit = {},
    onMoreOptionsClick: () -> Unit = {}
) {
    val initialCount = min(2, task.properties.size)

    var showMore by remember { mutableStateOf(false) }

    var currentlyVisibleCount by remember { mutableStateOf(initialCount) }

    LaunchedEffect(showMore) {
        if (showMore) {
            for (i in currentlyVisibleCount until task.properties.size) {
                delay(80)
                currentlyVisibleCount = i + 1
            }
        } else {

            for (i in currentlyVisibleCount downTo (initialCount + 1)) {
                delay(80)
                currentlyVisibleCount = i - 1
            }
        }
    }


    val rotation by animateFloatAsState(
        targetValue = if (showMore) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    )

    Card(
        colors = CardDefaults.cardColors(surfaceDefault),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = spacing05X,
                    start = spacing15X,
                    end = spacing15X,
                    bottom = spacing15X
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Text(
                    text = task.ticket_number ?: "",
                    style = body_large_strong,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp),
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.padding(vertical = spacing05X))

     
            task.properties.forEachIndexed { index, property ->
                AnimatedVisibility(
                    visible = index < currentlyVisibleCount,
                    enter = fadeIn() + expandVertically(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    PropertyItem(property)
                }
            }


            if (task.properties.size > 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing05X),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showMore = !showMore
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = if (showMore) "Show less" else "Show more",
                                color = surfaceBrandDefault
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = surfaceBrandDefault,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .rotate(rotation)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = spacing05X))


            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing05X),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    onClick = onMoreOptionsClick,
                    colors = CardDefaults.cardColors(surfaceDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(
                            width = 1.dp,
                            color = strokeDefaultLight,
                            shape = RoundedCornerShape(size = radiusLarge)
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        androidx.compose.material3.Text(
                            text = "More options",
                            color = textPrimary,
                            style = body_large,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    onClick = onActionClick,
                    colors = CardDefaults.cardColors(surfaceBrandDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(
                            width = 1.dp,
                            color = strokeDefaultLight,
                            shape = RoundedCornerShape(size = radiusLarge)
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        androidx.compose.material3.Text(
                            text = "Accept",
                            color = textInverse,
                            style = body_large,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun PropertyItem(property: PropertiesDomain) {
    Column(
        modifier = Modifier.padding(vertical = spacing05X)
    ) {

        androidx.compose.material3.Text(
            text = property.key,
            style = body_small_strong,
            color = textPrimary
        )
        androidx.compose.material3.Text(
            text = property.value,
            style = body_small,
            color = Color.Gray
        )
    }
}



