package ru.starfarm.mode.service.item.`object`.impl.booster

import net.minecraft.server.v1_16_R3.NBTTagDouble
import net.minecraft.server.v1_16_R3.NBTTagLong
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.booster.`object`.BoosterCause
import java.time.Duration

class BoosterItem(stats: Stats) : ItemScope("${stats.name}_booster",
    ItemUtil.of(stats.icon)
        .name("${stats.colored("%s Бустер", true)} ${NEUTRAL}(%s мин.)")
        .addLore("§7Нажмите, чтобы использовать.")
        .addItemFlags(
            ItemFlag.HIDE_ATTRIBUTES
        )
        .build()
) {

    init {
        clickHandler = clickHandler@{ event, playerScope ->
            val item = event.item ?: return@clickHandler

            val amount = ItemUtil.getTag<NBTTagLong>(item, id) ?: return@clickHandler
            val multiplierTag = ItemUtil.getTag<NBTTagDouble>(item, id.plus("#0")) ?: return@clickHandler
            val duration = Duration.ofMillis(amount.asLong())
            val multiplier = multiplierTag.asDouble()

            item.amount -= 1

            playerScope.addBooster(
                stats, multiplier, BoosterCause.TEMPORARY_BOOSTER,
                duration
            )

            ChatUtil.sendMessage(
                event.player,
                "${POSITIVE}Вы использовали ${stats.colored("Бустер", true)} ${POSITIVE}на ${duration.toMinutes()} минут."
            )
        }
    }

    fun addItem(player: Player, duration: Duration, multiplier: Double) {
        if (duration.isZero || duration.isNegative) return

        addItem(player, getFormattedStack(duration, multiplier))
    }

    private fun getFormattedStack(duration: Duration, multiplier: Double): ItemStack =
        ItemUtil.of(itemStack.clone())
            .apply {
                name = name?.format("+".plus((multiplier * 100).toInt().toString().plus("%")), duration.toMinutes())
            }
            .tag(id, NBTTagLong.a(duration.toMillis()))
            .tag(id.plus("#0"), NBTTagDouble.a(multiplier))
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
            .addItemFlags(ItemFlag.HIDE_ENCHANTS)
            .build()
}