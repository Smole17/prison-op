package ru.starfarm.mode

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.minecraft.server.v1_16_R3.EntityPlayer
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.entity.FakeEntity
import ru.starfarm.core.hologram.Hologram
import ru.starfarm.core.hologram.HologramLine
import ru.starfarm.core.inventory.container.Container
import ru.starfarm.core.inventory.item.ClickItem
import ru.starfarm.core.protocol.entity.PacketEntityMetadataWrapper
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.starfarm.mode.service.item.`object`.impl.pickaxe.PickaxeItem
import java.math.RoundingMode
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor

const val HOST = "85.10.200.40"
const val DATABASE = "PrisonOp"
const val USER = "root"
const val PASSWORD = "MySqlRootDev_SDFjajskdfamAMNdsmnqbnABNbnqANFnfnanfQNaMAnqqqFS1aM1123#"
const val TABLE = "prison_op_players"

val POSITIVE = ChatColor.GREEN
val NEGATIVE = ChatColor.RED
val NEUTRAL = ChatColor.DARK_GRAY

fun biggestLine(color: String = ChatColor.AQUA.toString()) = "$color§l${buildString { repeat(32) { append("-") } }}"

fun Hologram.updateLine(index: Int, value: String, vararg players: Player = this.players.toTypedArray()) {
    WrappedDataWatcher().apply {
        setObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                2, FakeEntity.OPTIONAL_CHAT_COMPONENT_SERIALIZER
            ), Optional.of(WrappedChatComponent.fromText(value).handle)
        )

        PacketEntityMetadataWrapper().apply {
            entityId = getLine<HologramLine>(index)?.entity?.id
            items = watchableObjects
        }.apply { players.forEach { send(it) } }
    }
}

fun Player.hidePlayerWithoutTab(target: Player): EntityPlayer =
    (this as CraftPlayer).handle.apply {
        hidePlayer(Plugin, target)

        playerConnection.sendPacket(
            PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                (target as CraftPlayer).handle
            )
        )
    }

fun String.upperEveryFirstLetter() = buildList {
    this@upperEveryFirstLetter.split("\\s".toRegex()).forEach { s ->
        add(s.replaceFirstChar { if (it.isLetter()) it.uppercaseChar() else it })
    }
}.joinToString(" ") { it }

fun String.hexed(): String = run {
    var message = this
    val pattern = Pattern.compile("&#[a-fA-F0-9]{6}")
    var matcher = pattern.matcher(message)

    while (matcher.find()) {
        val color = message.substring(matcher.start(), matcher.end())
        message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color.replace("&", "")).toString() + "")
        matcher = pattern.matcher(message)
    }

    net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message)
}

val PickaxeItem.expPercent
    get() =
        ((exp / neededExp) * 100.0).run { if (this >= 100.0) 100 else toInt() }.run {
            "$NEUTRAL[".plus(buildString {
                floor(this@run / 4.0).run floored@{ for (i in 0..25) append("${if (this@floored != .0 && i <= this@floored) POSITIVE else NEGATIVE}‖") }
            }).plus("$NEUTRAL] §7($POSITIVE$this%§7)")
        }

private const val MINI_LETTERS = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ"
private const val MINI_DIGITS = "₀₁₂₃₄₅₆₇₈₉⁰¹²³⁴⁵⁶⁷⁸⁹"

fun String.mini(upperDigits: Boolean = false): String = String(toCharArray().apply {
    forEachIndexed { index, char ->
        if (char.isLetter())
            set(index, MINI_LETTERS[char.code - if (char.isUpperCase()) 65 else 97])
        else if (char.isDigit()) set(index, MINI_DIGITS[(char.code - 48) + if (upperDigits) 10 else 0])
    }
})

object References {

    @JvmStatic
    fun getReturnIcon(slot: Int, previousContainer: Container): ClickItem = ClickItem(
        slot,
        ItemUtil.of(Material.ARROW).apply {
            name = "${NEGATIVE}Назад"
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
    ) { _, event ->
        (event.whoClicked as Player).run {
            previousContainer.openInventory(this)
            playSound(location, Sound.BLOCK_CHEST_CLOSE, 1f, 1f)
        }
    }

    @JvmStatic
    fun format(double: Double, format: String = "###,###"): String {
        return NumberUtil.newDoubleFormatterBuilder(format)
            .devider(1.0, "")
            .build()
            .format(double)
    }

    @JvmStatic
    fun formatWithLetters(double: Double, format: String = "###"): String {
        return NumberUtil.newDoubleFormatterBuilder(format).apply {
            this.format.roundingMode = RoundingMode.DOWN

            devider(1.0, "")
            devider(1e3, "K")
            devider(1e6, "M")
            devider(1e9, "B")
            devider(1e12, "T")
            devider(1e15, "q")
            devider(1e18, "Q")
            devider(1e21, "s")
            devider(1e24, "S")
        }.build().format(double)
    }
}
