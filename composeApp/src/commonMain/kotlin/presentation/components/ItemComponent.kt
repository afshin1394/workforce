package com.irancell.nwg.wfm.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign

import presentation.model.ItemComponentModel
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource

import presentation.theme.body_large
import presentation.theme.body_small
import presentation.theme.textPrimary


@Composable
fun ItemComponent(modifier: Modifier = Modifier,itemComponentModel : ItemComponentModel = ItemComponentModel(hasTag = true)) {


    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = itemComponentModel.color)
            .padding(spacing2X),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = itemComponentModel.text,
            style = body_large,
            color = textPrimary,
            textAlign = TextAlign.Start,
            modifier = modifier.weight(.9f)
        )
        if (itemComponentModel.hasTag) {

            Row(
                modifier = modifier
                    .background(
                        color = itemComponentModel.tagColor,
                        shape = RoundedCornerShape(radius2XLarge)
                    )
                    .padding(vertical = spacing05X, horizontal = spacing15X)
            ) {

                Text(
                    text = itemComponentModel.textTag,
                    style = body_small,
                    color = itemComponentModel.textTagColor
                )

            }
        }else{
            Row(
                modifier = modifier.alpha(0f)
                    .background(
                        color = itemComponentModel.tagColor,
                        shape = RoundedCornerShape(radius2XLarge)
                    )
                    .padding(vertical = spacing05X, horizontal = spacing15X)
            ) {

                Text(
                    text = itemComponentModel.textTag,
                    style = body_small,
                    color = itemComponentModel.textTagColor
                )

            }
        }


         if (itemComponentModel.hasImage) {
             Image(
                 painter = painterResource(itemComponentModel.imageResource),
                 contentDescription = "",
                 modifier = modifier.weight(.1f),
             )
         }
    }


}
