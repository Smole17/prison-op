package ru.starfarm.mode.service.player.`object`.donate

import ru.starfarm.api.service.BukkitService
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.service.player.`object`.donate.`object`.Donates

object DonateService : BukkitService {

    override fun load() {
        Donates.values().forEach {
            if (it == Donates.ABILITIES) return@forEach

            it.register()
        }
    }

    override fun loadOnTowerConnect() {
        Donates.ABILITIES.register()
    }
}