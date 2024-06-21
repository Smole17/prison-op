package ru.starfarm.mode.service.player.`object`.crate

import com.comphenix.protocol.wrappers.EnumWrappers
import org.bukkit.Bukkit
import ru.starfarm.core.entity.impl.FakeArmorStand
import ru.starfarm.core.entity.impl.FakeDropItem
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.service.player.`object`.PlayerScope
import java.util.function.Predicate
import org.bukkit.Material
import org.bukkit.Particle
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.POSITIVE
import ru.starfarm.mode.biggestLine
import java.util.LinkedList
import java.util.Queue
import java.util.UUID

val OpeningCrates = hashMapOf<UUID, Queue<CrateItem>>()

enum class CratesType(val openCrate: (PlayerScope, CrateScope) -> Unit) {

    DEFAULT({ playerScope, crateScope ->
        crateScope.getRandomItem().apply {
            receive(playerScope)

            if (rarity.ordinal >= 4) {
                val itemMeta = icon.itemMeta

                Bukkit.broadcastMessage("§6[${crateScope.crateName}] ${playerScope.player.name} получил ${itemMeta?.displayName}§6.")
            }
        }
    }),
    MULTI_REWARD({ playerScope, crateScope ->
        val taskContext = ru.starfarm.mode.Plugin.taskContext

        val player = playerScope.player
        val uuid = player.uniqueId
        val playerLocation = player.location.clone()

        taskContext.after(0) {
            player.closeInventory()
        }

        val items =
            OpeningCrates.merge(uuid, LinkedList<CrateItem>().apply {
                repeat(8) { push(crateScope.getRandomItem()) }
            }) { previous, current -> previous.apply { addAll(current.toList()) } }

        FakeArmorStand(playerLocation).apply stand@{
            playerScope.openingMultiRewardsCrates += 1

            invisible = true
            access = Predicate.isEqual(player)

            fun setHead() {
                itemSlots[EnumWrappers.ItemSlot.HEAD] = ItemUtil.of(Material.ENDER_CHEST).build()
            }

            setHead()

            var rotate = 1f
            var r = .0

            val clonedLocation = location.clone()

            val teleportTask = taskContext.everyAsync(0, 1) {
                if (!player.isOnline) {
                    it.cancel()
                    return@everyAsync
                }

                setHeadRotation(0f, rotate, 0f)
                rotate += 16.4f
                r += kotlin.math.PI / 46

                player.spawnParticle(
                    Particle.SPELL_WITCH,
                    clonedLocation.clone().add(1.5 * kotlin.math.cos(r), r * .5, 1.5 * kotlin.math.sin(r)),
                    1, .0, .0,
                    .0, .1
                )
                player.spawnParticle(
                    Particle.SPELL_WITCH,
                    clonedLocation.clone().subtract(1.5 * kotlin.math.cos(r), r * -.5, 1.5 * kotlin.math.sin(r)),
                    1, .0, .0,
                    .0, .05
                )

                teleport(location.add(.0, .025, .0))
            }

            fun dropRandomStack() {
                if (!player.isOnline)
                    return

                FakeDropItem(location.clone().add(.0, 2.0, .0)).apply {
                    small = false
                    access = Predicate.isEqual(player)
                    noGravity = true

                    items?.poll().let { it ->
                        if (!player.isOnline || it == null)
                            return@apply

                        val crateStack = it.iconStack
                        val displayName = crateStack.itemMeta?.displayName

                        item = crateStack

                        customNameVisible = true
                        customName =
                            it.rarity.let {
                                val rarityOrdinal = it.ordinal
                                if (rarityOrdinal >= 3) {
                                    Bukkit.broadcastMessage(
                                        """
                                        ${biggestLine("§e§k")}
                                            
                                            $POSITIVE§l${playerScope.player.name} ${POSITIVE}получил 
                                            $NEUTRAL[$it${NEUTRAL}] ${it.chatColor}$displayName
                                            ${POSITIVE}с §e§l${crateScope.crateName} ${POSITIVE}ящика. $NEUTRAL(/donate)
                                            
                                        ${biggestLine("§e§k")}
                                        """.trimIndent()
                                    )

                                    taskContext.after(0) {
                                        location.world?.strikeLightningEffect(
                                            location
                                        )
                                    }
                                }

                                "$NEUTRAL[${it}${NEUTRAL}] ${it.chatColor}$displayName"
                            }

                        it
                    }.receive(playerScope)

                    player.spawnParticle(Particle.CLOUD, location, 15, .1, .1, .1, .3)

                    taskContext.asyncAfter(40) {
                        remove()

                        if (items.isNullOrEmpty()) {
                            this@stand.remove()
                            OpeningCrates.remove(uuid)
                            playerScope.openingMultiRewardsCrates -= 1
                            player.spawnParticle(org.bukkit.Particle.EXPLOSION_LARGE, location.clone().subtract(.0, .3, .0), 3)
                            return@asyncAfter
                        }

                        dropRandomStack()
                        setHead()
                    }
                }
            }

            taskContext.asyncAfter(100) {
                if (!player.isOnline) {
                    it.cancel()
                    return@asyncAfter
                }

                teleportTask.cancel()
                taskContext.asyncAfter(20) { dropRandomStack() }
            }
        }
    }),
    ;
}