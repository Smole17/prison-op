package ru.starfarm.mode.service.item.`object`.impl.pickaxe

import com.google.common.cache.CacheBuilder
import net.minecraft.server.v1_16_R3.NBTTagString
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.*
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.playerScope
import java.util.*
import java.util.concurrent.TimeUnit

private val BlockMinings =
    CacheBuilder.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .removalListener<String, Double> { notification ->
            ((Bukkit.getPlayer(notification.key)?.playerScope?.getAmount(Stats.BLOCKS) ?: return@removalListener) - notification.value).apply {
                if (this >= 70) Bukkit.getOnlinePlayers().filter { (it.playerScope.profile?.staffGroup?.ordinal ?: 0) >= 7 }.forEach {
                    it.sendMessage("${notification.key} is broke ${References.format(this)} blocks for 3 seconds.")
                }
            }
        }
        .build<String, Double>()

class PickaxeItem : ItemScope(
    "pickaxe",
    ItemUtil.of(Material.DIAMOND_PICKAXE)
        .unbreakable(true)
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
        .build()
) {
    init {
        clickHandler = clickHandler@{ event, playerScope ->
            if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK)
                return@clickHandler

            putUpgrades(playerScope)
            openUpgradeGui(event.player, playerScope)
        }
    }

    val upgrades = EnumMap<PickaxeUpgrades, Int>(PickaxeUpgrades::class.java).apply {
        PickaxeUpgrades.values().forEach {
            put(it, it.beginLevel)
        }
    }

    var exp = .0
    private var level = 0
    private val toggleEnchantments = LinkedList<PickaxeUpgrades>()
    val neededExp get() = 8000

    fun handleAllEnchants(location: Location, playerScope: PlayerScope) {
        BlockMinings.asMap().putIfAbsent(playerScope.name!!, playerScope.getAmount(Stats.BLOCKS))

        playerScope.addAmount(Stats.BLOCKS)
        playerScope.pickaxe.exp += 1.0
        addLevel(playerScope)

        playerScope.pickaxe.upgrades.forEach {
            val upgrade = it.key

            if (isToggledEnchantment(playerScope, upgrade)) return@forEach

            val level = it.value

            if (level <= 0) return@forEach

            upgrade.handle(location, playerScope, upgrade, level)
        }
    }

    fun addItem(playerScope: PlayerScope, player: Player = playerScope.player) {
        val itemBuilder = ItemUtil.of(itemStack.clone())
        val pickaxe = playerScope.pickaxe

        itemBuilder.name("§fКирка $POSITIVE${player.displayName}")
        itemBuilder.lore(
            "§7Уровень: ${pickaxe.level}",
            pickaxe.expPercent,
            ""
        )

        itemBuilder.enchant(
            Enchantment.DIG_SPEED,
            playerScope.getPickaxeUpgradeLevel(PickaxeUpgrades.EFFICIENCY).run {
                if (this >= 200) 200 else this
            }
        )

        pickaxe.upgrades.forEach { if (it.value > 0) itemBuilder.addLore("${NEUTRAL}> ${it.key} §f${References.format(it.value.toDouble())}") }

        super.addItem(player, itemBuilder.build(), true) {
            it != null && ItemUtil.getTags(it).values.any { tag -> tag is NBTTagString && tag.asString() == id }
        }
    }

    fun toggleEnchantment(playerScope: PlayerScope, upgrade: PickaxeUpgrades) {
        val toggleEnchantments = playerScope.pickaxe.toggleEnchantments
        if (isToggledEnchantment(playerScope, upgrade)) {
            toggleEnchantments.remove(upgrade)
            return
        }

        toggleEnchantments.add(upgrade)
    }

    fun isToggledEnchantment(playerScope: PlayerScope, upgrade: PickaxeUpgrades): Boolean =
        playerScope.pickaxe.toggleEnchantments.contains(upgrade)

    private fun addLevel(playerScope: PlayerScope) {
        val pickaxe = playerScope.pickaxe
        if (pickaxe.exp < pickaxe.neededExp) return
        pickaxe.level += 1
        pickaxe.exp = .0

        if (pickaxe.level % 5 == 0)
            playerScope.addCrate(Crates.RELIC)

        ChatUtil.sendMessage(
            playerScope.player,
            "${POSITIVE}Вы повысили уровень кирки до ${pickaxe.level}."
        )
        addItem(playerScope)
    }

    private fun putUpgrades(playerScope: PlayerScope) {
        val enumUpgrades = PickaxeUpgrades.values()
        val pickaxeUpgrades = playerScope.pickaxe.upgrades

        if (enumUpgrades.size == pickaxeUpgrades.size) return

        enumUpgrades.forEach {
            pickaxeUpgrades.putIfAbsent(it, it.beginLevel)
        }
    }

    private fun openUpgradeGui(player: Player, playerScope: PlayerScope) {
        if (!playerScope.isPickaxeInitialized) return

        ApiManager.createInventoryContainer("${NEUTRAL}Улучшение", 4) { _, container ->
            playerScope.pickaxe.upgrades.forEach {
                container.addItem(
                    it.key.getClickItem(
                        container,
                        it.value,
                        playerScope
                    )
                )
            }
        }.openInventory(player)
    }
}