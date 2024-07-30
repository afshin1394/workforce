package presentation.screens.ticket_process.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

import com.irancell.nwg.wfm.presentation.model.ProcessLevel

import com.irancell.nwg.wfm.presentation.theme.spacing05X
import domain.usecase.usecase.mokSteps.StepDetail
import presentation.model.StepModel
import presentation.theme.body_large
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.surfaceSuccessStrong
import presentation.theme.textInverse
import presentation.theme.body_small
import presentation.theme.body_small_strong
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
    }
}


@Composable
fun ProcessStepSelected(
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
            modifier = Modifier.size(36.dp)
                .clip(
                    CircleShape
                )
                .background(surfaceSuccessStrong)
            , contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 4.dp).align(Alignment.Center),
                text = level.toString(),
                style = body_large,
                color = textInverse,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(spacing05X))
        Text(modifier = Modifier.wrapContentWidth(),text = levelText, style = caption, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
fun processBar(list: List<StepDetail>,currentLevelStep : Int ) {

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

