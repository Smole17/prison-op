package ru.starfarm.mode.service.item.`object`.impl.group

import net.minecraft.server.v1_16_R3.NBTTagLong
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.CurrentMillis
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.player.`object`.group.Groups
import java.time.Duration

class GroupItem(val group: Groups) : ItemScope(
    "group_${group.name.lowercase()}",
    ItemUtil.of(Material.MAP)
        .name("$group ${ NEUTRAL}(%s дн.)")
        .addLore(
            "§7У этой группы +${(group.multiplier * 100).toInt()}% бустер денег.",
            "§7Нажмите, чтобы использовать."
        )
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        .build()
) {

    init {
        clickHandler = clickHandler@{ event, playerScope ->
            event.isCancelled = true

            val itemStack = event.item ?: return@clickHandler

            val amount = ItemUtil.getTag<NBTTagLong>(itemStack, id) ?: return@clickHandler
            val duration = Duration.ofMillis(amount.asLong()).multipliedBy(itemStack.amount.toLong())

            val groupScope = playerScope.group

            val groupType = groupScope.type
            val groupExpire = groupScope.expireIn

            if (
                (groupType.ordinal >= group.ordinal
                        && (groupType != group || (!duration.isZero && groupExpire >= CurrentMillis + duration.toMillis())))
                && groupType != Groups.LS
            ) return@clickHandler

            itemStack.amount = 0

            playerScope.setGroup(group, duration)

            ChatUtil.sendMessage(
                event.player,
                "${POSITIVE}Вы активировали группу $group ${POSITIVE}на ${if (duration.isZero) "бесконечность" else "${duration.toDays()} дней"}."
            )
        }
    }

    fun addItem(player: Player, duration: Duration) {
        if (duration.isNegative) return

        addItem(player, getFormattedStack(duration))
    }

    fun getFormattedStack(duration: Duration): ItemStack = ItemUtil.of(itemStack.clone())
        .apply {
            name = name?.format(if (duration.isZero) "БЕСКОНЕЧНОСТЬ" else duration.toDays())
        }
        .tag(id, NBTTagLong.a(duration.toMillis()))
        .build()
}