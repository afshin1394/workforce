package presentation.screens.ticket_process.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.radiusLarge

import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import domain.usecase.usecase.steps.StepDetail
import presentation.theme.body_large
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.surfaceSuccessStrong
import presentation.theme.textInverse
import presentation.theme.body_small
import presentation.theme.caption


@Composable
fun ProcessStep(
    modifier: Modifier = Modifier,
    level: Int ,
    levelText: String ,
) {


    Column(
        modifier = modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(24.dp)
                .clip(
                    CircleShape
                )
                .background(surfaceBrandDefault)
            , contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 4.dp).align(Alignment.Center),
                text = level.toString(),
                style = body_small,
                color = textInverse,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(spacing05X))
        Text(modifier = Modifier.wrapContentWidth(),text = levelText, style = caption, textAlign = TextAlign.Center, maxLines = 1)
        Spacer(modifier = Modifier.padding(spacing05X))
    }
}


@Composable
fun ProcessStepSelected(
    modifier: Modifier = Modifier,
    level: Int ,
    levelText: String ,
) {

    var scaleState by remember { mutableStateOf(1f) }

    val scale by animateFloatAsState(
        targetValue = scaleState,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        scaleState = 1.2f
    }
    Column(
        modifier = modifier.wrapContentSize().padding(top = spacing05X),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(30.dp) .scale(scale)
                .clip(
                    CircleShape
                )
                .background(surfaceSuccessStrong)
            , contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 6.dp).align(Alignment.Center),
                text = level.toString(),
                style = body_small,
                color = textInverse,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(spacing05X))
        Text(modifier = Modifier.wrapContentWidth(),text = levelText, style = caption, textAlign = TextAlign.Center, maxLines = 1)
        Spacer(modifier = Modifier.padding(spacing05X))
    }
}

@Composable
fun processBar(list: List<StepDetail>, currentLevelStep : Int ) {

    Row(
        modifier = Modifier
            .wrapContentSize()
            .background(surfaceDefault), verticalAlignment = Alignment.CenterVertically
    ) {

        list.mapIndexed { index, process ->
            if (index == currentLevelStep){
                ProcessStepSelected(
                    Modifier
                        .weight(1f)
                    , process.id + 1, process.name
                )
            }else{
            ProcessStep(
                Modifier
                    .weight(1f)
                    , process.id + 1, process.name
            )
            }
            if (list.size - 1 != index)
                Box(modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(strokeDefaultLight))

        }
    }

}

