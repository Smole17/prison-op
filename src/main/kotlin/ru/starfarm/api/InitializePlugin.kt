package ru.starfarm.api

import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import ru.starfarm.api.service.BukkitService
import ru.starfarm.api.service.impl.register.RegisterService
import ru.starfarm.core.CorePlugin

abstract class InitializePlugin : CorePlugin(), Listener {

    lateinit var registerService: RegisterService

    final override fun enable() {
        registerService = RegisterService(this)

        execute()

        registerService.load()
    }

    final override fun disable() {
        close()

        registerService.unload()
    }

    abstract fun execute()

    abstract fun close()

    override fun handleTowerConnect() {
        registerService.services.forEach(BukkitService::loadOnTowerConnect)
    }

    override fun handleTowerDisconnect() {
        registerService.services.forEach(BukkitService::unloadOnTowerDisconnect)
    }

    inline fun <reified E : org.bukkit.event.Event> on(eventPriority: EventPriority = EventPriority.NORMAL, crossinline handler: E.() -> Unit) {
        eventContext.on(E::class.java, { handler.invoke(it) }, eventPriority)
    }
}