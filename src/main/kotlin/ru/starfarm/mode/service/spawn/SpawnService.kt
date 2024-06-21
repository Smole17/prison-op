package ru.starfarm.mode.service.spawn

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.mode.Plugin

object SpawnService : BukkitService {

    val spawn =
        Location(
            Bukkit.getWorlds()[0],
            .5, 102.0, .5,
            -90f, 0f
        )

    init {
        Plugin.registerCommands(
            object : Command<Player>("spawn", "Телепортироваться на спавн.") {
                override fun execute(ctx: CommandContext<Player>) {
                    teleport(ctx.sender)
                }
            }
        )

        Plugin.taskContext.every(0, 20) {
            Bukkit.getOnlinePlayers().filter { it.location.y <= 0 }.forEach(::teleport)
        }

        Plugin.on<CreatureSpawnEvent> {
            if (spawnReason !== CreatureSpawnEvent.SpawnReason.NATURAL || entityType !== EntityType.VILLAGER && entityType !== EntityType.WANDERING_TRADER &&
                entityType !== EntityType.TRADER_LLAMA) return@on
            isCancelled = true
        }

        Plugin.on<EntityDamageByEntityEvent> {
            if (damager.type === EntityType.PLAYER && entityType === EntityType.ARMOR_STAND) isCancelled = true
        }

        Plugin.on<PlayerInteractAtEntityEvent> {
            val entityType = rightClicked.type
            if (entityType === EntityType.ARMOR_STAND || entityType === EntityType.VILLAGER || entityType === EntityType.WANDERING_TRADER) isCancelled = true
        }

        Plugin.on<PlayerInteractEvent> {
            if (hasBlock() && clickedBlock!!.type.isInteractable) isCancelled = true
        }
    }

    override fun load(player: Player) {
        teleport(player)
    }

    fun teleport(entity: Entity) {
        entity.teleport(spawn)
    }
}