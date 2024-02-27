package presentation.screens.cr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.radius
import com.irancell.nwg.wfm.presentation.theme.radiusLarge
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import domain.models.CRDomain
import presentation.theme.body_large
import presentation.theme.body_large_strong
import presentation.theme.body_small
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.surfaceErrorLight
import presentation.theme.textBrand
import presentation.theme.textError
import presentation.theme.textInverse
import presentation.theme.textPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CRCard(
    modifier: Modifier = Modifier,
    crDomain: CRDomain,
    onActionClick: () -> Unit = {},
) =

    Card(
        colors = CardDefaults.cardColors(surfaceDefault),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x0D000000),
                ambientColor = Color(0x0D000000)
            )
            .shadow(
                elevation = 1.dp,
                spotColor = Color(0x0D000000),
                ambientColor = Color(0x0D000000)
            )
            .padding(top = spacing15X)


    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = crDomain.ticketInstanceNumber,
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
                    Text(
                        text =  crDomain.ticketInstanceId.toString(),
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
                    Text(
                        text = crDomain.ticketInstanceState,
                        style = body_small, color = textBrand
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
                    Text(
                        text = crDomain.ticketInstanceTitle,
                        style = body_small, color = textBrand
                    )
                }
            }

            Spacer(modifier = modifier.padding(vertical = spacing05X))

            Row(verticalAlignment = Alignment.CenterVertically) {


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
                    Text(
                        text = "Accept",
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

            }
        }
    }

