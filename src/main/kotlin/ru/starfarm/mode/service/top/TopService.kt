package ru.starfarm.mode.service.top

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.hologram.Hologram
import ru.starfarm.core.hologram.impl.line.TextHologramLine
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.References
import ru.starfarm.mode.TABLE
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.playerScope

object TopService : BukkitService {

    private val tops = mutableListOf<Pair<Stats, Hologram>>()

    override fun load() {
        Stats.values().forEach { it.topEntity?.let { hologram -> tops.add(it to hologram) } }

        Plugin.taskContext.everyAsync(0, 60 * 20) {
            getSortedTop(*tops.map { it.first }.toTypedArray()) {
                tops.forEach { (stats, hologram) ->
                    val topByStat = it.filter { it.key.second == stats }
                        .map { it.key.first to it.value }
                        .sortedByDescending { it.second }
                        .take(10)

                    hologram
                        .lines
                        .values
                        .reversed()
                        .filterIsInstance<TextHologramLine>()
                        .drop(1)
                        .take(topByStat.size)
                        .forEachIndexed { index, line ->
                            val (name, value) = topByStat[index]

                            line.text = "${
                                (index + 1).run {
                                    "${
                                        when (this) {
                                            1 -> ChatColor.DARK_AQUA
                                            2 -> ChatColor.BLUE
                                            3 -> ChatColor.DARK_BLUE
                                            else -> ChatColor.YELLOW
                                        }
                                    }$this. §f$name"
                                }
                            } - §l§o${References.format(value)}"
                        }
                }
            }
        }
    }

    private fun getSortedTop(
        vararg sortStats: Stats,
        callback: (List<MutableMap.MutableEntry<Pair<String, Stats>, Double>>) -> Unit,
    ) =
        mutableMapOf<Pair<String, Stats>, Double>().apply map@{
            Plugin.baseService.query(
                "SELECT * FROM $TABLE"
            ) { resultSetWrapper ->
                JSONParser().also { parser ->
                    while (resultSetWrapper.next()) {
                        val data = parser.parse(resultSetWrapper.getString("data")) as JSONObject
                        val name = data["name"] as String
                        val player = Bukkit.getPlayer(name)

                        sortStats.forEach {
                            val value = player?.playerScope?.getAmount(it)
                                ?: (data["stats"] as JSONObject).run {
                                    if (isEmpty()) .0 else getOrDefault(
                                        it.name,
                                        .0
                                    ) as Double
                                }

                            put(name to it, value)
                        }
                    }
                }

                Plugin
                    .playerService
                    .players
                    .values
                    .forEach players@{ playerScope ->
                        sortStats.forEach {
                            put((playerScope.name ?: return@players) to it, playerScope.getAmount(it))
                        }
                    }

                callback.invoke(entries.distinct())
            }
        }
}