package ru.starfarm.mode.service.view

import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.ApiManager
import ru.starfarm.core.profile.IProfileService
import ru.starfarm.core.scoreboard.IScoreboardService
import ru.starfarm.core.scoreboard.ScoreboardService
import ru.starfarm.core.util.CurrentMillis
import ru.starfarm.mode.*
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.event.PlayerInitializedEvent
import ru.starfarm.mode.service.player.playerScope
import ru.starfarm.mode.service.prestige.PrestigeService
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.function.Predicate
import kotlin.math.floor

object ViewService : BukkitService {

    private val scoreboardBuilder
        get() = ApiManager.newScoreboardBuilder().apply {
            title = "§b${"OpPrison".mini()}"
            addLine("   ${"&#ff7be2ᴡ&#ff7de6ᴡ&#fe7feaᴡ&#fe81ee.&#fe84f1s&#fe86f5ᴛ&#fd88f9ᴀ&#fd8afdʀ§dғ&#fd91edᴀ&#fe94e2ʀ&#fe98d6ᴍ&#fe9cca.&#fea0beғ&#ffa3b3ᴜ&#ffa7a7ɴ".hexed()}")// 1
            addLine("")
            addLine("")
            addLine("")
            addLine("") // 5
            addLine(" §9§l| &#00a1fbК&#148ffcо&#277dfdш&#148ffcе&#00a1fbл&#148ffcё&#277dfdк".hexed())
            addLine("")
            addLine("")
            addLine("")
            addLine("") // 10
            addLine("")
            addLine("")
            addLine("")
        }

    private val scoreboardUpdaterBuilder
        get() = scoreboardBuilder.apply {
            updaters.clear()

            addUpdater(20) { player, scoreboard ->
                val playerScope = player.playerScope

                scoreboard.setLine(
                    13,
                    "$NEUTRAL ${
                        LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(
                                CurrentMillis
                            ),
                            ZoneId.of("Europe/Moscow")
                        ).format(
                            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                        ).mini(true)
                    }"
                )
                scoreboard.setLine(11, " §b§l| §b${player.name.mini(true)}")
                scoreboard.setLine(
                    10,
                    " §b§l| §7${
                        Stats.PRESTIGE.run {
                            rowName.plus(": ").plus(colored(References.format(playerScope.getAmount(this))))
                        }
                    }")

                val rightPercent =
                    ((playerScope.getAmount(Stats.MONEY) / PrestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1)) * 100.0)
                        .run { if (this >= 100.0) 100 else toInt() }

                scoreboard.setLine(
                    9,
                    " §b§l| ${
                        if (playerScope.isMaxedPrestige) "§e§lМАКСИМУМ!" else "$NEUTRAL[${
                            buildString {
                                floor(rightPercent / 4.0).run { for (i in 0..25) append("${if (this@run != .0 && i <= this@run) POSITIVE else NEGATIVE}‖") }
                            }
                        }$NEUTRAL] $NEUTRAL($POSITIVE$rightPercent%$NEUTRAL)"
                    }"
                )
                scoreboard.setLine(
                    8,
                    " §b§l| §7${
                        Stats.BLOCKS.run {
                            rowName.plus(": ")
                                .plus(colored(References.formatWithLetters(playerScope.getAmount(this), "###.##")))
                        }
                    }"
                )

                scoreboard.setLine(
                    5,
                    " §9§l| §7${
                        Stats.MONEY.run {
                            rowName.plus(": ").plus(colored(References.formatWithLetters(playerScope.getAmount(this))))
                        }
                    }"
                )
                scoreboard.setLine(
                    4,
                    " §9§l| §7${
                        Stats.TOKENS.run {
                            rowName.plus(": ").plus(colored(References.formatWithLetters(playerScope.getAmount(this), "###.#")))
                        }
                    }"
                )
                scoreboard.setLine(
                    3,
                    " §9§l| §7${
                        Stats.EMERALDS.run {
                            rowName.plus(": ")
                                .plus(colored(References.formatWithLetters(playerScope.getAmount(this), "###.##")))
                        }
                    }"
                )
            }
        }

    init {
        Plugin.registerService(IScoreboardService::class.java, ScoreboardService())

        Plugin.on<PlayerInitializedEvent> { scoreboardUpdaterBuilder.build(player) }
        ApiManager.newTabBuilder()
            .setAccess { player, receiver -> receiver.canSee(player) || !receiver.canSee(player) }
            .setSortWeight {
                it.playerScope.group.type.ordinal
            }
            .setTab {
                val profile = IProfileService.Service.getProfile(it)

                "${it.playerScope.group} ${profile?.hideData?.name ?: profile?.name}${profile?.title.run { if (this == null) "" else " §7[$this]" }}§r"
            }
            .setTag { player, hologram ->
                hologram.access = Predicate { it !== player && it.canSee(player) }
                val profile = IProfileService.Service.getProfile(player)

                hologram.textLine(
                    0,
                    "${player.playerScope.group} ${profile?.hideData?.name ?: profile?.name}${profile?.title.run { if (this == null) "" else " §7[$this]" }}§r"
                )
            }
            .register()
    }
}