package ru.starfarm.mode.service.player.`object`.donate.`object`

import org.bukkit.Material
import ru.starfarm.core.ApiManager
import ru.starfarm.core.profile.group.DonateGroup
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.service.item.`object`.Items
import ru.starfarm.mode.service.item.`object`.impl.group.GroupItem
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.playerScope
import java.time.Duration
import java.util.function.Consumer

enum class Donates(private val donateBuilder: () -> Unit) {

    ABILITIES({
        ApiManager.newDonateAbilitiesBuilder("§b§lOpPrison", Material.DIAMOND_PICKAXE)
            .apply {
                ability(
                    DonateGroup.VIP,
                    "&c▸ &fБустер Денег - &b+5%"
                )

                ability(
                    DonateGroup.VIP_PLUS,
                    "&c▸ &fБустер Денег - &b+10%"
                )
                ability(
                    DonateGroup.PREMIUM,
                    "&c▸ &fБустер Денег - &b+15%"
                )
                ability(
                    DonateGroup.PREMIUM_PLUS,
                    "&c▸ &fБустер Денег - &b+20%",
                    "&c▸ &fДоступ к команде - &b/autoprestige",
                    "$NEUTRAL Автоматическое улучшение",
                    "$NEUTRAL престижа на все деньги."
                )

                ability(
                    DonateGroup.ELITE,
                    "&c▸ &fБустер Денег - &b+25%",
                    "&c▸ &fДоступ к команде - &b/autoprestige",
                    "$NEUTRAL Автоматическое улучшение",
                    "$NEUTRAL престижа на все деньги."
                )

                ability(
                    DonateGroup.ELITE_PLUS,
                    "&c▸ &fБустер Денег - &b+35%",
                    "&c▸ &fДоступ к команде - &b/autoprestige",
                    "$NEUTRAL Автоматическое улучшение",
                    "$NEUTRAL престижа на все деньги.",
                    "&c▸ &fДоступ к команде - &b/crates",
                    "$NEUTRAL Виртуальный доступ к ящикам."
                )

                ability(
                    DonateGroup.SPONSOR,
                    "&c▸ &fБустер Денег - &b+50%",
                    "&c▸ &fДоступ к команде - &b/autoprestige",
                    "$NEUTRAL Автоматическое улучшение",
                    "$NEUTRAL престижа на все деньги.",
                    "&c▸ &fДоступ к команде - &b/crates",
                    "$NEUTRAL Виртуальный доступ к ящикам."
                )

                ability(
                    DonateGroup.SPONSOR_PLUS,
                    "&c▸ &fБустер Денег - &b+75%",
                    "&c▸ &fДоступ к команде - &b/autoprestige",
                    "$NEUTRAL Автоматическое улучшение",
                    "$NEUTRAL престижа на все деньги.",
                    "&c▸ &fДоступ к команде - &b/crates",
                    "$NEUTRAL Виртуальный доступ к ящикам."
                )

                ability(
                    DonateGroup.UNIQUE,
                    "&c▸ &fБустер Денег - &b+100%",
                    "&c▸ &fДоступ к команде - &b/autoprestige",
                    "$NEUTRAL Автоматическое улучшение",
                    "$NEUTRAL престижа на все деньги.",
                    "&c▸ &fДоступ к команде - &b/crates",
                    "$NEUTRAL Виртуальный доступ к ящикам."
                )
            }
            .register()
    }),

    EARTH({
        ApiManager.run {
            val groupItem = (Items.EARTH.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 10

                price(25)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    AQUA({
        ApiManager.run {
            val groupItem = (Items.AQUA.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 11

                price(49)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    AIR({
        ApiManager.run {
            val groupItem = (Items.AIR.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 12

                price(79)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    SKY({
        ApiManager.run {
            val groupItem = (Items.SKY.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 13

                price(119)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    COSMOS({
        ApiManager.run {
            val groupItem = (Items.COSMOS.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 14

                price(159)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    SUN({
        ApiManager.run {
            val groupItem = (Items.SUN.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 15

                price(229)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    GALAXY({
        ApiManager.run {
            val groupItem = (Items.GALAXY.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 16

                price(299)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    UNIVERSE({
        ApiManager.run {
            val groupItem = (Items.UNIVERSE.itemScope as GroupItem)
            val duration = Duration.ofDays(3)

            val itemStack = groupItem.getFormattedStack(duration)
            val itemMeta = itemStack.itemMeta

            newDonateBuilder(itemMeta?.displayName ?: "").apply {
                icon = itemStack
                description = itemMeta?.lore?.toTypedArray() ?: arrayOf()

                slot = 19

                price(399)

                callback = Consumer { ctx -> ctx.player?.let { groupItem.addItem(it, duration) } }
            }
        }.register()
    }),
    PRECIOUS_KEY({
        ApiManager.run {
            val crate = Crates.PRECIOUS

            val itemStack = crate.icon.clone()
            val itemMeta = itemStack.itemMeta

            newDonateBuilder("${crate.chatColor}${itemMeta?.displayName ?: ""} §fключ").apply {
                icon = ItemUtil.of(itemStack).build()
                description(
                    "§7Можно открыть на спавне или прописав",
                    "§7команду /crates $NEUTRAL(от ELITE+ и выше)"
                )

                price(49)

                slot = 28

                countedType(20, 5)

                callback = Consumer { ctx ->
                    ctx.player?.playerScope?.addCrate(crate, ctx.count)
                }
            }
        }.register()
    }),
    RELIC_KEY({
        ApiManager.run {
            val crate = Crates.RELIC

            val itemStack = crate.icon.clone()
            val itemMeta = itemStack.itemMeta

            newDonateBuilder("${crate.chatColor}${itemMeta?.displayName ?: ""} §fключ").apply {
                icon = ItemUtil.of(itemStack).build()
                description(
                    "§7Можно открыть на спавне или прописав",
                    "§7команду /crates $NEUTRAL(от ELITE+ и выше)"
                )

                price(99)

                slot = 30

                countedType(20, 5)

                callback = Consumer { ctx ->
                    ctx.player?.playerScope?.addCrate(crate, ctx.count)
                }
            }
        }.register()
    }),
    BLOCKS_KEY({
        ApiManager.run {
            val crate = Crates.DESIGN

            val itemStack = crate.icon.clone()
            val itemMeta = itemStack.itemMeta

            newDonateBuilder("${crate.chatColor}${itemMeta?.displayName ?: ""} §fключ").apply {
                icon = ItemUtil.of(itemStack).build()
                description(
                    "§7Можно открыть на спавне или прописав",
                    "§7команду /crates $NEUTRAL(от ELITE+ и выше)"
                )

                slot = 32

                price(69)

                countedType(4, 5)

                callback = Consumer { ctx ->
                    ctx.player?.playerScope?.addCrate(crate, ctx.count)
                }
            }
        }.register()
    }),
    MYTHICAL_KEY({
        ApiManager.run {
            val crate = Crates.MYTHICAL

            val itemStack = crate.icon.clone()
            val itemMeta = itemStack.itemMeta

            newDonateBuilder("${crate.chatColor}${itemMeta?.displayName ?: ""} §fключ").apply {
                icon = ItemUtil.of(itemStack).build()
                description(
                    "§7Можно открыть на спавне или прописав",
                    "§7команду /crates $NEUTRAL(от ELITE+ и выше)"
                )

                slot = 34

                price(299)

                countedType(3, 5)

                callback = Consumer { ctx ->
                    ctx.player?.playerScope?.addCrate(crate, ctx.count)
                }
            }
        }.register()
    }),
    ;

    fun register() = donateBuilder.invoke()
}