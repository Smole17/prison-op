package ru.starfarm.mode.service.player.`object`.booster.`object`

import ru.starfarm.core.util.CurrentMillis
import ru.starfarm.mode.service.player.`object`.Stats

data class BoosterScope(val stats: Stats, val boosterCause: BoosterCause, val multiplier: Double, val endTimeAt: Long) {

    val endTime = if (endTimeAt == 0L) 0L else CurrentMillis + endTimeAt

    val isBoosterAvailable get() = endTime > CurrentMillis || endTimeAt == 0L
}