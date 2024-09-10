package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import domain.models.task.TaskDomain
import irancell.nwg.wfm.MR

import presentation.theme.body_large
import presentation.theme.body_large_strong
import presentation.theme.body_small
import presentation.theme.caption
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.surfaceErrorLight
import presentation.theme.textBrand
import presentation.theme.textError
import presentation.theme.textInverse
import presentation.theme.textPrimary
import presentation.theme.textWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ticketCard(
    modifier: Modifier = Modifier, task: TaskDomain,
    onActionClick: () -> Unit = {},
    onMoreOptionsClick: () -> Unit = {}
) =

    Card(
        colors = CardDefaults.cardColors(surfaceDefault),
        modifier = modifier
            .fillMaxWidth()

            .padding(top = spacing15X)


    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            ) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Text(
                    text = task.basic_info.ticket_number?:"",
                    style = body_large_strong,
                    maxLines = 1,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(22.dp),
                    color = textPrimary


                )

            }
            Spacer(modifier = modifier.padding(vertical = spacing05X))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Card(
                    colors = CardDefaults.cardColors(surfaceErrorLight),
                    modifier = modifier
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
                        text =  task.basic_info.province.toString(),
                        style = body_small,
                        color = textError
                    )
                }
                Spacer(modifier = modifier.padding(spacing15X))

                Card(
                    colors = CardDefaults.cardColors(subtleDefault),
                    modifier = modifier
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
                Spacer(modifier = modifier.padding(spacing15X))



            }
            Spacer(modifier = modifier.padding(vertical = spacing05X))

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Card(
                    colors = CardDefaults.cardColors(subtleDefault),
                    modifier = modifier
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
            Spacer(modifier = modifier.padding(vertical = spacing05X))

            Row(verticalAlignment = Alignment.CenterVertically) {
//                if (task.instanceStateId != Completed.id) {
                    Card(
                        onClick = onMoreOptionsClick,
                        colors = CardDefaults.cardColors(surfaceDefault),
                        modifier = modifier
                            .weight(1f)
                            .background(
                                color = surfaceDefault,
                                shape = RoundedCornerShape(size = radiusLarge)
                            )

                            .border(
                                width = 1.dp,
                                color = strokeDefaultLight,
                                shape = RoundedCornerShape(size = radiusLarge)
                            )

                    ) {
                        androidx.compose.material3.Text(
                            text = stringResource(MR.strings.more_options),
                            color = textPrimary,
                            style = body_large,
                            fontSize = 16.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .padding(
                                    start = spacing05X,
                                    top = 4.dp,
                                    end = spacing05X,
                                    bottom = 4.dp
                                )
                                .padding(
                                    start = spacing05X,
                                    top = 4.dp,
                                    end = spacing05X,
                                    bottom = 4.dp
                                )

                        )

                    }
                    Spacer(modifier = modifier.padding(spacing05X))

                    Card(
                        onClick = onActionClick,

                        colors = CardDefaults.cardColors(surfaceBrandDefault),
                        modifier = modifier
                            .weight(1f)
                            .background(
                                color = surfaceBrandDefault,
                                shape = RoundedCornerShape(size = radius)
                            )
                            .background(
                                color = surfaceBrandDefault,
                                shape = RoundedCornerShape(size = radiusLarge),

                                )


                    ) {
                        androidx.compose.material3.Text(
                            text = stringResource(MR.strings.accept),
                            color = textInverse,
                            style = body_large,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .padding(
                                    start = spacing05X,
                                    top = 4.dp,
                                    end = spacing05X,
                                    bottom = 4.dp
                                )
                                .padding(
                                    start = spacing05X,
                                    top = 4.dp,
                                    end = spacing05X,
                                    bottom = 4.dp
                                )

                        )

                    }
//                }

            }
        }
    }

