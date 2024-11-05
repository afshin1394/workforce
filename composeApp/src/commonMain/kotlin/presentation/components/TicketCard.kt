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
                    text = task.basic_info.ticket_number ?: "",
                    style = body_large_strong,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp),
                    color = textPrimary
                )
            }
            Spacer(modifier = Modifier.padding(vertical = spacing05X))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Card(
                    colors = CardDefaults.cardColors(surfaceErrorLight),
                    modifier = Modifier
                        .background(
                            color = surfaceErrorLight,
                            shape = RoundedCornerShape(size = radius)
                        )
                        .padding(
                            start = spacing15X,
                            top = spacing05X,
                            end = spacing15X,
                            bottom = spacing05X
                        )

                ) {
                    androidx.compose.material3.Text(
                        text = task.basic_info.province.toString(),
                        style = body_small,
                        color = textError
                    )
                }
                Spacer(modifier = Modifier.padding(spacing15X))

                Card(
                    colors = CardDefaults.cardColors(subtleDefault),
                    modifier = Modifier
                        .background(
                            color = subtleDefault,
                            shape = RoundedCornerShape(size = radius)
                        )
                        .padding(
                            start = spacing15X,
                            top = spacing05X,
                            end = spacing15X,
                            bottom = spacing05X
                        )

                ) {
                    androidx.compose.material3.Text(
                        text = task.basic_info.region.toString(),
                        style = body_small, color = textBrand
                    )
                }
                Spacer(modifier = Modifier.padding(spacing15X))


            }
            Spacer(modifier = Modifier.padding(vertical = spacing05X))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Card(
                    colors = CardDefaults.cardColors(subtleDefault),
                    modifier = Modifier
                        .background(
                            color = subtleDefault,
                            shape = RoundedCornerShape(size = radius)
                        )
                        .padding(
                            start = spacing15X,
                            top = spacing05X,
                            end = spacing15X,
                            bottom = spacing05X
                        )
                ) {
                    androidx.compose.material3.Text(
                        text = task.basic_info.site.toString(),
                        style = body_small, color = textBrand
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = spacing05X))

            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            fontSize = 16.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing05X)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(spacing05X))

                Card(
                    onClick = onActionClick,
                    colors = CardDefaults.cardColors(surfaceBrandDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(
                            width = 1.dp,
                            color = strokeDefaultLight,
                            shape = RoundedCornerShape(size = 12.dp)
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
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing05X)
                        )
                    }
                }
            }
        }
    }
}


