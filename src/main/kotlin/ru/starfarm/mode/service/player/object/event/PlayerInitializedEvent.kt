package ru.starfarm.mode.service.player.`object`.event

import org.bukkit.entity.Player
import ru.starfarm.core.event.pattern.PlayerCoreEvent
import ru.starfarm.mode.service.player.`object`.PlayerScope

class PlayerInitializedEvent(player: Player, val playerScope: PlayerScope) : PlayerCoreEvent(player)