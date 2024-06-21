package ru.starfarm.mode.service.prestige

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.require.Require
import ru.starfarm.core.profile.group.DonateGroup
import ru.starfarm.core.profile.group.StaffGroup
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.mode.*
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.playerScope

object PrestigeService : BukkitService {

    const val MAX_PRESTIGE = 3_000_000.0

    init {
        Plugin.taskContext.everyAsync(0, 20 * 30) {
            Bukkit.getOnlinePlayers().forEach {
                it.playerScope.apply {
                    if (isAutoPrestige) increasePrestige(this, false)
                }
            }
        }

        Plugin.registerCommands(
            object : Command<Player>(
                "autoprestige",
                "Улучшает автоматически престиж до максимально возможного.",
                "ap"
            ) {
                init {
                    addRequire(
                        Require.groups(
                            DonateGroup.PREMIUM_PLUS, DonateGroup.ELITE, DonateGroup.ELITE_PLUS,
                            DonateGroup.SPONSOR, DonateGroup.SPONSOR_PLUS, DonateGroup.UNIQUE,
                            StaffGroup.GAME_DESIGNER, StaffGroup.DEVELOPER, StaffGroup.ADMINISTRATOR
                        )
                    )
                }

                override fun execute(ctx: CommandContext<Player>) {
                    toggleAutoPrestige(ctx.sender)
                }
            },
            object :
                Command<Player>("maxprestige", "Улучшает престиж до максимально возможного.", "prestigemax", "mp") {
                override fun execute(ctx: CommandContext<Player>) {
                    increasePrestige(ctx.sender.playerScope, false)
                }
            },
            object : Command<Player>("prestige", "Улучшает престиж.", "pr") {
                override fun execute(ctx: CommandContext<Player>) {
                    increasePrestige(ctx.sender.playerScope)
                }
            }
        )
    }

    fun isAutoPrestige(player: Player): Boolean = player.playerScope.isAutoPrestige

    fun toggleAutoPrestige(player: Player) =
        player.playerScope.toggleAutoPrestige().apply {
            ChatUtil.sendMessage(
                player,
                "${if (this) POSITIVE else NEGATIVE}Вы ${if (this) "включили" else "выключили"} автоматическое улучшение престижа."
            )
        }

    fun getPrestigePrice(prestige: Double): Double = (prestige * 2e10).run { this + (this * 0.1) }

    private fun increasePrestige(playerScope: PlayerScope, single: Boolean = true) {
        val prestige = playerScope.getAmount(Stats.PRESTIGE)

        if (playerScope.isMaxedPrestige) {
            playerScope.isAutoPrestige = false
            return
        }

        var count = prestige + 1
        val moneyStats = Stats.MONEY
        val money = playerScope.getAmount(moneyStats)

        var price = getPrestigePrice(count)
        val player = playerScope.player

        if (money < price) return

        fun finalStage() {
            playerScope.subtractAmount(moneyStats, price)
            playerScope.setAmount(Stats.PRESTIGE, count)

            if (playerScope.isMaxedPrestige) return

            ChatUtil.sendMessage(player, "${POSITIVE}Вы улучшили престиж до ${References.format(count)}.")

            ChatUtil.sendMessage(
                player,
                "${POSITIVE}Цена ${References.format(++count)} престижа: ${
                    getPrestigePrice(count).run {
                        References.format(getPrestigePrice(count))
                            .plus(" $NEUTRAL§o(${References.formatWithLetters(this, "###.#")})$POSITIVE")
                    }
                } ${moneyStats}."
            )
        }

        if (!single) {
            Plugin.taskContext.asyncAfter(0) {
                while (money > price) {
                    val tempPrice = price + getPrestigePrice(count + 1)

                    if ((money < tempPrice) || (count >= MAX_PRESTIGE)) break

                    price += getPrestigePrice(++count)
                    if (count % 500 == .0) playerScope.addCrate(Crates.PRECIOUS)
                }

                finalStage()
            }

            return
        }

        finalStage()
    }
}