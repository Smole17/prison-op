package ru.starfarm.mode

import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.weather.WeatherChangeEvent
import ru.starfarm.api.InitializePlugin
import ru.starfarm.api.service.BukkitService
import ru.starfarm.api.service.impl.base.BaseService
import ru.starfarm.core.util.serializer.Serializer
import ru.starfarm.core.util.serializer.registerEnumMapAdapter
import ru.starfarm.mode.service.chat.ChatService
import ru.starfarm.mode.service.npc.NPCService
import ru.starfarm.mode.service.item.ItemService
import ru.starfarm.mode.service.item.`object`.impl.pickaxe.PickaxeUpgrades
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.booster.BoosterService
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.player.`object`.donate.DonateService
import ru.starfarm.mode.service.prestige.PrestigeService
import ru.starfarm.mode.service.watcher.UpdateService
import ru.starfarm.mode.service.mine.MineService
import ru.starfarm.mode.service.player.PlayerService
import ru.starfarm.mode.service.skill.SkillScope
import ru.starfarm.mode.service.skill.Skills
import ru.starfarm.mode.service.spawn.SpawnService
import ru.starfarm.mode.service.top.TopService
import ru.starfarm.mode.service.view.ViewService

lateinit var Plugin:  PrisonOp

class PrisonOp : InitializePlugin() {

    lateinit var baseService: BaseService
    lateinit var playerService: PlayerService
    lateinit var prestigeService: PrestigeService
    
    override fun execute() {
        Plugin = this

        initSerializerConfig()
        initServices()

        Bukkit.getWorlds().forEach {
            it.pvp = false
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            it.time = 6000
        }

        on<CraftItemEvent> { isCancelled = true }
        on<LeavesDecayEvent> { isCancelled = true }
        on<BlockPhysicsEvent> { isCancelled = true }
        on<BlockFadeEvent> { isCancelled = true }
        on<WeatherChangeEvent> { isCancelled = true }
        on<FoodLevelChangeEvent> { foodLevel = 20 }
    }

    override fun close() {
        Bukkit.getOnlinePlayers().forEach { playerService.unload(it) }
    }

    private fun initSerializerConfig() {
        Serializer.apply {
            config(true) {
                it.registerEnumMapAdapter<Stats, Double>()
                it.registerEnumMapAdapter<PickaxeUpgrades, Int>()
                it.registerEnumMapAdapter<Crates, Int>()
                it.registerEnumMapAdapter<Skills, SkillScope>()
            }
        }

        BukkitService.LOGGER.info("Serializer was successfully configured.")
    }

    private fun initServices() {
        baseService = BaseService(HOST, DATABASE, USER, PASSWORD)
        baseService.updateQuery("CREATE TABLE IF NOT EXISTS $TABLE (`uuid` VARCHAR(128) PRIMARY KEY, `data` TEXT)")

        BukkitService.LOGGER.info("Table ${baseService.getTable(TABLE).name} is successfully initialized.")

        playerService = PlayerService
        prestigeService = PrestigeService

        registerService.addServices(
            ItemService, baseService, MineService,
            ViewService, ChatService, prestigeService,
            NPCService, UpdateService, DonateService,
            BoosterService, playerService, SpawnService,
            TopService
        )
    }
}