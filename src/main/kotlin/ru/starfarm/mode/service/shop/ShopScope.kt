package ru.starfarm.mode.service.shop

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.starfarm.core.inventory.item.ClickItem
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.References
import ru.starfarm.mode.service.player.PlayerService
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats

abstract class ShopScope(title: String, private val rows: Int, private val playerService: PlayerService) :
    InventoryContainer(title, rows) {

    abstract fun render()

    fun addItem(
        slot: Int,
        itemStack: ItemStack,
        stats: Stats,
        price: Double,
        message: Boolean = true,
        onSuccess: PlayerScope.() -> Unit,
    ) {
        addItem(
            ClickItem(
                slot,
                ItemUtil.of(itemStack.clone()).apply {
                    addItemFlags(
                        ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_ATTRIBUTES
                    )

                    addLore(
                        "",
                        "§7Цена",
                        stats.colored(References.format(price), true),
                        "",
                        "§eНажмите, чтобы приобрести!"
                    )
                }.build(),
            )
            { _, event ->
                val playerScope = playerService.getPlayerScope(event.whoClicked.uniqueId)

                if (playerScope.getAmount(stats) < price) return@ClickItem

                onSuccess.invoke(playerScope)
                playerScope.subtractAmount(stats, price)
                if (message)
                    ChatUtil.sendMessage(
                        event.whoClicked,
                        "${POSITIVE}Вы приобрели ${itemStack.itemMeta?.displayName}${if (itemStack.amount > 1) " §7x${itemStack.amount}" else ""}${POSITIVE}."
                    )
            })
    }

    override fun drawInventory(player: Player) {
        var side = false
        var i = -1
        while (i < 9 * rows - 1) {
            val tempSide = side
            if (i != 0 && i % 9 == 0 && i < 9 * (rows - 1)) side = !side
            if (tempSide == side) side = false
            if (side) i += 8 else i++

            addItem(
                i,
                ItemUtil.of(Material.GRAY_STAINED_GLASS_PANE)
                    .name("")
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build()
            )
        }

        render()
    }
}