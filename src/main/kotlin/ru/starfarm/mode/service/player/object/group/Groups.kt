package ru.starfarm.mode.service.player.`object`.group

import org.bukkit.ChatColor
import ru.starfarm.mode.hexed

enum class Groups(private val hexedName: String, val chatColor: ChatColor, val multiplier: Double) {

    MANTLE("&#a78c8c&lM&#9c918f&lA&#919692&lN&#8f9490&lT&#968b89&lL&#9d8383&lE".hexed(), ChatColor.GRAY, 1.0),
    EARTH("&#6d4e12&lE&#6c4f18&lA&#6c4f1d&lR&#6b5023&lT&#6a5028&lH".hexed(), ChatColor.DARK_GRAY, 1.1),
    AQUA("&#0bc4d0&lA&#07d8e0&lQ&#04ebef&lU&#00ffff&lA".hexed(), ChatColor.AQUA, 1.2),
    AIR("&#dedfc3&lA&#fffdd7&lI&#d3dfbf&lR".hexed(), ChatColor.WHITE, 1.3),
    SKY("&#0053d0&lS&#032ae8&lK&#0500ff&lY".hexed(), ChatColor.BLUE, 1.4),
    COSMOS("&#7500d0&lC&#8000d9&lO&#8b00e3&lS&#9700ec&lM&#a200f6&lO&#ad00ff&lS".hexed(), ChatColor.DARK_PURPLE, 1.6),
    SUN("&#d0bb00&lS&#e8d100&lU&#ffe600&lN".hexed(), ChatColor.YELLOW, 1.7),
    GALAXY("&#a600d0&lG&#b400d9&lA&#c200e3&lL&#cf00ec&lA&#dd00f6&lX&#eb00ff&lY".hexed(), ChatColor.LIGHT_PURPLE, 1.9),
    UNIVERSE("&#413535&lU&#36262e&lN&#2a1726&lI&#1f081f&lV&#1f081f&lE&#2a1726&lR&#36262e&lS&#413535&lE".hexed(), ChatColor.BLACK, 2.1),
    LS("&#ff135a&lL&#ff135a&lS".hexed(), ChatColor.RED, 2.1)
    ;

    override fun toString() = "$hexedName§r$chatColor"
}