package ru.starfarm.mode.service.player.`object`.crate

import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.service.player.`object`.PlayerScope
import kotlin.random.Random

data class CrateScope(val crateName: String, val crateType: CratesType, private val items: List<CrateItem>) {

    fun openCrate(playerScope: PlayerScope) {
        crateType.openCrate.invoke(playerScope, this)
    }

    fun openPreview(playerScope: PlayerScope) {
        if (isItemsEmpty()) return

        val itemsSize = items.size

        val player = playerScope.player
        val inventorySize = (itemsSize / 9) + 1

        ApiManager.createInventoryContainer(
            "${NEUTRAL}$crateName",
            if (inventorySize >= 6) 6 else inventorySize
        ) { _, container ->
            items.forEachIndexed { i, crateItem ->
                container.addItem(
                    i, ItemUtil.of(crateItem.icon)
                        .addLore(
                            "§7Редкость: ${crateItem.rarity}"
                        )
                        .build()
                )
            }
        }.openInventory(player)
    }

    fun isItemsEmpty() = items.isEmpty()

    fun getRandomItem(): CrateItem {
        val commonItems =
            items.filter { it.rarity === Rarities.COMMON || it.rarity === Rarities.UNCOMMON || it.rarity === Rarities.RARE }
        var crateItem = commonItems.random()

        if (items.size > 1) {
            items
                .sortedBy { -it.rarity.ordinal }
                .forEach {
                    if (Random.nextDouble() <= it.rarity.chance) {
                        crateItem = it
                        return@forEach
                    }
                }
        }

        return crateItem
    }
}