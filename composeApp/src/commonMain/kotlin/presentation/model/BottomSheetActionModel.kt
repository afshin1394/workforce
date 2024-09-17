package presentation.model

import androidx.compose.ui.graphics.Color

data class BottomSheetActionModel(
    val firstButtonText: String,
    val firstButtonColor: Color,
    val firstButtonTextColor: Color,
    val secondButtonText: String = "",
    val secondButtonColor: Color = Color(0xFF0077B6),
    val secondColorTextColor: Color = Color(0xFFFFFFFF)
)
