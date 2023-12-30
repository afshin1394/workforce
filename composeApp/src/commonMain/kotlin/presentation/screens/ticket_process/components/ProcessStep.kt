package com.irancell.nwg.wfm.presentation.screens.ticket_process.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.irancell.nwg.wfm.presentation.model.ProcessLevel

import com.irancell.nwg.wfm.presentation.theme.spacing05X
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
    level: Int = 1,
    levelText: String = "HSE check 1",
    isActive: Boolean = false
) {
    val circleColor = if (isActive) surfaceSuccessStrong else surfaceBrandDefault
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
                .background(circleColor)
        ) {
            Text(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
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

val listSample = mutableListOf<ProcessLevel>(
    ProcessLevel("242340=-23043-2=",level = 1, levelName = "HSE check 1", isActive = true),
    ProcessLevel("9423492=394=249",level = 2, levelName = "Routing", isActive = false),
    ProcessLevel("iewjckplpwelde",level = 3, levelName = "HSE check 2", isActive = false),
    ProcessLevel("gkewj-ri-0wir0-3i0",level = 4, levelName = "Job report", isActive = false),

)

@Composable
fun processBar(list: MutableList<ProcessLevel> = listSample, activeState: Int = 1) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .background(surfaceDefault), verticalAlignment = Alignment.CenterVertically
    ) {
        list.mapIndexed { index, process ->
            ProcessStep(
                Modifier
                    .weight(1f)
                    , process.level, process.levelName, process.isActive
            )
            if (list.size - 1 != index)
                Box(modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(strokeDefaultLight))

        }
    }

}

