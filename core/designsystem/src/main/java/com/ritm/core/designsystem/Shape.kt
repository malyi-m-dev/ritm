package com.ritm.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Радиусы скругления, перенесённые из border-radius вёрстки. */
object RitmShapes {
    val card = RoundedCornerShape(20.dp)
    val list = RoundedCornerShape(18.dp)
    val chip = RoundedCornerShape(10.dp)
    val fab = RoundedCornerShape(16.dp)
    val field = RoundedCornerShape(12.dp)
    val sheetTop = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
    val pill = RoundedCornerShape(percent = 50)
}
