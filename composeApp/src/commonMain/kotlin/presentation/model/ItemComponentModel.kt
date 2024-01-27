package presentation.model

import androidx.compose.ui.graphics.Color
import presentation.theme.subtleDefault
import presentation.theme.surfaceDefault
import presentation.theme.textBrand
import dev.icerock.moko.resources.ImageResource
import irancell.nwg.wfm.MR

data class ItemComponentModel(val text : String="Cancel ticket", val color : Color = surfaceDefault, val hasImage: Boolean = false, val imageResource: ImageResource = MR.images.chevron_right, val hasTag : Boolean = false, val textTag : String = "English", val textTagColor : Color = textBrand, val tagColor : Color = subtleDefault)
