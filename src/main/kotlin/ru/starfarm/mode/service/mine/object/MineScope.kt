package ru.starfarm.mode.service.mine.`object`

import com.comphenix.protocol.wrappers.WrappedBlockData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.protocol.block.v1_16_R3PacketMultiBlockChange
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.core.util.math.Cuboid
import ru.starfarm.mode.NEGATIVE
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.hidePlayerWithoutTab
import java.util.UUID
import kotlin.random.Random

private const val MAXIMUM_BLOCKS = 27
private const val MAXIMUM_CURRENT_BLOCKS = 2

private var brokeBlocks = mutableMapOf<UUID, Int>()

class MineScope {

    @delegate:Transient
    val mineCuboid by lazy {
        Cuboid.atCoordinates(
            Bukkit.getWorld("mine")!!,
            22.0 + level, 69.0, 20.0 + level,
            -23.0 - level, 3.0, -25.0 - level
        )
    }

    @delegate:Transient
    private val mineCuboidCount by lazy { mineCuboid.count() }

    @Transient
    var isFilling = false

    private val materials = mutableSetOf(Material.COBBLESTONE)
    private val currentMaterials = mutableSetOf(*materials.toTypedArray())

    private val level = 0

    fun addMaterial(material: Material) = materials.run { if (size < MAXIMUM_BLOCKS) add(material) }

    fun hasCurrentMaterial(material: Material) = currentMaterials.contains(material)

    fun openInventory(player: Player) {
        ApiManager.createInventoryContainer("${NEUTRAL}Блоки", (materials.size / 9) + 1) { _, container ->
            materials.forEachIndexed { i, material ->
                if (i >= container.inventory.size - 1) return@createInventoryContainer

                val containsMaterial = currentMaterials.contains(material)

                container.addItem(
                    i,
                    ItemUtil.of(material)
                        .apply {
                            addItemFlags(ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)

                            lore(
                                "",
                                "§eНажмите, чтобы ${
                                    if (containsMaterial) {
                                        enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                                        "убрать"
                                    } else "выбрать"
                                } блок."
                            )
                        }.build()
                ) click@{ _, _ ->
                    if (containsMaterial) {
                        if (currentMaterials.size <= 1) {
                            ChatUtil.sendMessage(player, "${NEGATIVE}Вы не можете убрать все блоки из шахты.")
                            player.closeInventory()
                            return@click
                        }

                        currentMaterials.remove(material)
                        openInventory(player)
                        return@click
                    }

                    if (currentMaterials.size >= MAXIMUM_CURRENT_BLOCKS) return@click

                    currentMaterials.add(material)
                    openInventory(player)
                }
            }
        }.openInventory(player)
    }

    fun decrementBlock(player: Player) {
        val uuid = player.uniqueId

        brokeBlocks.merge(uuid, 1) { oldValue, newValue -> oldValue - newValue }
        if ((brokeBlocks[uuid]?.div(mineCuboidCount.toDouble()) ?: .0) > 0.3) return

        teleport(player)
    }

    fun teleport(player: Player) =
        mineCuboid.center.let { center ->
            Plugin.taskContext.after(0) { task ->
                if (isFilling) return@after

                val world = center.world

                world?.players?.forEach {
                    player.hidePlayerWithoutTab(it)
                    it.hidePlayerWithoutTab(player)
                }

                isFilling = true

                player.teleport(Location(world, 1.0, 70.0, 23.0, -145f, 0f))

                task.context.asyncAfter(0) { fill(player) }
            }
        }

    private fun fill(player: Player) {
        var totalBlocks = 0

        mineCuboid.blockByChunks.entries.associate { entry ->
            entry.key to entry.value.map {
                it.location.toVector() to WrappedBlockData.createData(
                    currentMaterials.toList().run { if (size == 1 || Random.nextBoolean()) get(0) else get(1) }
                )
            }.toTypedArray()
        }.forEach { (chunk, blocks) ->
            v1_16_R3PacketMultiBlockChange().apply {
                sectionPosition = chunk
                blocksData = blocks.apply { totalBlocks += size }
            }.send(player)
        }

        brokeBlocks[player.uniqueId] = totalBlocks
        isFilling = false
    }
}