package ru.starfarm.mode.service.player.`object`.booster

import org.bukkit.entity.Player
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.util.CurrentMillis
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.booster.`object`.BoosterCause
import ru.starfarm.mode.service.player.playerScope

object BoosterService : BukkitService {

    init {
        Plugin.registerCommands(
            object : Command<Player>("boosters", "Получить информацию о активных бустерах.") {

                override fun execute(ctx: CommandContext<Player>) {
                    val player = ctx.sender

                    player.playerScope.apply {
                        Stats.values().forEach { stats ->
                            ChatUtil.sendMessage(player, "${stats.colored("Бустеры", true)}:")
                            val boosters = getBoosters(stats)

                            if (boosters.isEmpty()) {
                                ChatUtil.sendMessage(player, " Пусто ;c")
                                return@forEach
                            }

                            boosters.forEach {
                                val boosterCause = it.boosterCause

                                ChatUtil.sendMessage(
                                    player,
                                    " ${
                                        when (boosterCause) {
                                            BoosterCause.GROUP -> boosterCause.toString().format(group)
                                            BoosterCause.DONATE_GROUP -> boosterCause.toString()
                                                .format(profile?.donateGroup?.displayName.run {
                                                    if (this == null) return

                                                    ifBlank { return@run "Игрока" }
                                                })

                                            else -> boosterCause.toString()
                                        }
                                    } ${stats.chatColor}+${(it.multiplier * 100).toInt()}%%${
                                        when (boosterCause) {
                                            BoosterCause.GROUP ->
                                                group.expireIn.run {
                                                    if (this == 0L) "" else " ${NEUTRAL}§o(${
                                                        NumberUtil.getTime(
                                                            this - CurrentMillis
                                                        )
                                                    })"
                                                }

                                            else ->
                                                if (it.endTimeAt == 0L) "" else " ${NEUTRAL}§o(${
                                                    NumberUtil.getTime(
                                                        it.endTime - CurrentMillis
                                                    )
                                                })"
                                        }
                                    }"
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}