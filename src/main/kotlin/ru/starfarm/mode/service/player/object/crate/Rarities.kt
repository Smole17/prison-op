package ru.starfarm.mode.service.player.`object`.crate

import org.bukkit.ChatColor

enum class Rarities(val chatColor: ChatColor, private val formatted: String, val chance: Double) {

    COMMON(ChatColor.GRAY, "Обычный", 0.1),
    UNCOMMON(ChatColor.GREEN, "Необычный", 0.020),
    RARE(ChatColor.BLUE, "Редкий", 0.012),
    EPIC(ChatColor.LIGHT_PURPLE, "Эпический", 0.0075),
    LEGENDARY(ChatColor.YELLOW, "Легендарный", 0.0025),
    EXCEEDINGLY_RARE(ChatColor.DARK_AQUA, "Чрезвычайно редкий", 0.00055),
    GODLY(ChatColor.RED, "Божественный", 0.0001),
    ;

    override fun toString(): String {
        return "$chatColor$formatted"
    }

    fun color(text: String): String = "$chatColor$text"
}