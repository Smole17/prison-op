package ru.starfarm.mode.service.item.`object`.impl.flight

import net.minecraft.server.v1_16_R3.NBTTagLong
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.References
import ru.starfarm.mode.service.item.`object`.ItemScope
import java.time.Duration

object FlightItem : ItemScope(
    "flight",
    ItemUtil.of(Material.FEATHER)
        .name("§fПолёт $NEUTRAL(%s мин.)")
        .addLore(
            "§7Нажмите, чтобы использовать.",
            "§7Используйте /fly для полёта."
        )
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        .build()
) {
    init {
        clickHandler = clickHandler@{ event, playerScope ->
            val item = event.item ?: return@clickHandler

            val amount = ItemUtil.getTag<NBTTagLong>(item, id) ?: return@clickHandler
            val millis = Duration.ofMillis(amount.asLong())

            item.amount -= 1

            playerScope.addFlight(millis)
            event.player.allowFlight = true

            ChatUtil.sendMessage(
                event.player,
                "${POSITIVE}Вы получили ${millis.toMinutes()} минут полёта."
            )
        }
    }

    fun addItem(player: Player, duration: Duration) {
        if (duration.isNegative || duration.isZero) return

        addItem(player, getFormattedStack(duration))
    }

    fun getFormattedStack(duration: Duration): ItemStack = ItemUtil.of(itemStack.clone())
        .apply {
            name = name?.format(duration.toMinutes())
        }
        .tag(id, NBTTagLong.a(duration.toMillis()))
        .build()
}