package ru.starfarm.mode.service.item.`object`.impl.material

import net.minecraft.server.v1_16_R3.NBTTagString
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.player.playerScope
import ru.starfarm.mode.upperEveryFirstLetter

object MaterialItem : ItemScope(
    "material_${Material.DIORITE.name.lowercase()}",
    ItemUtil.of(Material.DIORITE)
        .name(Material.DIORITE.name)
        .lore(
            "§7Используется как тип блока для шахты.",
            "",
            "§c§lЕсли у вас уже есть этот",
            "§c§lматериал, то он пропадёт",
            "",
            "§7Нажмите, чтобы использовать."
        )
        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        .build()
) {

    init {
        clickHandler = clickHandler@{ event, _ ->
            event.isCancelled = true

            val itemStack = event.item ?: return@clickHandler
            val material = Material.getMaterial((ItemUtil.getTag<NBTTagString>(itemStack, id) ?: return@clickHandler).asString())

            itemStack.amount -= 1

            ChatUtil.sendMessage(
                event.player.apply {
                    if (material != null) playerScope.mineScope.addMaterial(material)
                },
                "${POSITIVE}Вы активировал материал для вашей шахты."
            )
        }
    }

    fun addItem(player: Player, material: Material) {
        if (!material.isBlock) return

        addItem(player, getFormattedStack(material))
    }

        private fun getFormattedStack(material: Material): ItemStack = ItemUtil.of(itemStack.clone())
        .name(material.name.replace("_", " ").lowercase().upperEveryFirstLetter())
        .lore(
            "§7Используется как тип блока для шахты.",
            "",
            "§c§lЕсли у вас уже есть этот",
            "§c§lматериал, то он пропадёт.",
            "",
            "§7Нажмите, чтобы использовать."
        )
        .type(material)
        .tag(id, NBTTagString.a(material.name))
        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        .build()
}