package ru.starfarm.mode.service.item.`object`

import net.minecraft.server.v1_16_R3.NBTTagString
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.service.player.`object`.PlayerScope

open class ItemScope(@Transient val id: String, @Transient var itemStack: ItemStack, @Transient var clickHandler: (PlayerInteractEvent, PlayerScope) -> Unit = { _, _ -> }) {

    companion object {
        const val TAG_KEY = "clickable"
    }

    init {
        itemStack = ItemUtil.setTags(itemStack, TAG_KEY to NBTTagString.a(id))
    }

    fun addItem(player: Player, itemStack: ItemStack = this.itemStack.clone(), checkSimilar: Boolean = false, filterPredicate: (ItemStack?) -> Boolean = { it != null && it.isSimilar(itemStack) }) {
        val playerInventory = player.inventory
        if (checkSimilar)
            playerInventory.contents
                .filter(filterPredicate)
                .forEach { it.amount = 0 }

        playerInventory.addItem(itemStack)
    }
}