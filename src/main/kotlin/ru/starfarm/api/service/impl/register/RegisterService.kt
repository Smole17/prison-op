package ru.starfarm.api.service.impl.register

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.starfarm.api.InitializePlugin
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.event.on

class RegisterService(private val initializePlugin: InitializePlugin) : BukkitService {

    val services = hashSetOf<BukkitService>()

    override fun load() {
        services.forEach { it.load() }

        initializePlugin.eventContext.on<PlayerJoinEvent> {
            joinMessage = ""
            services.forEach { it.load(player) }
        }

        initializePlugin.eventContext.on<PlayerQuitEvent> {
            quitMessage = ""
            services.forEach { it.unload(player) }
        }
    }

    override fun unload() {
        services.forEach {
            logger.info("Unloading ${it::class.java.simpleName} service.")
            it.unload()
        }
    }

    fun addServices(vararg service: BukkitService) {
        service.forEach {
            logger.info("Registering ${it::class.java.simpleName} service.")
            services.add(it)
        }
    }
}