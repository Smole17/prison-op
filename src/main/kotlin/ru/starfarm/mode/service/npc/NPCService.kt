package ru.starfarm.mode.service.npc

import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.entity.impl.FakePlayer
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.service.npc.`object`.NPCs

object NPCService : BukkitService {

    private val npcs = arrayListOf<FakePlayer>()

    override fun load() {
        NPCs.values().forEach { npcs.add(it.fakePlayer) }

        Plugin.taskContext.everyAsync(0, 1) {
            npcs.forEach { fakePlayer ->
                fakePlayer.players.forEach { fakePlayer.look(it) }
            }
        }
    }
}