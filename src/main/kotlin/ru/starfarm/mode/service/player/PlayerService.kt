package ru.starfarm.mode.service.player

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.require.Require
import ru.starfarm.core.command.type.TypeString
import ru.starfarm.core.database.query.DatabaseResultSetWrapper
import ru.starfarm.core.event.GlobalEventContext
import ru.starfarm.core.profile.group.DonateGroup
import ru.starfarm.core.profile.group.StaffGroup
import ru.starfarm.core.profile.violation.StaffMod
import ru.starfarm.core.profile.violation.event.StaffDisableEvent
import ru.starfarm.core.profile.violation.event.StaffEnableEvent
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.serializer.Serializer
import ru.starfarm.mode.*
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.`object`.event.PlayerInitializedEvent
import ru.starfarm.mode.service.skill.Skills
import ru.starfarm.mode.service.spawn.SpawnService
import java.util.*

val Player.playerScope get() = Plugin.playerService.getPlayerScope(uniqueId)

object PlayerService : BukkitService {

    val players = hashMapOf<UUID, PlayerScope>()

    init {
        Plugin.taskContext.every(0, 100) {
            players.values.forEach {
                it.pickaxe.addItem(it, it.player.apply {
                    addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, true, false))
                })
            }
        }

        Plugin.taskContext.every(0, 20) {
            players.values.forEach {
                if (!it.isFlightAvailable && it.resetFlight()) {
                    it.player.apply {
                        isFlying = false
                        allowFlight = false

                        ChatUtil.sendMessage(this, "${NEGATIVE}Время действия полёта было исчерпано.")
                    }
                }

                if (!it.isGroupAvailable) {
                    it.setGroup()
                    ChatUtil.sendMessage(it.player, "${POSITIVE}Время действия группы было исчерпано.")
                }

                it.checkBoosters()
            }
        }

        Plugin.registerCommands(
            object : Command<Player>("crates") {
                init {
                    addRequire(
                        Require.groups(
                            DonateGroup.ELITE_PLUS, DonateGroup.SPONSOR, DonateGroup.SPONSOR_PLUS,
                            DonateGroup.UNIQUE, StaffGroup.GAME_DESIGNER, StaffGroup.DEVELOPER,
                            StaffGroup.ADMINISTRATOR
                        )
                    )
                }

                override fun execute(ctx: CommandContext<Player>) =
                    Crates.openCratesGui(ctx.sender.playerScope)

            },
            object : Command<Player>("skills") {
                override fun execute(ctx: CommandContext<Player>) =
                    Skills.openSkillsGui(ctx.sender.playerScope)

            },
            object : Command<Player>("fly", "Включить режим полёта.") {
                override fun execute(ctx: CommandContext<Player>) {
                    val player = ctx.sender
                    val playerScope = player.playerScope

                    if (!playerScope.isFlightAvailable) {
                        ChatUtil.sendMessage(player, "${NEGATIVE}У вас нет доступа к полёту!")
                        return
                    }

                    player.allowFlight = !player.allowFlight
                    ChatUtil.sendMessage(
                        player,
                        "${if (player.allowFlight) "${POSITIVE}Вы парите!" else "${NEGATIVE}Вы отключили полёт."} ${NEUTRAL}(${
                            playerScope.getFlight().toMinutes()
                        } минут)"
                    )
                }
            },
            object : Command<Player>("stats", "Вывести подробную статистику о игроке.", "st") {
                init {
                    addCommand(object : Command<Player>("player", "Вывести подробную статистику о игроке.", "p") {
                        init {
                            addParameter("имя игрока", TypeString())

                            addRequire(
                                Require.groups(
                                    StaffGroup.HELPER, StaffGroup.MODERATOR, StaffGroup.SR_MODERATOR,
                                    StaffGroup.CURATOR, StaffGroup.GAME_DESIGNER, StaffGroup.DEVELOPER,
                                    StaffGroup.ADMINISTRATOR
                                )
                            )
                        }

                        override fun execute(ctx: CommandContext<Player>) {
                            printStats(Bukkit.getPlayer(ctx.getArg<String>(0) ?: return) ?: return, ctx.sender)
                        }
                    })
                }

                override fun execute(ctx: CommandContext<Player>) {
                    printStats(ctx.sender)
                }

                private fun printStats(player: Player, receiver: Player = player) {
                    val playerScope = player.playerScope

                    ChatUtil.sendMessage(receiver, "${POSITIVE}Статистика ${player.name}:")
                    ChatUtil.sendMessage(receiver, "")
                    Stats.values().forEach {
                        ChatUtil.sendMessage(
                            receiver,
                            "§b§l| ${it.colored(References.format(playerScope.getAmount(it), "###,###"), true)}"
                        )
                    }
                    ChatUtil.sendMessage(receiver, "")
                }
            }
        )

        Plugin.on<StaffEnableEvent> {
            moderator.showPlayer(Plugin, target)
        }

        Plugin.on<StaffDisableEvent> {
            moderator.showPlayer(Plugin, target)
        }

        Plugin.on<StaffDisableEvent> {
            moderator.hidePlayer(Plugin, target)
        }

        Plugin.on<PlayerDeathEvent> {
            deathMessage = null
            entity.spigot().respawn()
        }

        Plugin.on<PlayerRespawnEvent> {
            respawnLocation = SpawnService.spawn
        }

        Plugin.on<PlayerToggleFlightEvent> {
            if (player.gameMode != GameMode.SURVIVAL || StaffMod.StaffModMap.containsKey(player)) return@on
            if (!player.playerScope.isFlightAvailable && player.allowFlight) isCancelled = true
        }

        Plugin.on<PlayerInitializedEvent> {
            Skills.values().forEach {
                playerScope.getCurrentSkill(it).apply {
                    levels = it.skillScope.levels
                }
            }
        }
    }

    override fun load(player: Player) {
        logger.info("${player.name} is trying to connect on the mode.")

        Plugin.baseService
            .query("SELECT * FROM `${TABLE}` WHERE `uuid` = ?;", player.uniqueId.toString()) {
                addPlayer(
                    player,
                    it
                )
            }
    }

    override fun unload(player: Player) {
        logger.info("${player.name} is came out of the mode.")

        val uuid = player.uniqueId

        val playerScope = Serializer.toJson(player.playerScope)

        Plugin.baseService
            .updateQuery(
                "INSERT INTO `$TABLE` VALUES (?, ?) ON DUPLICATE KEY UPDATE `data` = ?;",
                uuid.toString(), playerScope, playerScope
            )
        players.remove(uuid)
    }

    fun getPlayerScope(uuid: UUID): PlayerScope = Bukkit.getPlayer(uuid)!!.run {
        players.getOrDefault(uuid, PlayerScope(uuid, name))
    }

    private fun addPlayer(player: Player, resultSet: DatabaseResultSetWrapper) {
        logger.info("Attempt to add ${player.name} player cache.")
        val uuid = player.uniqueId
        val nullableResultSet = resultSet.nextOrNull()

        val playerScope =
            run { nullableResultSet?.getJsonObject<PlayerScope>("data") } ?: PlayerScope(uuid, player.name)
        sendInitializedEvent(player, playerScope)
    }

    private fun sendInitializedEvent(player: Player, playerScope: PlayerScope) {
        players[player.uniqueId] = playerScope

        Plugin.taskContext.after(0) { GlobalEventContext.post(PlayerInitializedEvent(player, playerScope)) }
        logger.info("${player.name} is successfully connected!")
    }
}