package ru.starfarm.api.service

import org.bukkit.entity.Player
import ru.starfarm.mode.Plugin
import java.util.logging.Logger

interface BukkitService {

    companion object {
        @JvmStatic
        val LOGGER: Logger get() = Logger.getLogger(Plugin.javaClass.simpleName)
    }

    val logger: Logger get() = Logger.getLogger("${LOGGER.name}-${javaClass.simpleName}")

    fun load() {}

    fun unload() {}

    fun load(player: Player) {}

    fun unload(player: Player) {}

    fun loadOnTowerConnect() {}

    fun unloadOnTowerDisconnect() {}
}