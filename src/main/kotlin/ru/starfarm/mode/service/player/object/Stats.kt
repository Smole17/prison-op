package ru.starfarm.mode.service.player.`object`

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import ru.starfarm.core.ApiManager
import ru.starfarm.core.hologram.Hologram
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.Plugin

enum class Stats(
    val chatColor: ChatColor, val rowName: String, private val topName: String? = null,
    val icon: Material = Material.AIR, private val location: Location? = null,
) {

    BLOCKS(
        ChatColor.LIGHT_PURPLE,
        "Блоков",
        "Блокам",
        Material.COBBLESTONE,
        Location(
            Bukkit.getWorlds()[0],
            .5, 104.0, -9.5
        )
    ),
    PRESTIGE(
        ChatColor.WHITE,
        "Престиж",
        "Престижам",
        Material.PAPER,
        Location(
            Bukkit.getWorlds()[0],
            .5, 104.0, 10.5
        )
    ),
    MONEY(ChatColor.GOLD, "Денег", icon = Material.GOLD_INGOT),
    TOKENS(ChatColor.DARK_AQUA, "Токенов", icon = Material.MAGMA_CREAM),
    EMERALDS(ChatColor.GREEN, "Изумрудов", icon = Material.EMERALD),
    ;

    override fun toString(): String = rowName

    fun colored(needsFormat: String, text: Boolean = false): String =
        "$chatColor$needsFormat ${if (text) this.toString() else ""}"

    val topEntity: Hologram?
        get() = run {
            if (location == null || topName == null) return@run null

            ApiManager.createHologram(location).apply {
                for (i in 10 downTo 1)
                    textLine("§e$i. §7§oПусто :c")

                emptyLine()
                textLine("Топ по $chatColor$topName")
            }
        }
}