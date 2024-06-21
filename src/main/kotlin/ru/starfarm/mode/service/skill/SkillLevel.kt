package ru.starfarm.mode.service.skill

import org.bukkit.Material
import ru.starfarm.core.inventory.item.BaseItem
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.service.player.`object`.PlayerScope

class SkillLevel(val exp: Int, vararg val lore: String, val reward: PlayerScope.() -> Unit?) {
    fun getBaseItem(skills: Skills, slot: Int, level: Int = slot + 1, playerScope: PlayerScope): BaseItem = BaseItem(
        slot,
        ItemUtil.of(Material.STONE)
            .apply {
                name = "${POSITIVE}Уровень $level"
                if (exp != 0) lore("${NEUTRAL}$exp опыта.")
                addLore(*this@SkillLevel.lore)

                type = playerScope.getCurrentSkillLevel(skills).run {
                    if (this == this@SkillLevel) {
                        name = name.plus(" ${NEUTRAL}(Вы здесь)")
                        Material.LIME_DYE
                    } else Material.GRAY_DYE
                }
            }.build(),
    )
}