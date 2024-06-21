package ru.starfarm.mode.service.player.`object`.booster.`object`

enum class BoosterCause(private val text: String) {

    GROUP("От %s"),
    DONATE_GROUP("От %s"),
    GREED("От §6Жадности§f"),
    TEMPORARY_BOOSTER("Временный");

    override fun toString(): String {
        return "$text§f:"
    }
}