package ru.starfarm.mode.service.player.`object`

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import ru.starfarm.core.profile.IProfileService
import ru.starfarm.core.profile.Profile
import ru.starfarm.core.profile.group.DonateGroup
import ru.starfarm.core.util.CurrentMillis
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.References
import ru.starfarm.mode.service.item.`object`.impl.pickaxe.PickaxeItem
import ru.starfarm.mode.service.item.`object`.impl.pickaxe.PickaxeUpgrades
import ru.starfarm.mode.service.mine.`object`.MineScope
import ru.starfarm.mode.service.player.`object`.booster.`object`.BoosterCause
import ru.starfarm.mode.service.player.`object`.booster.`object`.BoosterScope
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.`object`.group.GroupScope
import ru.starfarm.mode.service.player.`object`.group.Groups
import ru.starfarm.mode.service.prestige.PrestigeService
import ru.starfarm.mode.service.skill.SkillLevel
import ru.starfarm.mode.service.skill.SkillScope
import ru.starfarm.mode.service.skill.Skills
import java.time.Duration
import java.util.*

data class PlayerScope(private val uuid: UUID) {

    constructor(uuid: UUID, name: String) : this(uuid) {
        this.name = name
    }

    var name: String? = null
    lateinit var pickaxe: PickaxeItem
    var group = GroupScope(Groups.MANTLE, 0)
    var isAutoPrestige = false
    var isMessagesFromCrates = true
    @Transient
    var openingMultiRewardsCrates = 0

    val mineScope = MineScope()

    val profile: Profile? get() = IProfileService.Service.getProfile(player)
    val player get() = Bukkit.getPlayer(name!!)!!
    val isPickaxeInitialized get() = this::pickaxe.isInitialized
    val isGroupAvailable get() = group.expireIn > CurrentMillis || group.expireIn == 0L
    val isFlightAvailable get() = flightExpireIn > CurrentMillis
    val isMaxedPrestige get() = getAmount(Stats.PRESTIGE) >= PrestigeService.MAX_PRESTIGE

    private val stats = EnumMap<Stats, Double>(Stats::class.java)
    private val crates = EnumMap<Crates, Int>(Crates::class.java)
    private val boosters = EnumMap<Stats, ArrayList<BoosterScope>>(Stats::class.java)
    private val skills = EnumMap<Skills, SkillScope>(Skills::class.java)

    private var flightExpireIn = 0L

    fun setGroup(group: Groups = Groups.MANTLE, duration: Duration = Duration.ZERO) {
        this.group = GroupScope(
            group,
            if (duration.isZero) 0L else CurrentMillis + duration.toMillis()
        )

        addBooster(
            Stats.MONEY, group.multiplier, BoosterCause.GROUP,
            Duration.ZERO
        )
    }

    fun hasPickaxeUpgrade(upgrade: PickaxeUpgrades): Boolean = getPickaxeUpgradeLevel(upgrade) != 0

    fun addPickaxeUpgradeLevel(upgrade: PickaxeUpgrades, amount: Int = 1): Boolean {
        getPickaxeUpgradeLevel(upgrade).apply {
            if (this >= upgrade.maxLevel) return false

            pickaxe.upgrades[upgrade] = this + amount
            return true
        }
    }

    fun getPickaxeUpgradeLevel(upgrade: PickaxeUpgrades): Int = pickaxe.upgrades[upgrade] ?: 0

    fun getAmount(stats: Stats): Double {
        return this.stats.getOrDefault(stats, 0.0)
    }

    fun setAmount(stats: Stats, amount: Double) {
        this.stats.merge(stats, amount) { _, currentAmount -> currentAmount }
    }

    fun addAmount(stats: Stats, amount: Double = 1.0, boostered: Boolean = false) {
        if (amount <= 0) return

        this.stats.merge(
            stats,
            if (boostered) (boosters.entries
                .find { it.key == stats }
                ?.value
                .run {
                    if (isNullOrEmpty()) return@run amount

                    return@run sumOf { it.multiplier + 1 }.times(amount)
                }) else amount
        ) { previousAmount, currentAmount -> previousAmount + currentAmount }
    }

    fun subtractAmount(stats: Stats, amount: Double) {
        if (!this.stats.containsKey(stats)) return

        this.stats.merge(
            stats,
            amount
        ) { previousAmount, currentAmount -> if (previousAmount - currentAmount < 0.0) 0.0 else previousAmount - currentAmount }
    }

    fun getCrateAmount(crates: Crates): Int = this.crates.getOrDefault(crates, 0)

    fun hasCrate(crates: Crates): Boolean = getCrateAmount(crates) != 0

    fun addCrate(crates: Crates, amount: Int = 1) {
        if (amount > 0) ChatUtil.sendMessage(
            player,
            "${crates.chatColor}§l + ${crates.icon.itemMeta?.displayName} ключ $NEUTRAL§o(x${amount})"
        )
        this.crates.merge(crates, amount) { previousAmount, currentAmount -> previousAmount + currentAmount }
    }

    fun subtractCrate(crates: Crates, amount: Int) {
        this.crates.merge(
            crates,
            amount
        ) { previousAmount, currentAmount -> if (previousAmount - currentAmount <= 0) 0 else previousAmount - currentAmount }
    }

    fun getBoosters(stats: Stats): List<BoosterScope> = boosters.getOrDefault(stats, arrayListOf())

    fun addBooster(
        stats: Stats = Stats.MONEY,
        multiplier: Double = Groups.MANTLE.multiplier,
        boosterCause: BoosterCause = BoosterCause.GROUP,
        duration: Duration = Duration.ZERO,
    ) = boosters[stats].apply {
        BoosterScope(stats, boosterCause, multiplier, duration.toMillis()).also { boosterScope ->
            if (this == null) {
                boosters[stats] = arrayListOf(boosterScope)
                return@apply
            }

            if (any { it.boosterCause == boosterScope.boosterCause }) {
                removeBooster(stats, boosterCause)
                add(boosterScope)
            } else add(boosterScope)
        }
    }

    private fun removeBooster(stats: Stats, boosterCause: BoosterCause) = boosters[stats]?.apply {
        removeIf { it.boosterCause == boosterCause }
    }

    fun checkBoosters() {
        val donateGroup = IProfileService.Service.getProfile(player)?.donateGroup ?: DonateGroup.PLAYER

        fun updateDonateBooster() {
            val multiplier = when (donateGroup) {
                DonateGroup.VIP -> 0.05
                DonateGroup.VIP_PLUS -> 0.1
                DonateGroup.PREMIUM -> 0.15
                DonateGroup.PREMIUM_PLUS -> 0.2
                DonateGroup.ELITE -> 0.25
                DonateGroup.ELITE_PLUS -> 0.35
                DonateGroup.SPONSOR -> 0.5
                DonateGroup.SPONSOR_PLUS -> 0.75
                DonateGroup.UNIQUE -> 1.0
                else -> 0.0
            }

            if (boosters.entries.any {
                    (it.value.any { boosterScope -> it.key == Stats.MONEY && boosterScope.boosterCause == BoosterCause.DONATE_GROUP && boosterScope.multiplier == multiplier })
                }) return

            addBooster(
                multiplier = multiplier,
                boosterCause = BoosterCause.DONATE_GROUP
            )
        }

        updateDonateBooster()

        boosters.values
            .flatten()
            .filterNot(BoosterScope::isBoosterAvailable)
            .forEach { removeBooster(it.stats, it.boosterCause) }
    }

    fun addFlight(duration: Duration) {
        if (flightExpireIn <= 0) flightExpireIn = CurrentMillis

        flightExpireIn += duration.toMillis()
    }

    fun resetFlight(): Boolean {
        if (flightExpireIn == 0L) return false

        flightExpireIn = 0
        return true
    }

    fun getFlight(): Duration = Duration.ofMillis(flightExpireIn - CurrentMillis)

    fun toggleAutoPrestige(): Boolean {
        isAutoPrestige = !isAutoPrestige
        return isAutoPrestige
    }

    fun getCurrentSkillLevel(skill: Skills): SkillLevel = getCurrentSkill(skill).currentLevel

    fun getCurrentSkill(skill: Skills): SkillScope = skills.getOrDefault(skill, skill.skillScope)

    fun increaseSkill(skill: Skills) = skills.merge(skill, skill.skillScope.copy()) { oldValue, _ ->
        oldValue.apply {
            if (isMaxLevel) return@apply

            val nextExp = levels[level + 1].exp.toDouble()

            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                *TextComponent.fromLegacyText(
                    "§e+1 ${skill.skillScope.itemStack.itemMeta?.displayName} §7(${References.format(exp + 1.0)}/${
                        References.formatWithLetters(nextExp, "###.##")
                    })"
                )
            )

            increaseExp(skill, this@PlayerScope)
        }
    }

    fun teleportToMine() = mineScope.teleport(player)
}