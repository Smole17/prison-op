package ru.starfarm.mode.service.item.`object`.impl.crate

import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.player.`object`.crate.Crates

class CrateItem(crates: Crates) : ItemScope(
    "crate_${crates.name.lowercase()}",
    ItemUtil.of(crates.icon).apply {
        name = crates.chatColor.toString().plus(name)
    }
        .addLore(
            "§7Нажмите, чтобы внести в виртуальное хранилище.",
            "§7Используются на спавне или в /crates."
        )
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        .build()
) {

    init {
        clickHandler = clickHandler@{ event, playerScope ->
            if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return@clickHandler

            val player = event.player

            val clonedItem = event.item?.clone()
            val amount = if (player.isSneaking) clonedItem?.amount ?: 0 else 1

            event.item?.amount = event.item?.amount?.minus(amount) ?: 0

            playerScope.addCrate(crates, amount)
        }
    }

    fun addItem(player: Player, amount: Int = 1) {
        itemStack.clone().apply {
            this.amount = amount

            addItem(player, this)
        }
    }
}