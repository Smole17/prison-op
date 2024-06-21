package ru.starfarm.mode.service.shop

import org.bukkit.entity.Player
import java.time.Duration
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.service.item.`object`.Items
import ru.starfarm.mode.service.item.`object`.impl.flight.FlightItem
import ru.starfarm.mode.service.item.`object`.impl.group.GroupItem
import ru.starfarm.mode.service.player.PlayerService
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates

enum class Shops(private val shopScopeFunction: (PlayerService) -> ShopScope) {

    EMERALD({ playerService ->
        object : ShopScope("${NEUTRAL}Магический Торговец", 5, playerService) {
            override fun render() {
                (Items.FLIGHT.itemScope as FlightItem).apply {
                    Duration.ofMinutes(1).also {
                        addItem(
                            10,
                            ItemUtil.of(getFormattedStack(it)).build(), Stats.EMERALDS, 75.0
                        ) {
                            addItem(player, it)
                        }
                    }

                    Duration.ofMinutes(3).also {
                        addItem(
                            11,
                            ItemUtil.of(getFormattedStack(it)).amount(3).build(),
                            Stats.EMERALDS,
                            200.0
                        ) {
                            addItem(player, it)
                        }
                    }

                    Duration.ofMinutes(5).also {
                        addItem(
                            12,
                            ItemUtil.of(getFormattedStack(it)).amount(5).build(),
                            Stats.EMERALDS,
                            325.0
                        ) {
                            addItem(player, it)
                        }
                    }
                }

                (Crates.PRECIOUS).apply {
                    addItem(
                        13,
                        ItemUtil.of(icon.clone())
                            .apply {
                                name = "$chatColor${itemStack.itemMeta?.displayName} §fключ"
                            }
                            .build(),
                        Stats.EMERALDS,
                        100.0,
                        false,
                    ) {
                        addCrate(this@apply)
                    }

                    addItem(
                        14,
                        ItemUtil.of(icon.clone())
                            .apply {
                                name = "$chatColor${itemStack.itemMeta?.displayName} §fключ"
                                amount = 3
                            }
                            .build(),
                        Stats.EMERALDS,
                        250.0,
                        false,
                    ) {
                        addCrate(this@apply, 3)
                    }
                }

                (Crates.RELIC).apply {
                    addItem(
                        15,
                        ItemUtil.of(icon.clone())
                            .apply {
                                name = "$chatColor${itemStack.itemMeta?.displayName} §fключ"
                            }
                            .build(),
                        Stats.EMERALDS,
                        150.0,
                        false,
                    ) {
                        addCrate(this@apply)
                    }

                    addItem(
                        16,
                        ItemUtil.of(icon.clone())
                            .apply {
                                name = "$chatColor${itemStack.itemMeta?.displayName} §fключ"
                                amount = 2
                            }
                            .build(),
                        Stats.EMERALDS,
                        275.0,
                        false,
                    ) {
                        addCrate(this@apply, 2)
                    }
                }
                (Crates.DESIGN).apply {
                    addItem(
                        19,
                        ItemUtil.of(icon.clone())
                            .apply {
                                name = "$chatColor${itemStack.itemMeta?.displayName} §fключ"
                            }
                            .build(),
                        Stats.EMERALDS,
                        500.0,
                        false,
                    ) {
                        addCrate(this@apply)
                    }
                }

                (Items.SKY.itemScope as GroupItem).apply {
                    Duration.ofDays(1).also {
                        addItem(
                            20,
                            ItemUtil.of(getFormattedStack(it)).build(),
                            Stats.EMERALDS,
                            1250.0
                        ) {
                            addItem(player, it)
                        }
                    }
                }
                (Items.COSMOS.itemScope as GroupItem).apply {
                    Duration.ofDays(1).also {
                        addItem(
                            21,
                            ItemUtil.of(getFormattedStack(it)).build(),
                            Stats.EMERALDS,
                            2000.0
                        ) {
                            addItem(player, it)
                        }
                    }
                }
            }
        }
    })
    ;

    fun openShop(playerService: PlayerService, player: Player) =
        shopScopeFunction.invoke(playerService).openInventory(player)
}