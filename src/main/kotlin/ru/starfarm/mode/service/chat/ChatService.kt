package ru.starfarm.mode.service.chat

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ItemTag
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Item
import net.minecraft.server.v1_16_R3.ItemStack
import net.minecraft.server.v1_16_R3.NBTTagCompound
import org.bukkit.entity.Player
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.parser.ChatComponentParser
import ru.starfarm.core.util.format.parser.ChatReplacement
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.mode.References
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.playerScope
import ru.starfarm.mode.upperEveryFirstLetter

object ChatService : BukkitService {

    init {
        val itemParser = ChatComponentParser<Player>(
            ChatReplacement.replacement("item", mutableListOf("#hand", "#рука", "[item]", "#item")) handle@{ player ->
                val itemInMainHand = player.inventory.itemInMainHand

                if (itemInMainHand.type.isAir) return@handle null
                val itemMeta = itemInMainHand.itemMeta

                val nmsStack = (ItemUtil.toNMS(itemInMainHand) as ItemStack).save(NBTTagCompound()).toString()

                val amount = itemInMainHand.amount
                itemMeta?.displayName.run {
                    if (isNullOrEmpty() || isBlank())
                        ApiManager.buildMessage {
                            text = itemInMainHand.type.name
                                .replace("_", " ").lowercase()
                                .upperEveryFirstLetter()
                            hoverItem(nmsStack)
                        }.build()
                    else ApiManager.newMessageBuilder().hoverItem(nmsStack).build()
                        .apply { TextComponent.fromLegacyText(this@run).onEach { addExtra(it) } }
                }.apply {
                    addExtra(
                        ApiManager.buildMessage {
                            text = if (amount <= 1) "" else " §ex$amount"
                        }.build()
                    )
                }
            }
        )

        ApiManager.newDefaultChatBuilder()
            .withMessageMapper { ctx ->
                val target = ctx.player
                val message = ctx.message

                arrayListOf<BaseComponent>().apply {
                    target.playerScope.run {
                        val profile = ctx.profile

                        addAll(
                            arrayOf(
                                ApiManager.newMessageBuilder(
                                    "§b${profile.guildTag.run { if (isNullOrEmpty()) "" else "$this " }}§r§8[${
                                        References.format(
                                            getAmount(Stats.PRESTIGE)
                                        )
                                    }] "
                                ).build(),
                                *TextComponent.fromLegacyText(group.toString()),
                                ApiManager.newMessageBuilder("${group.type.chatColor} ${profile.name}§r${profile.title.run { if (this == null) "" else " §7[$this]" }}")
                                    .hoverText("§fГруппа: ${profile.primaryGroup.displayName.run { ifEmpty { "§7Игрок" } }}")
                                    .build(),
                                ApiManager.newMessageBuilder("§f: ")
                                    .build(),
                                *itemParser.parse(message, target).toTypedArray()
                            )
                        )
                    }
                }.toTypedArray()
            }
            .withDefaultProcessors()
            .register()
    }
}