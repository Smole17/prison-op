package ru.starfarm.mode.service.item.`object`.impl.withdraw

import net.minecraft.server.v1_16_R3.NBTTagDouble
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.References
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.player.`object`.Stats

class WithdrawItem(private val stats: Stats) : ItemScope(
    "withdraw_${stats.name.lowercase()}",
    ItemUtil.of(stats.icon)
        .name(stats.colored("1"))
        .addLore("§7Нажмите, чтобы использовать.")
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        .build()
) {

    init {
        clickHandler = clickHandler@{ event, playerScope ->
            val item = event.item ?: return@clickHandler
            val amount = ItemUtil.getTag<NBTTagDouble>(item, stats.name.lowercase()) ?: return@clickHandler

            playerScope.addAmount(stats, amount.asDouble())
            item.amount -= 1
        }
    }

    fun addItem(player: Player, amount: Double) {
        if (amount <= 0) return

        addItem(
            player, ItemUtil.of(itemStack.clone())
                .name(stats.colored(References.format(amount)))
                .tag(stats.name.lowercase(), NBTTagDouble.a(amount))
                .build()
        )
    }
}