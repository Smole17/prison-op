package ru.starfarm.mode.service.skill

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEGATIVE
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.service.item.`object`.Items
import ru.starfarm.mode.service.item.`object`.impl.booster.BoosterItem
import ru.starfarm.mode.service.item.`object`.impl.crate.CrateItem
import ru.starfarm.mode.service.item.`object`.impl.group.GroupItem
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.playerScope
import java.time.Duration
import kotlin.math.floor

enum class Skills(private val slot: Int, val skillScope: SkillScope) {

    MINING(
        13,
        SkillScope(
            ItemUtil.of(Material.GOLDEN_PICKAXE).apply {
                name = "§eКопание"
                lore(
                    "",
                    "${NEUTRAL}Повышайте уровень навыка, путём",
                    "${NEUTRAL}копания блоков, и получайте",
                    "${NEUTRAL}награды за проделанные успехи."
                )
            }.build(),
            levels = arrayListOf(
                SkillLevel(0) {},
                SkillLevel(
                    150,
                    "",
                    "§fНаграды:",
                    "§7 + 500.000 Токенов"
                ) { addAmount(Stats.TOKENS, 5e5) },
                SkillLevel(
                    250,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T1 ключ х4"
                ) { (Items.MINE_T1.itemScope as CrateItem).addItem(player, 4) },
                SkillLevel(
                    400,
                    "",
                    "§fНаграды:",
                    "§7 + 750.000 Токенов"
                ) { addAmount(Stats.TOKENS, 75e4) },
                SkillLevel(
                    700,
                    "",
                    "§fНаграды:",
                    "§7 + 1.250.000 Токенов"
                ) { addAmount(Stats.TOKENS, 125e4) },
                SkillLevel(
                    1250,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T2 ключ x2"
                ) { (Items.MINE_T2.itemScope as CrateItem).addItem(player, 2) },

                // 5

                SkillLevel(
                    1800,
                    "",
                    "§fНаграды:",
                    "§7 + 1.000.000 Токенов",
                    "§7 + Шахтёрский T1 ключ x6"
                ) {
                    addAmount(Stats.TOKENS, 1e6)
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 6)
                },
                SkillLevel(
                    2500,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T3 ключ x4"
                ) { (Items.MINE_T3.itemScope as CrateItem).addItem(player, 4) },
                SkillLevel(
                    3200,
                    "",
                    "§fНаграды:",
                    "§7 + 3.000.000 Токенов",
                ) { addAmount(Stats.TOKENS, 3e6) },
                SkillLevel(
                    4700,
                    "",
                    "§fНаграды:",
                    "§7 + 2.000.000 Токенов",
                    "§7 + Шахтёрский T1 ключ x8"
                ) {
                    addAmount(Stats.TOKENS, 2e6)
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 8)
                },
                SkillLevel(
                    6300,
                    "",
                    "§fНаграды:",
                    "§7 + 1.500.000 Токенов",
                    "§7 + Шахтёрский T2 ключ x10",
                    "§7 + +100% Бустер Токенов (15 мин.)"
                ) {
                    addAmount(Stats.TOKENS, 15e5)
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 10)
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(15), 1.0)
                },

                // 10

                SkillLevel(
                    7300,
                    "",
                    "§fНаграды:",
                    "§7 + 4.000.000 Токенов",
                ) { addAmount(Stats.TOKENS, 4e6) },
                SkillLevel(
                    8540,
                    "",
                    "§fНаграды:",
                    "§7 + 6.000.000 Токенов",
                ) { addAmount(Stats.TOKENS, 6e6) },
                SkillLevel(
                    10000,
                    "",
                    "§fНаграды:",
                    "§7 + 4.500.000 Токенов",
                    "§7 + Шахтёрский T3 ключ x4"
                ) {
                    addAmount(Stats.TOKENS, 45e5)
                    (Items.MINE_T3.itemScope as CrateItem).addItem(player, 4)
                },
                SkillLevel(
                    12000,
                    "",
                    "§fНаграды:",
                    "§7 + 5.000.000 Токенов",
                    "§7 + Шахтёрский T1 ключ x10"
                ) {
                    addAmount(Stats.TOKENS, 5e6)
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 10)
                },
                SkillLevel(
                    14500,
                    "",
                    "§fНаграды:",
                    "§7 + EARTH (1 дн.)",
                ) { (Items.EARTH.itemScope as GroupItem).addItem(player, Duration.ofDays(1)) },

                // 15

                SkillLevel(
                    16750,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T3 ключ x6"
                ) {
                    addAmount(Stats.TOKENS, 1e6)
                    (Items.MINE_T3.itemScope as CrateItem).addItem(player, 6)
                },
                SkillLevel(
                    18050,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T1 ключ x12",
                    "§7 + Шахтёрский T2 ключ x8"
                ) {
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 12)
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 8)
                },
                SkillLevel(
                    20000,
                    "",
                    "§fНаграды:",
                    "§7 + Драгоценный ключ x4"
                ) { addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 4) },
                SkillLevel(
                    22500,
                    "",
                    "§fНаграды:",
                    "§7 + 3.000.000 Токенов",
                    "§7 + Шахтёрский T3 ключ x8",
                    "§7 + Реликвийный ключ x1"
                ) {
                    addAmount(Stats.TOKENS, 3e6)
                    (Items.MINE_T3.itemScope as CrateItem).addItem(player, 8)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 1)
                },
                SkillLevel(
                    25500,
                    "",
                    "§fНаграды:",
                    "§7 + 12.500.000 Токенов",
                    "§7 + +200% Бустер Денег (15 мин.)",
                ) {
                    addAmount(Stats.TOKENS, 125e5)
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(15), 2.0)
                },

                // 20

                SkillLevel(
                    29900,
                    "",
                    "§fНаграды:",
                    "§7 + 11.250.000 Токенов",
                    "§7 + Драгоценный ключ x4",
                    "§7 + Реликвийный ключ x2"
                ) {
                    addAmount(Stats.TOKENS, 1125e4)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 4)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 2)
                },
                SkillLevel(
                    33010,
                    "",
                    "§fНаграды:",
                    "§7 + 13.000.000 Токенов",
                    "§7 + Шахтёрский T3 ключ x16",
                    "§7 + Драгоценный ключ x12",
                    "§7 + Реликвийный ключ x3"
                ) {
                    addAmount(Stats.TOKENS, 13e6)
                    (Items.MINE_T3.itemScope as CrateItem).addItem(player, 16)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 12)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 3)
                },
                SkillLevel(
                    37025,
                    "",
                    "§fНаграды:",
                    "§7 + 20.000.000 Токенов",
                ) {
                    addAmount(Stats.TOKENS, 2e7)
                    (Items.MYTHICAL.itemScope as CrateItem).addItem(player)
                },
                SkillLevel(
                    44050,
                    "",
                    "§fНаграды:",
                    "§7 + 10.000.000 Токенов",
                    "§7 + Шахтёрский T2 ключ x16",
                    "§7 + +150% Бустер Токенов (15 мин.)"
                ) {
                    addAmount(Stats.TOKENS, 1e7)
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 16)
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(15), 1.5)
                },
                SkillLevel(
                    55500,
                    "",
                    "§fНаграды:",
                    "§7 + AQUA (1 дн.)",
                    "§7 + Мистический ящик х1",
                    "§7 + +200% Бустер Денег (30 мин.)",
                ) {
                    (Items.AQUA.itemScope as GroupItem).addItem(player, Duration.ofDays(1))
                    (Items.MYTHICAL.itemScope as CrateItem).addItem(player)
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(30), 2.0)
                },

                // 25

                SkillLevel(
                    72120,
                    "",
                    "§fНаграды:",
                    "§7 + 30.000.000 Токенов",
                    "§7 + Реликвийный ключ x4"
                ) {
                    addAmount(Stats.TOKENS, 3e7)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 4)
                },
                SkillLevel(
                    92000,
                    "",
                    "§fНаграды:",
                    "§7 + 20.000.000 Токенов",
                    "§7 + Драгоценный ключ x10",
                    "§7 + Реликвийный ключ x5"
                ) {
                    addAmount(Stats.TOKENS, 2e7)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 10)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 5)
                },
                SkillLevel(
                    119860,
                    "",
                    "§fНаграды:",
                    "§7 + Реликвийный ключ x7"
                ) {
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 7)
                },
                SkillLevel(
                    148000,
                    "",
                    "§fНаграды:",
                    "§7 + 15.000.000 Токенов",
                    "§7 + +200% Бустер Денег (30 мин.)"
                ) {
                    addAmount(Stats.TOKENS, 15e6)
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(30), 2.0)
                },
                SkillLevel(
                    175680,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T1 ключ x32",
                    "§7 + +200% Бустер Токенов (30 мин.)",
                ) {
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 32)
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(30), 2.0)
                },

                // 30

                SkillLevel(
                    212500,
                    "",
                    "§fНаграды:",
                    "§7 + Драгоценный ключ x10",
                    "§7 + Реликвийный ключ x6"
                ) {
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 10)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 6)
                },
                SkillLevel(
                    281000,
                    "",
                    "§fНаграды:",
                    "§7 + 25.000.000 Токенов",
                    "§7 + +200% Бустер Денег (30 мин.)"
                ) {
                    addAmount(Stats.TOKENS, 25e6)
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(30), 2.0)
                },
                SkillLevel(
                    355600,
                    "",
                    "§fНаграды:",
                    "§7 + Мистический ящик x1",
                    "§7 + 15.000.000 Токенов",
                ) {
                    (Items.MYTHICAL.itemScope as CrateItem).addItem(player)
                    addAmount(Stats.TOKENS, 25e6)
                },
                SkillLevel(
                    466550,
                    "",
                    "§fНаграды:",
                    "§7 + 40.000.000 Токенов",
                ) {
                    addAmount(Stats.TOKENS, 4e7)
                },
                SkillLevel(
                    576555,
                    "",
                    "§fНаграды:",
                    "§7 + Реликвийный ключ x7",
                    "§7 + 15.000.000 Токенов",
                ) {
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 7)
                    addAmount(Stats.TOKENS, 15e6)
                },
            )
        ),
    ),
    BREAKING_LAYER(
        10,
        SkillScope(
            ItemUtil.of(Material.TNT).apply {
                name = "§cЛомание Слоя"
                lore(
                    "",
                    "${NEUTRAL}Повышайте уровень навыка, путём",
                    "${NEUTRAL}ломания слоя блоков при помощи",
                    "${NEUTRAL}зачарования \"Отбойный Молоток\", и получайте",
                    "${NEUTRAL}награды за проделанные успехи."
                )
            }.build(),
            levels = arrayListOf(
                SkillLevel(0) {},
                SkillLevel(
                    6,
                    "",
                    "§fНаграды:",
                    "§7 + 700.000 Токенов"
                ) { addAmount(Stats.TOKENS, 7e5) },
                SkillLevel(
                    12,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T2 ключ x1"
                ) { (Items.MINE_T2.itemScope as CrateItem).addItem(player, 1) },
                SkillLevel(
                    18,
                    "",
                    "§fНаграды:",
                    "§7 + 1.000.000 Токенов"
                ) { addAmount(Stats.TOKENS, 1e6) },
                SkillLevel(
                    24,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T1 ключ x6"
                ) { (Items.MINE_T1.itemScope as CrateItem).addItem(player, 6) },
                SkillLevel(
                    29,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T1 ключ x2",
                    "§7 + Шахтёрский T2 ключ x2"
                ) {
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 2)
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 2)
                },

                // 5

                SkillLevel(
                    40,
                    "",
                    "§fНаграды:",
                    "§7 + 2.000.000 Токенов",
                ) { addAmount(Stats.TOKENS, 2e6) },
                SkillLevel(
                    65,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T2 ключ x3"
                ) { (Items.MINE_T2.itemScope as CrateItem).addItem(player, 3) },
                SkillLevel(
                    96,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T1 ключ x8"
                ) { (Items.MINE_T1.itemScope as CrateItem).addItem(player, 8) },
                SkillLevel(
                    112,
                    "",
                    "§fНаграды:",
                    "§7 + 1.000.000 Токенов",
                    "§7 + Шахтёрский T2 ключ x2"
                ) {
                    addAmount(Stats.TOKENS, 1e6)
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 2)
                },
                SkillLevel(
                    143,
                    "",
                    "§fНаграды:",
                    "§7 + 1.500.000 Токенов",
                    "§7 + Драгоценный ключ х1"
                ) {
                    addAmount(Stats.TOKENS, 15e5)
                    addCrate(Crates.PRECIOUS, 1)
                },

                // 10

                SkillLevel(
                    173,
                    "",
                    "§fНаграды:",
                    "§7 + AQUA (1 дн.)",
                ) { (Items.AQUA.itemScope as GroupItem).addItem(player, Duration.ofDays(1)) },
                SkillLevel(
                    199,
                    "",
                    "§fНаграды:",
                    "§7 + 6.000.000 Токенов",
                ) { addAmount(Stats.TOKENS, 6e6) },
                SkillLevel(
                    236,
                    "",
                    "§fНаграды:",
                    "§7 + 4.000.000 Токенов",
                    "§7 + Шахтёрский T1 ключ x10",
                ) {
                    addAmount(Stats.TOKENS, 4e6)
                    (Items.MINE_T1.itemScope as CrateItem).addItem(player, 10)
                },
                SkillLevel(
                    279,
                    "",
                    "§fНаграды:",
                    "§7 + 6.000.000 Токенов",
                    "§7 + Шахтёрский T2 ключ x3"
                ) {
                    addAmount(Stats.TOKENS, 6e6)
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 3)
                },
                SkillLevel(
                    312,
                    "",
                    "§fНаграды:",
                    "§7 + +150% Бустер Токенов (15 мин.)"
                ) { (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(15), 1.5) },

                // 15

                SkillLevel(
                    353,
                    "",
                    "§fНаграды:",
                    "§7 + Реликвийный ключ x1"
                ) {
                    addCrate(Crates.RELIC, 1)
                },
                SkillLevel(
                    412,
                    "",
                    "§fНаграды:",
                    "§7 + 6.000.000 Токенов",
                    "§7 + Шахтёрский T3 ключ x8",
                ) {
                    addAmount(Stats.TOKENS, 6e6)
                    (Items.MINE_T3.itemScope as CrateItem).addItem(player, 8)
                },
                SkillLevel(
                    478,
                    "",
                    "§fНаграды:",
                    "§7 + Драгоценный ключ x4"
                ) { addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 4) },
                SkillLevel(
                    567,
                    "",
                    "§fНаграды:",
                    "§7 + 7.500.000 Токенов",
                    "§7 + +200% Бустер Денег (15 мин.)",
                ) {
                    addAmount(Stats.TOKENS, 75e5)
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(15), 2.0)
                },
                SkillLevel(
                    662,
                    "",
                    "§fНаграды:",
                    "§7 + Реликвийный ключ x2"
                ) { addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 2) },

                // 20

                SkillLevel(
                    721,
                    "",
                    "§fНаграды:",
                    "§7 + 15.000.000 Токенов",
                ) { addAmount(Stats.TOKENS, 15e6) },
                SkillLevel(
                    789,
                    "",
                    "§fНаграды:",
                    "§7 + Шахтёрский T2 ключ x16",
                    "§7 + Драгоценный ключ x6",
                    "§7 + Реликвийный ключ x1"
                ) {
                    (Items.MINE_T2.itemScope as CrateItem).addItem(player, 16)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.PRECIOUS, 6)
                    addCrate(ru.starfarm.mode.service.player.`object`.crate.Crates.RELIC, 1)
                },
                SkillLevel(
                    863,
                    "",
                    "§fНаграды:",
                    "§7 + AIR (1 дн.)",
                ) { (Items.AIR.itemScope as GroupItem).addItem(player, Duration.ofDays(1)) },
                SkillLevel(
                    943,
                    "",
                    "§fНаграды:",
                    "§7 + 10.000.000 Токенов",
                    "§7 + +150% Бустер Токенов (20 мин.)"
                ) {
                    addAmount(Stats.TOKENS, 1e7)
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(20), 1.5)
                },
                SkillLevel(
                    1003,
                    "",
                    "§fНаграды:",
                    "§7 + Мистический ящик х1",
                    "§7 + +200% Бустер Денег (30 мин.)",
                ) {
                    (Items.MYTHICAL.itemScope as CrateItem).addItem(player)
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(player, Duration.ofMinutes(30), 2.0)
                },

                // 25
            )
        )
    ),
    ;

    companion object {

        fun openSkillsGui(playerScope: PlayerScope) {
            ApiManager.createInventoryContainer(
                "${NEUTRAL}Навыки", 3
            ) { _, container ->
                Skills.values().forEach {
                    container.addItem(
                        it.slot,
                        ItemUtil.of(it.skillScope.itemStack.clone()).apply {
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS)

                            val pastLore = lore?.toList()
                            val currentSkill = playerScope.getCurrentSkill(it)

                            val currentExp = currentSkill.exp

                            lore(
                                "",
                                "§fВаш уровень: §e§n${currentSkill.level}${if (currentSkill.isMaxLevel) "$NEUTRAL(§eМАКСИМАЛЬНЫЙ$NEUTRAL)" else ""}",
                            ).apply lore@{
                                if (currentSkill.isMaxLevel) return@lore else {
                                    val neededExp = currentSkill.levels[currentSkill.level + 1].exp

                                    val rightPercent =
                                        ((currentExp / neededExp.toDouble()) * 100.0).run { if (this >= 100.0) 100 else toInt() }

                                    addLore(
                                        "$NEUTRAL[".plus(buildString {
                                            floor(rightPercent / 2.0).run {
                                                for (i in 0..50) append("${if (this@run != .0 && i <= this@run) POSITIVE else NEGATIVE}‖")
                                            }
                                        }).plus("$NEUTRAL] §7($currentExp/$neededExp опыта)")

                                    )
                                }
                            }

                            addLore(pastLore!!)
                            addLore("", "§eНажмите, чтобы открыть список уровней!")
                        }.build()
                    ) { _, _ -> it.openProgressiveGui(playerScope) }
                }
            }.openInventory(playerScope.player)
        }
    }

    fun openProgressiveGui(playerScope: PlayerScope) {
        val skillLevels = skillScope.levels

        ApiManager.createInventoryContainer(
            "${NEUTRAL}${ChatColor.stripColor(skillScope.itemStack.itemMeta?.displayName)}",
            ((skillLevels.size / 9) + 1).run { if (this >= 6) 6 else this }
        ) { _, container ->
            skillLevels.filter { it.exp != 0 }.forEachIndexed { index, it ->
                container.addItem(
                    it.getBaseItem(
                        this,
                        index,
                        playerScope = playerScope
                    )
                )
            }
        }.openInventory(playerScope.player)
    }
}