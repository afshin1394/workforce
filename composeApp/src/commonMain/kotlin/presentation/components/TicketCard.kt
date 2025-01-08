package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.stringResource
import domain.models.task.TaskDomain
import irancell.nwg.wfm.MR
import presentation.theme.body_large
import presentation.theme.body_large_strong
import presentation.theme.body_small
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

@Composable
fun ticketCard(
    modifier: Modifier,
    task: TaskDomain,
    onActionClick: () -> Unit = {},
    onMoreOptionsClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(surfaceDefault),
        modifier = modifier
    ) {
        Column(
            modifier = modifier.padding(
                top = spacing05X,
                start = spacing15X,
                end = spacing15X,
                bottom = spacing15X
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
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


            task.properties.forEach { property ->
                androidx.compose.material3.Text(
                    text = "${property.key}: ${property.value}",
                    style = body_small,
                    color = textBrand,
                    modifier = Modifier.padding(vertical = spacing05X)
                )
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
                            text = stringResource(MR.strings.more_options),
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
                            text = stringResource(MR.strings.accept),
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



