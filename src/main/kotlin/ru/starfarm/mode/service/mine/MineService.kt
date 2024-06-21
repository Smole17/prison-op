package ru.starfarm.mode.service.mine

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.inventory.TradeSelectEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerDropItemEvent
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.require.Require
import ru.starfarm.core.profile.group.StaffGroup
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.hidePlayerWithoutTab
import ru.starfarm.mode.service.player.playerScope
import ru.starfarm.mode.service.skill.Skills
import java.util.*

object MineService : BukkitService {

    private val builders = hashSetOf<UUID>()

    init {
        Plugin.registerCommands(
            object : Command<Player>("build") {
                init {
                    addRequire(
                        Require.groups(
                            StaffGroup.BUILDER, StaffGroup.SR_BUILDER, StaffGroup.BUILD_CURATOR,
                            StaffGroup.GAME_DESIGNER, StaffGroup.DEVELOPER, StaffGroup.ADMINISTRATOR
                        )
                    )
                }

                override fun execute(ctx: CommandContext<Player>) {
                    changeBuild(ctx.sender.uniqueId)
                }
            },
            object : Command<Player>("mine") {

                override fun execute(ctx: CommandContext<Player>) {
                    ctx.sender.playerScope.teleportToMine()
                }
            }
        )

        ProtocolLibrary.getProtocolManager().apply {
            fun handleBreak(event: PacketEvent) {
                val player = event.player

                if (player.inventory.itemInMainHand.type != Material.DIAMOND_PICKAXE) return

                val packet = event.packet
                val blockPosition = packet.blockPositionModifier.read(0)

                val world = player.world
                val location = blockPosition.toLocation(world)

                if (location.distance(player.location) > 6.5) return

                val playerScope = player.playerScope

                if (!playerScope.mineScope.mineCuboid.contains(location)) return

                playerScope.apply {
                    pickaxe.handleAllEnchants(location, this)
                    mineScope.decrementBlock(player)
                    increaseSkill(Skills.MINING)
                }
            }

            addPacketListener(object : PacketAdapter(Plugin, PacketType.Play.Server.BLOCK_CHANGE) {
                override fun onPacketSending(event: PacketEvent) {
                    val packet = event.packet
                    val player = event.player

                    val playerScope = player.playerScope
                    val mineScope = playerScope.mineScope

                    val blockPosition = packet.blockPositionModifier.read(0)

                    if (!mineScope.mineCuboid.contains(blockPosition.toLocation(player.world))) return

                    event.isCancelled = true
                }
            })

            addPacketListener(object : PacketAdapter(Plugin, PacketType.Play.Client.BLOCK_DIG) {
                override fun onPacketReceiving(event: PacketEvent) = event.packet.playerDigTypes.read(0)
                    .run {
                        when (this) {
                            PlayerDigType.START_DESTROY_BLOCK -> handleBreak(event)
                            PlayerDigType.ABORT_DESTROY_BLOCK -> event.isCancelled = true
                            else -> return@run
                        }
                    }
            })
        }

        Plugin.on<EntityDamageEvent> {
            if (cause === EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause === EntityDamageEvent.DamageCause.FALL || cause === EntityDamageEvent.DamageCause.LAVA
                || cause === EntityDamageEvent.DamageCause.FIRE || cause === EntityDamageEvent.DamageCause.DROWNING
            ) isCancelled = true
        }

        Plugin.on<BlockDamageEvent> { isCancelled = true }

        Plugin.on<PlayerChangedWorldEvent> {
            val world = player.world

            when (world.name) {
                "mine" -> world.players.forEach {
                    player.hidePlayerWithoutTab(it)
                    it.hidePlayerWithoutTab(player)
                }

                "spawn" -> Bukkit.getOnlinePlayers().forEach { player.showPlayer(Plugin, it) }
            }
        }

        Plugin.on<BlockPlaceEvent> {
            if (!builders.contains(player.uniqueId)) isCancelled = true
        }

        Plugin.on<BlockBreakEvent> {
            if (!builders.contains(player.uniqueId)) isCancelled = true
        }

        Plugin.on<VillagerAcquireTradeEvent> {
            isCancelled = true
        }

        Plugin.on<BlockExplodeEvent> {
            isCancelled = true
        }

        Plugin.on<EntityExplodeEvent> {
            isCancelled = true
        }

        Plugin.on<PlayerDropItemEvent> {
            if (player.world.name == "mine") isCancelled = true
        }
    }

    fun changeBuild(uuid: UUID) {
        if (builders.contains(uuid)) {
            builders.remove(uuid)
            return
        }

        builders.add(uuid)
    }

    override fun unload() {
        builders.clear()
    }

    override fun unload(player: Player) {
        Bukkit.getOnlinePlayers().forEach { it.showPlayer(Plugin, player) }
    }
}