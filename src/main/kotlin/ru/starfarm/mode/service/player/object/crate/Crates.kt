package ru.starfarm.mode.service.player.`object`.crate

import com.comphenix.protocol.wrappers.EnumWrappers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import ru.starfarm.core.ApiManager
import ru.starfarm.core.entity.impl.FakeArmorStand
import ru.starfarm.core.entity.type.Interact
import ru.starfarm.core.hologram.Hologram
import ru.starfarm.core.hologram.impl.line.TextHologramLine
import ru.starfarm.core.inventory.container.Container
import ru.starfarm.core.inventory.item.ClickItem
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEGATIVE
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.References
import ru.starfarm.mode.service.item.`object`.Items
import ru.starfarm.mode.service.item.`object`.impl.booster.BoosterItem
import ru.starfarm.mode.service.item.`object`.impl.crate.CrateItem
import ru.starfarm.mode.service.item.`object`.impl.flight.FlightItem
import ru.starfarm.mode.service.item.`object`.impl.group.GroupItem
import ru.starfarm.mode.service.item.`object`.impl.material.MaterialItem
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.playerScope
import java.time.Duration
import java.util.UUID
import java.util.function.BiConsumer
import java.util.function.Predicate

enum class Crates(
    val chatColor: ChatColor,
    private val slot: Int,
    private val x: Double, private val y: Double, private val z: Double,
    val icon: ItemStack = ItemUtil.of(Material.AIR).build(),
    private val crateScope: CrateScope,
) {

    DESIGN(
        ChatColor.LIGHT_PURPLE,
        4,
        45.5, 101.0, .5,
        ItemUtil.of(Material.PHANTOM_MEMBRANE)
            .name("Дизайн")
            .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            .build(),
        CrateScope(
            "ДИЗАЙН",
            CratesType.DEFAULT,
            listOf(

                /**
                 * COMMON
                 */

                Material.MOSSY_COBBLESTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.COMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.STONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.COMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.COAL_ORE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.COMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.TERRACOTTA.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.COMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },

                /**
                 * UNCOMMON
                 */

                Material.IRON_ORE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.UNCOMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.GOLD_ORE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.UNCOMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.STONE_BRICKS.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.UNCOMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.GRAY_TERRACOTTA.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.UNCOMMON
                    ) { MaterialItem.addItem(it.player, this); null }
                },

                /**
                 * RARE
                 */

                Material.DIAMOND_ORE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.SANDSTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.CUT_SANDSTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.QUARTZ_BLOCK.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.QUARTZ_PILLAR.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },

                /**
                 * EPIC
                 */

                Material.EMERALD_ORE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EPIC
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.NETHERRACK.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EPIC
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.NETHER_GOLD_ORE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EPIC
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.SMOOTH_STONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EPIC
                    ) { MaterialItem.addItem(it.player, this); null }
                },

                /**
                 * LEGENDARY
                 */

                Material.BRICKS.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.LEGENDARY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.DARK_PRISMARINE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.LEGENDARY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.END_STONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.LEGENDARY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.RED_SANDSTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.LEGENDARY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.CUT_RED_SANDSTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.LEGENDARY
                    ) { MaterialItem.addItem(it.player, this); null }
                },

                /**
                 * EXCEEDINGLY RARE
                 */

                Material.PRISMARINE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EXCEEDINGLY_RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.RED_NETHER_BRICKS.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EXCEEDINGLY_RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.POLISHED_BLACKSTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EXCEEDINGLY_RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.PURPUR_BLOCK.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EXCEEDINGLY_RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.PURPUR_PILLAR.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EXCEEDINGLY_RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.OBSIDIAN.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.EXCEEDINGLY_RARE
                    ) { MaterialItem.addItem(it.player, this); null }
                },

                /**
                 * GODLY
                 */

                Material.POLISHED_BLACKSTONE_BRICKS.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.GODLY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.POLISHED_BLACKSTONE.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.GODLY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
                Material.CRYING_OBSIDIAN.run {
                    CrateItem(
                        ItemUtil.of(this)
                            .name(name.replace("_", " "))
                            .build(),
                        Rarities.GODLY
                    ) { MaterialItem.addItem(it.player, this); null }
                },
            )
        )
    ),
    MINE_T1(
        ChatColor.GRAY,
        10,
        37.5, 101.0, -10.5,
        ItemUtil.of(Material.GRAY_DYE)
            .name("Шахтёрский T1")
            .build(),
        CrateScope(
            "T1",
            CratesType.DEFAULT,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+45% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * .45

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 2)
                        .name("+60% Денег от следующего престижа")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * .6

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 3)
                        .name("+75% Денег от следующего престижа")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * .75

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("10.000 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 1e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("12.500 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 125e2

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("15.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 15e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 4)
                        .name("20.000 токенов")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 20e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 1 минуту")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(1))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.LIGHT_BLUE_DYE, 2, 12.toShort())
                        .name("Шахтёрские T2 ключи")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    (Items.MINE_T2.itemScope as CrateItem).addItem(
                        playerScope.player,
                        2
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.LAPIS_LAZULI, 1, 4.toShort())
                        .name("Шахтёрский T3 ключ")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    (Items.MINE_T3.itemScope as CrateItem).addItem(
                        playerScope.player
                    )
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа EARTH ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.EARTH.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AQUA ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AQUA.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    MINE_T2(
        ChatColor.AQUA,
        11,
        41.5, 101.0, -8.5,
        ItemUtil.of(Material.LIGHT_BLUE_DYE, 12.toShort())
            .name("Шахтёрский T2")
            .build(),
        CrateScope(
            "T2",
            CratesType.DEFAULT,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+50% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * .5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 2)
                        .name("+75% Денег от следующего престижа")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * .75

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 3)
                        .name("+135% Денег от следующего престижа")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 1.35

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("12.500 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 125e2

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("15.000 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 15e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("20.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 2e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 4)
                        .name("25.000 токенов")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 25e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 1 минуту")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(1))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.LAPIS_LAZULI, 2, 4.toShort())
                        .name("Шахтёрские T3 ключи")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    (Items.MINE_T3.itemScope as CrateItem).addItem(
                        playerScope.player,
                        2
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.YELLOW_DYE)
                        .name("Драгоценный ключ")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    playerScope.addCrate(PRECIOUS)
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа EARTH ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.EARTH.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AQUA ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AQUA.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    MINE_T3(
        ChatColor.DARK_BLUE,
        12,
        44.5, 101.0, -4.5,
        ItemUtil.of(Material.LAPIS_LAZULI)
            .name("Шахтёрский T3")
            .build(),
        CrateScope(
            "T3",
            CratesType.DEFAULT,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+60% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 0.6

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 2)
                        .name("+70% Денег от следующего престижа")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 0.7

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 3)
                        .name("+100% Денег от следующего престижа")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 1.0

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("25.000 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 25e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("30.000 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 3e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("40.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 4e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 4)
                        .name("50.000 токенов")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 5e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 1 минуту")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 3 минуты")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(3))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.LAPIS_LAZULI, 2)
                        .name("Шахтёрские T3 ключи")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    (Items.MINE_T3.itemScope as CrateItem).addItem(
                        playerScope.player,
                        2
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.YELLOW_DYE)
                        .name("Драгоценный ключ")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    playerScope.addCrate(PRECIOUS)
                    null
                },

                /**
                 * Boosters
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("§6+100% Бустер Денег $NEUTRAL(15 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(15),
                        1.0
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("§3+100% Бустер Токенов $NEUTRAL(15 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(15),
                        1.0
                    )
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AQUA ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AQUA.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AIR ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AIR.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    VULCAN(
        ChatColor.DARK_RED,
        14,
        44.5, 101.0, 5.5,
        ItemUtil.of(Material.ORANGE_DYE)
            .name("Вулканический")
            .build(),
        CrateScope(
            "ВУЛКАН",
            CratesType.DEFAULT,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+35% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 0.35

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("15.000 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 15e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("20.000 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 2e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("25.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 25e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 4)
                        .name("30.000 токенов")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 3e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 1 минуту")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 3 минуты")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(3))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.LAPIS_LAZULI, 2)
                        .name("Шахтёрские T3 ключи")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    (Items.MINE_T3.itemScope as CrateItem).addItem(
                        playerScope.player,
                        2
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.RED_DYE)
                        .name("Реликвийный ключ")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    playerScope.addCrate(RELIC)
                    null
                },

                /**
                 * Boosters
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("§6+125% Бустер Денег $NEUTRAL(20 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(20),
                        1.25
                    )
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AQUA ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AQUA.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AIR ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AIR.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    PRECIOUS(
        ChatColor.GOLD,
        15,
        41.5, 101.0, 9.5,
        ItemUtil.of(Material.YELLOW_DYE)
            .name("Драгоценный")
            .build(),
        CrateScope(
            "ДРАГ",
            CratesType.DEFAULT,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+150% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 1.5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 2)
                        .name("+175% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 1.75

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("35.000 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 35e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("50.000 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 5e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("75.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 75e3

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 4)
                        .name("100.000 токенов")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 1e5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 3 минуты")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(3))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.LAPIS_LAZULI, 2)
                        .name("Шахтёрские T3 ключи")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    (Items.MINE_T3.itemScope as CrateItem).addItem(
                        playerScope.player,
                        2
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.RED_DYE)
                        .name("Реликвийный ключ")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    playerScope.addCrate(RELIC)
                    null
                },

                /**
                 * Boosters
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("§6+150% Бустер Денег $NEUTRAL(20 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(20),
                        1.5
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("§3+150% Бустер Токенов $NEUTRAL(20 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(20),
                        1.5
                    )
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа AIR ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.AIR.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа SKY ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.SKY.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа COSMOS ${NEUTRAL}(1 день)")
                        .build(), Rarities.GODLY
                ) { playerScope ->
                    (Items.COSMOS.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    RELIC(
        ChatColor.RED,
        16,
        37.5, 101.0, 11.5,
        ItemUtil.of(Material.RED_DYE)
            .name("Реликвийный")
            .data(1)
            .build(),
        CrateScope(
            "РЕЛИК",
            CratesType.DEFAULT,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+200% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 2.0

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 2)
                        .name("+250% Денег от следующего престижа")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 2.5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("100.000 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 1e5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("150.000 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 15e4

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("200.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 2e5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 3 минуты")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(3))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 5 минут")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(5))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.LAPIS_LAZULI, 2)
                        .name("Шахтёрские T3 ключи")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    (Items.MINE_T3.itemScope as CrateItem).addItem(
                        playerScope.player,
                        2
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.RED_DYE, 2)
                        .name("Реликвийные ключи")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    playerScope.addCrate(RELIC)
                    null
                },

                /**
                 * Boosters
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("§3+175% Бустер Токенов $NEUTRAL(15 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(15),
                        1.75
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("§3+225% Бустер Токенов $NEUTRAL(30 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(30),
                        2.25
                    )
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа COSMOS ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.COSMOS.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа SUN ${NEUTRAL}(1 день)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.SUN.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа GALAXY ${NEUTRAL}(1 день)")
                        .build(), Rarities.GODLY
                ) { playerScope ->
                    (Items.GALAXY.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    MYTHICAL(
        ChatColor.DARK_PURPLE,
        22,
        38.5, 101.0, 0.5,
        ItemUtil.of(Material.ENDER_CHEST)
            .name("Мистический")
            .enchant(Enchantment.DIG_SPEED, 1)
            .build(),
        CrateScope(
            "МИСТИЧЕСКОГО",
            CratesType.MULTI_REWARD,
            listOf(
                /**
                 * Money for next prestige
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+700% Денег от следующего престижа")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 7.0

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT, 2)
                        .name("+850% Денег от следующего престижа")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.MONEY

                    val amount =
                        Plugin.prestigeService.getPrestigePrice(playerScope.getAmount(Stats.PRESTIGE) + 1) * 8.5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Tokens
                 */

                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("1.000.000 токенов")
                        .build(), Rarities.COMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 1e6

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 2)
                        .name("1.500.000 токенов")
                        .build(), Rarities.UNCOMMON
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 15e5

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 3)
                        .name("2.000.000 токенов")
                        .build(), Rarities.RARE
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 2e6

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM, 4)
                        .name("3.000.000 токенов")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    val stats = Stats.TOKENS

                    val amount = 3e6

                    playerScope.addAmount(stats, amount)

                    stats.colored(" §l+ ${References.format(amount)}", true)
                },

                /**
                 * Fly item
                 */

                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 5 минут")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(5))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.FEATHER)
                        .name("Полёт на 10 минут")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.FLIGHT.itemScope as FlightItem).addItem(playerScope.player, Duration.ofMinutes(10))
                    null
                },

                /**
                 * Keys
                 */

                CrateItem(
                    ItemUtil.of(Material.RED_DYE, 10)
                        .name("Реликвийные ключи")
                        .build(), Rarities.EPIC
                ) { playerScope ->
                    playerScope.addCrate(RELIC, 10)
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.RED_DYE, 15)
                        .name("Реликвийные ключи")
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    playerScope.addCrate(RELIC, 15)
                    null
                },

                /**
                 * Boosters
                 */

                CrateItem(
                    ItemUtil.of(Material.GOLD_INGOT)
                        .name("+300% Бустер Денег $NEUTRAL(45 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.MONEY_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(45),
                        3.0
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("+250% Бустер Токенов $NEUTRAL(25 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.LEGENDARY
                ) { playerScope ->
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(25),
                        2.5
                    )
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAGMA_CREAM)
                        .name("+300% Бустер Токенов $NEUTRAL(45 мин.)")
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.TOKEN_BOOSTER.itemScope as BoosterItem).addItem(
                        playerScope.player,
                        Duration.ofMinutes(45),
                        3.0
                    )
                    null
                },

                /**
                 * Ranks
                 */

                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа SUN ${NEUTRAL}(3 дня)")
                        .build(), Rarities.EXCEEDINGLY_RARE
                ) { playerScope ->
                    (Items.SUN.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(3))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа GALAXY ${NEUTRAL}(2 дня)")
                        .build(), Rarities.GODLY
                ) { playerScope ->
                    (Items.GALAXY.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(2))
                    null
                },
                CrateItem(
                    ItemUtil.of(Material.MAP)
                        .name("Группа UNIVERSE ${NEUTRAL}(1 день)")
                        .build(), Rarities.GODLY
                ) { playerScope ->
                    (Items.UNIVERSE.itemScope as GroupItem).addItem(playerScope.player, Duration.ofDays(1))
                    null
                },
            )
        )
    ),
    ;

    override fun toString(): String {
        return "${chatColor}${icon.clone().itemMeta?.displayName}"
    }

    companion object {

        @JvmStatic
        fun openCratesGui(playerScope: PlayerScope) {
            ApiManager.createInventoryContainer("${NEUTRAL}Ящики", 3) { _, container ->
                fillCrateGui(container, playerScope)
            }.openInventory(playerScope.player)
        }

        @JvmStatic
        private fun fillCrateGui(container: Container, playerScope: PlayerScope) {
            values().forEach {
                if (!playerScope.hasCrate(it)) playerScope.addCrate(it, 0)

                container.addItem(it.getClickItem(playerScope))

                fun getMessagesFromCratesStack() =
                    listOf(
                        "§eНажмите, чтобы переключить."
                    ).run {
                        if (playerScope.isMessagesFromCrates)
                            ItemUtil.of(Material.ENDER_EYE)
                                .name("§aСообщения о выпавших предметах")
                                .lore(this)
                                .build()
                        else
                            ItemUtil.of(Material.ENDER_PEARL)
                                .name("§cСообщения о выпавших предметах")
                                .lore(this)
                                .build()
                    }

                container.addItem(
                    ClickItem(18, getMessagesFromCratesStack()) { _, event ->
                        playerScope.isMessagesFromCrates = !playerScope.isMessagesFromCrates
                        event.currentItem = getMessagesFromCratesStack()
                    }
                )
            }
        }
    }

    fun spawnCrate(): Hologram =
        ApiManager.createHologram(Location(Bukkit.getWorlds()[0], x, y, z)).apply hologram@{
            click = BiConsumer { player, interact ->
                val playerScope = player.playerScope

                when (interact) {
                    Interact.ATTACK -> openCratesGui(playerScope)
                    Interact.CLICK -> crateScope.openPreview(playerScope)
                }
            }

            FakeArmorStand(location.clone().add(.0, 2.0, .0)).apply {
                invisible = true
                click = this@hologram.click
                setHeadRotation(0f, 90f, 0f)

                setItem(EnumWrappers.ItemSlot.HEAD, icon.clone())
            }

            textLine("§7ПКМ: ${chatColor}просмотреть§7 содержимое.")
            textLine("§7ЛКМ: ${chatColor}открыть§7 этот ящик.")
            emptyLine()
            textLine("")
            textLine("$chatColor${icon.itemMeta?.displayName} §fящик${if (crateScope.crateType == CratesType.MULTI_REWARD) " §e§lx8 НАГРАД!" else ""}")
            lines.values.filterIsInstance<TextHologramLine>().map(TextHologramLine::entity).forEach {
                it.small = false
                it.marker = false
            }
        }

    private fun getClickItem(playerScope: PlayerScope): ClickItem = ClickItem(
        slot, ItemUtil.of(icon.clone())
            .apply {
                name =
                    "${chatColor}$name${if (crateScope.crateType == CratesType.MULTI_REWARD) " §e§lx8 НАГРАД!" else ""}"
            }
            .lore(
                "§fВы имеете $chatColor§n${playerScope.getCrateAmount(this@Crates)}§f ключей от этого ящика.",
                "",
                "§7ЛКМ: ${chatColor}открыть§7 этот ящик.",
                "§7ПКМ: ${chatColor}просмотреть§7 содержимое.",
                "§7Выбросить Предмет: ${chatColor}открыть§7 все ящики этого типа."
            )
            .addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS
            )
            .build()
    ) event@{ container, event ->
        when (event.click) {
            ClickType.LEFT -> 1
            ClickType.DOUBLE_CLICK -> 1
            ClickType.DROP -> playerScope.getCrateAmount(this)
                .run { if (crateScope.crateType === CratesType.MULTI_REWARD || this@Crates == DESIGN) 1 else if (this >= 1000) 1000 else this }

            else -> {
                crateScope.openPreview(playerScope)
                0
            }
        }.apply {
            if (!playerScope.hasCrate(this@Crates) || this <= 0 || crateScope.isItemsEmpty()) return@event
            if (crateScope.crateType == CratesType.MULTI_REWARD && playerScope.openingMultiRewardsCrates >= 3) {
                ChatUtil.sendMessage(playerScope.player, "${NEGATIVE}Вы не можете открывать больше 3 ящиков за раз данного типа.")
                return@event
            }

            playerScope.subtractCrate(this@Crates, this)
            Plugin.taskContext.asyncAfter(0) { repeat(this) { openCrate(playerScope) } }

            fillCrateGui(container, playerScope)
        }
    }

    private fun openCrate(playerScope: PlayerScope) =
        crateScope.openCrate(playerScope)
}