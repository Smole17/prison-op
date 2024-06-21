package ru.starfarm.mode.service.item

import net.minecraft.server.v1_16_R3.NBTTagString
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.ApiManager
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.require.Require
import ru.starfarm.core.command.type.TypeDouble
import ru.starfarm.core.profile.group.StaffGroup
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.service.item.`object`.ItemScope
import ru.starfarm.mode.service.item.`object`.Items
import ru.starfarm.mode.service.item.`object`.impl.booster.BoosterItem
import ru.starfarm.mode.service.item.`object`.impl.crate.CrateItem
import ru.starfarm.mode.service.item.`object`.impl.flight.FlightItem
import ru.starfarm.mode.service.item.`object`.impl.group.GroupItem
import ru.starfarm.mode.service.item.`object`.impl.material.MaterialItem
import ru.starfarm.mode.service.item.`object`.impl.pickaxe.PickaxeItem
import ru.starfarm.mode.service.item.`object`.impl.withdraw.WithdrawItem
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.`object`.crate.OpeningCrates
import ru.starfarm.mode.service.player.`object`.event.PlayerInitializedEvent
import ru.starfarm.mode.service.player.`object`.group.Groups
import ru.starfarm.mode.service.player.playerScope
import ru.starfarm.mode.updateLine
import java.time.Duration

object ItemService : BukkitService {

    init {
        Plugin.on<PlayerInteractEvent> {
            if (item == null || item?.type == Material.AIR || item?.hasItemMeta() == false) return@on

            val itemId = ItemUtil.getTag<NBTTagString>(item!!, ItemScope.TAG_KEY)

            Items.values()
                .map { it.itemScope }
                .filter { it.id == itemId?.asString() }
                .forEach { it.clickHandler.invoke(this, player.playerScope) }
        }

        Plugin.on<PlayerInitializedEvent> {
            if (playerScope.isFlightAvailable) player.allowFlight = true
        }
        Plugin.on<PlayerInitializedEvent> {
            if (!playerScope.isPickaxeInitialized) playerScope.pickaxe = PickaxeItem()

            playerScope.pickaxe.addItem(playerScope, player)
            playerScope.pickaxe.upgrades.keys.forEach { it.initialize(playerScope) }
        }

        Plugin.on<PlayerDropItemEvent> {
            val itemStack = itemDrop.itemStack

            if (!itemStack.hasItemMeta() || !itemStack.type.name.contains("PICKAXE")) return@on

            isCancelled = true
        }
        Plugin.on<PlayerSwapHandItemsEvent> {
            if (mainHandItem?.hasItemMeta() == false || mainHandItem?.type?.name?.contains("PICKAXE") == false &&
                offHandItem?.hasItemMeta() == false || offHandItem?.type?.name?.contains("PICKAXE") == false
            ) return@on

            isCancelled = true
        }
        Plugin.on<InventoryClickEvent> {
            val player = whoClicked as Player

            val itemStack = when (true) {
                (action == InventoryAction.PLACE_ALL) -> view.cursor
                (hotbarButton != -1) -> player.inventory.contents[hotbarButton]
                else -> currentItem
            }

            if (itemStack == null || !itemStack.type.name.contains("PICKAXE")) return@on

            if (inventory.type == InventoryType.CRAFTING) {
                if (slot == 40) {
                    player.closeInventory()
                    isCancelled = true
                }

                return@on
            }

            if (action != InventoryAction.MOVE_TO_OTHER_INVENTORY && action != InventoryAction.HOTBAR_SWAP && action != InventoryAction.PICKUP_ALL) return@on

            isCancelled = true
        }

        Plugin.registerCommands(
            object : Command<Player>("tokenswithdraw", "Превратить токены в предмет.", "tewithdraw", "tew") {
                init {
                    addParameter("количество", TypeDouble(0.0, Double.MAX_VALUE))
                }

                override fun execute(ctx: CommandContext<Player>) {
                    val amount = ctx.getArg<Double>(0) ?: return

                    val playerScope = ctx.sender.playerScope
                    if (amount > playerScope.getAmount(Stats.TOKENS)) return

                    playerScope.subtractAmount(Stats.TOKENS, amount)
                    (Items.TOKENS_WITHDRAW.itemScope as WithdrawItem).addItem(ctx.sender, amount)
                }
            },
            object : Command<Player>("emeraldwithdraw", "Превратить изумруды в предмет.", "emwithdraw", "emw") {
                init {
                    addParameter("количество", TypeDouble(0.0, Double.MAX_VALUE))
                }

                override fun execute(ctx: CommandContext<Player>) {
                    val amount = ctx.getArg<Double>(0) ?: return

                    val playerScope = ctx.sender.playerScope
                    if (amount > playerScope.getAmount(Stats.EMERALDS)) return

                    playerScope.subtractAmount(Stats.EMERALDS, amount)
                    (Items.EMERALDS_WITHDRAW.itemScope as WithdrawItem).addItem(ctx.sender, amount)
                }
            },
            object : Command<Player>("items", "shhhh...") {
                init {
                    addRequire(
                        Require.groups(
                            StaffGroup.GAME_DESIGNER, StaffGroup.DEVELOPER, StaffGroup.ADMINISTRATOR
                        )
                    )
                }

                override fun execute(ctx: CommandContext<Player>) {
                    ApiManager.createInventoryContainer("${NEUTRAL}Choose Items", 6) { player, container ->
                        Items.values().forEachIndexed { i, item ->
                            val itemScope = item.itemScope

                            container.addItem(i, itemScope.itemStack) { _, _ ->
                                when (itemScope) {
                                    is FlightItem -> itemScope.addItem(player, Duration.ofMinutes(1))
                                    is GroupItem -> itemScope.addItem(
                                        player, when (itemScope.group) {
                                            Groups.LS -> Duration.ZERO
                                            else -> Duration.ofDays(7)
                                        }
                                    )

                                    is PickaxeItem -> return@addItem
                                    is WithdrawItem -> itemScope.addItem(player, 1e21)
                                    is CrateItem -> itemScope.addItem(player)
                                    is BoosterItem -> itemScope.addItem(player, Duration.ofMinutes(30), 2.0)
                                    is MaterialItem -> itemScope.addItem(player, Material.DIORITE)
                                }
                            }
                        }
                    }.openInventory(ctx.sender)
                }
            }
        )
    }

    override fun load() {
        val hologramEntity = Crates.values().map {
            it to it.spawnCrate()
        }

        Plugin.taskContext.everyAsync(0, 20) {
            hologramEntity.forEach { (crate, hologram) ->
                hologram.players.forEach {
                    hologram.updateLine(
                        hologram.lines.size - 2,
                        "§fВы имеете ${crate.chatColor}§n${
                            it.playerScope.getCrateAmount(crate)
                        }§r §fключей от этого ящика.",
                        it
                    )
                }
            }
        }
    }
    override fun unload(player: Player) {
        val playerScope = player.playerScope

        OpeningCrates.remove(player.uniqueId)?.apply { logger.info("Execute Crate Saver with $size items.") }?.forEach {
            it.receive(playerScope)
        }
        playerScope.openingMultiRewardsCrates = 0
    }
}