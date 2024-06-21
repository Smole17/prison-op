package ru.starfarm.mode.service.player.`object`.crate

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.service.player.`object`.PlayerScope
import java.util.concurrent.CompletableFuture

class CrateItem(
    val iconStack: ItemStack,
    val rarity: Rarities,
    private val onReceive: (PlayerScope) -> String?,
) {

    val icon
        get() = ItemUtil.of(iconStack).apply {
            name = rarity.color(name!!)

            addItemFlags(
                ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES
            )
        }.build().clone()

    fun receive(playerScope: PlayerScope) {
        Plugin.taskContext.asyncAfter(0) {
            onReceive.invoke(playerScope).run {
                if (playerScope.isMessagesFromCrates && this != null)
                    ChatUtil.sendMessage(playerScope.player, this)
            }
        }
    }
}