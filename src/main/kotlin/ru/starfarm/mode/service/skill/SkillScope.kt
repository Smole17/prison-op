package ru.starfarm.mode.service.skill

import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.biggestLine
import ru.starfarm.mode.service.player.`object`.PlayerScope

data class SkillScope(
    @Transient val itemStack: ItemStack, var level: Int = 0, var exp: Int = 0,
    @Transient var levels: List<SkillLevel>,
) {

    val currentLevel get() = levels[level]
    val isMaxLevel get() = level >= levels.size - 1

    fun increaseExp(skills: Skills, playerScope: PlayerScope, exp: Int = ++this.exp) {
        if (isMaxLevel) return

        if (getExp(level + 1) <= exp) {
            addLevel(playerScope)

            val player = playerScope.player

            ChatUtil.sendMessage(
                player,
                """
                    ${biggestLine()}
                    §e§lПОВЫШЕНИЕ УРОВНЯ! §r${skills.skillScope.itemStack.itemMeta?.displayName} $POSITIVE§l${level - 1}→$level.
                    
                """.trimIndent()
            )

            currentLevel.lore.forEach { player.sendMessage("   ".plus(it)) }

            ChatUtil.sendMessage(
                player,
                """
                    
                    ${biggestLine()}
                """.trimIndent()
            )
        }
    }

    private fun getExp(level: Int): Int = levels[level].exp

    private fun addLevel(playerScope: PlayerScope) {
        exp = 0
        getReward(playerScope, ++level)
    }

    private fun getReward(playerScope: PlayerScope, level: Int) = levels[level].reward.invoke(playerScope)
}