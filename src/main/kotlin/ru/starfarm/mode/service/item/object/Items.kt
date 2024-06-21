package ru.starfarm.mode.service.item.`object`

import ru.starfarm.mode.service.item.`object`.impl.booster.BoosterItem
import ru.starfarm.mode.service.item.`object`.impl.crate.CrateItem
import ru.starfarm.mode.service.item.`object`.impl.flight.FlightItem
import ru.starfarm.mode.service.item.`object`.impl.group.GroupItem
import ru.starfarm.mode.service.item.`object`.impl.material.MaterialItem
import ru.starfarm.mode.service.item.`object`.impl.pickaxe.PickaxeItem
import ru.starfarm.mode.service.item.`object`.impl.withdraw.WithdrawItem
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.`object`.group.Groups

enum class Items(val itemScope: ItemScope) {

    FLIGHT(FlightItem),
    PICKAXE(PickaxeItem()),
    MATERIAL(MaterialItem),
    TOKENS_WITHDRAW(WithdrawItem(Stats.TOKENS)),
    EMERALDS_WITHDRAW(WithdrawItem(Stats.EMERALDS)),
    MONEY_BOOSTER(BoosterItem(Stats.MONEY)),
    TOKEN_BOOSTER(BoosterItem(Stats.TOKENS)),
    MINE_T1(CrateItem(Crates.MINE_T1)),
    MINE_T2(CrateItem(Crates.MINE_T2)),
    MINE_T3(CrateItem(Crates.MINE_T3)),
    DESIGN(CrateItem(Crates.DESIGN)),
    MYTHICAL(CrateItem(Crates.MYTHICAL)),
    EARTH(GroupItem(Groups.EARTH)),
    AQUA(GroupItem(Groups.AQUA)),
    AIR(GroupItem(Groups.AIR)),
    SKY(GroupItem(Groups.SKY)),
    COSMOS(GroupItem(Groups.COSMOS)),
    SUN(GroupItem(Groups.SUN)),
    GALAXY(GroupItem(Groups.GALAXY)),
    UNIVERSE(GroupItem(Groups.UNIVERSE)),
    LC(GroupItem(Groups.LS)),
}