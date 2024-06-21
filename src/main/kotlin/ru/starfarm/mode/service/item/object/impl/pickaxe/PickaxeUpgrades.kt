package ru.starfarm.mode.service.item.`object`.impl.pickaxe

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedBlockData
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.starfarm.core.ApiManager
import ru.starfarm.core.entity.impl.FakeArmorStand
import ru.starfarm.core.entity.impl.FakeChicken
import ru.starfarm.core.entity.impl.FakeDropItem
import ru.starfarm.core.entity.impl.FakeGiant
import ru.starfarm.core.entity.type.Interact
import ru.starfarm.core.inventory.container.Container
import ru.starfarm.core.inventory.item.ClickItem
import ru.starfarm.core.protocol.block.v1_16_R3PacketMultiBlockChange
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.core.util.math.Cuboid
import ru.starfarm.mode.*
import ru.starfarm.mode.service.item.`object`.Items
import ru.starfarm.mode.service.item.`object`.impl.crate.CrateItem
import ru.starfarm.mode.service.player.`object`.PlayerScope
import ru.starfarm.mode.service.player.`object`.Stats
import ru.starfarm.mode.service.player.`object`.booster.`object`.BoosterCause
import ru.starfarm.mode.service.player.`object`.crate.Crates
import ru.starfarm.mode.service.skill.Skills
import java.util.function.BiConsumer
import java.util.function.Predicate
import kotlin.math.*
import kotlin.random.Random

enum class PickaxeUpgrades(
    private val slot: Int = 0,
    private val material: Material = Material.AIR,
    private val chatColor: ChatColor = ChatColor.WHITE,
    private val data: Int = 0,
    private val canToggle: Boolean = true,
    val maxLevel: Int = 0,
    private val currency: Stats = Stats.TOKENS,
    private val price: Double = .0,
    val beginLevel: Int = 0,
    private val rowName: String = "",
    private val describe: Array<String> = arrayOf(),
) {
    EFFICIENCY(
        10, Material.REDSTONE, ChatColor.GREEN, canToggle = false,
        maxLevel = 100_000, price = 5.0, beginLevel = 25,
        rowName = "Эффективность",
        describe = arrayOf(
            "Увеличивает скорость копания."
        )
    ),
    FORTUNE(
        11, Material.GOLDEN_PICKAXE, ChatColor.GOLD,
        maxLevel = 200_000, price = 1.25, beginLevel = 25,
        rowName = "Удача",
        describe = arrayOf(
            "Каждый уровень добавляет",
            "дополнительный блок за вскапывание."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) = playerScope.addAmount(Stats.MONEY, currentLevel * 1e8, true)
    },
    TOKEN_MINER(
        12, Material.MAGMA_CREAM, ChatColor.YELLOW,
        maxLevel = 20_000, price = 5.0, beginLevel = 150,
        rowName = "Добыча Токенов",
        describe = arrayOf(
            "Шанс найти токены при вскапывании.",
            "В чат пишутся сообщение о добыче,",
            "в эквиваленте больше чем 50.000."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.03 + (currentLevel / 2000000)

            if (Random.nextDouble() > chance) return

            var combo = 0
            val amount = Random.nextDouble(1.0, 100000.0).run {
                val comboLevel = playerScope.getPickaxeUpgradeLevel(COMBO)

                if (comboLevel <= 0) return@run this

                val comboChance = 0.05 + (comboLevel / 100000.0)
                if (Random.nextDouble() > comboChance) return@run this

                combo = Random.nextInt(2, 6)

                this * combo
            }

            if (amount >= 50000.0)
                ChatUtil.sendMessage(
                    playerScope.player,
                    "${
                        Stats.TOKENS.colored(
                            "§l+ ${References.format(amount)}",
                            true
                        )
                    }${if (combo == 0) "" else " ${NEUTRAL}§o(x$combo)"} §7§o(${currentPickaxeUpgrades.rowName})"
                )

            playerScope.addAmount(Stats.TOKENS, amount, true)
        }
    },
    EMERALD_CHICKEN(
        13, Material.EGG, ChatColor.GREEN,
        maxLevel = 5000, price = 5.5, beginLevel = 0,
        rowName = "Изумрудная Курица",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "появится курица, которая",
            "при ударе выдаёт изумруды."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.005 + (currentLevel / 1000000.0)
            if (Random.nextDouble() > chance) return

            val player = playerScope.player
            val playerLocation = player.location

            player.playSound(location, Sound.ENTITY_CHICKEN_AMBIENT, 1f, 1f)

            FakeChicken(
                Location(
                    location.world,
                    location.x,
                    location.y,
                    location.z,
                    abs(playerLocation.yaw) - 180f,
                    0f
                )
            ).apply {
                customNameVisible = true
                customName = "§e§lУДАРЬ МЕНЯ"
                access = Predicate.isEqual(player)

                val task = Plugin.taskContext.asyncAfter(80) { remove() }

                var attacked = false
                click = BiConsumer { _, interact ->
                    if (interact == Interact.ATTACK && !attacked) {
                        val stats = Stats.EMERALDS
                        attacked = true

                        playerScope.addAmount(
                            stats,
                            Random.nextDouble(1.0, 4.0).apply {
                                ChatUtil.sendMessage(
                                    player,
                                    stats.colored("§l+ ${References.format(this, "###.##")}", true).plus(" §7§o(${currentPickaxeUpgrades.rowName})")
                                )
                            },
                            true
                        )

                        player.spawnParticle(
                            Particle.VILLAGER_HAPPY,
                            location.clone().add(0.0, 0.4, 0.0),
                            15,
                            0.5,
                            0.5,
                            0.5
                        )

                        player.playSound(location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1f)
                        remove()
                        task.cancel()
                    }
                }
            }
        }
    },
    SPEED(
        14, Material.SUGAR, ChatColor.WHITE, canToggle = false,
        maxLevel = 5, currency = Stats.EMERALDS, price = 20.0,
        beginLevel = 0,
        rowName = "Скорость",
        describe = arrayOf(
            "Увеличивает скорость",
            "передвижения с киркой в руке."
        )
    ) {
        private val players = hashMapOf<Player, Int>()

        init {
            Plugin.taskContext.every(0, 20) {
                players
                    .filter { it.key.isOnline }
                    .forEach {
                        val itemInMainHand = it.key.inventory.itemInMainHand

                        if (!itemInMainHand.hasItemMeta()) return@forEach

                        val displayName = itemInMainHand.itemMeta?.displayName ?: ""

                        if (displayName.isEmpty() || !displayName.contains(it.key.name)) return@forEach

                        it.key.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 300, it.value - 1, true, false))
                    }
            }
        }

        override fun initialize(playerScope: PlayerScope) {
            if (!playerScope.hasPickaxeUpgrade(SPEED) || players.containsKey(playerScope.player)) return

            players.merge(playerScope.player, playerScope.getPickaxeUpgradeLevel(SPEED)) { _, newValue -> newValue }
        }

        override fun onUpgrade(playerScope: PlayerScope, currentLevel: Int) {
            players.merge(playerScope.player, 1) { oldValue, newValue -> oldValue + newValue }
        }
    },
    JACKHAMMER(
        15, Material.TNT, ChatColor.RED,
        maxLevel = 15_000, price = 2.0, beginLevel = 150,
        rowName = "Отбойный Молоток",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "сломается слой шахт.",
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.000005 * currentLevel
            if (Random.nextDouble() > chance) return

            val mineCuboid = playerScope.mineScope.mineCuboid

            val minPoint = mineCuboid.min
            val maxPoint = mineCuboid.max

            fun generateLayer() = apply {
                val y = location.y

                val airBlockData = WrappedBlockData.createData(Material.AIR)

                Cuboid.atPoints(
                    location.world ?: return@apply,
                    minPoint.copy().withY(y),
                    maxPoint.copy().withY(y)
                ).blockByChunks.entries.associate { entry ->
                    entry.key to entry.value.map {
                        it.location.apply {
                            FORTUNE.handle(this, playerScope, FORTUNE, playerScope.getPickaxeUpgradeLevel(FORTUNE))

                            playerScope.mineScope.decrementBlock(playerScope.player)
                        }.toVector() to airBlockData
                    }.toTypedArray()
                }.forEach { (chunk, blocks) ->
                    v1_16_R3PacketMultiBlockChange().apply {
                        sectionPosition = chunk
                        blocksData = blocks
                    }.send(playerScope.player)
                }
            }

            Plugin.taskContext.asyncAfter(0) {
                generateLayer()
                playerScope.increaseSkill(Skills.BREAKING_LAYER)
            }
        }
    },
    LUCKY_BLOCK(
        16, Material.SPONGE, ChatColor.GOLD,
        maxLevel = 10000, price = 6.0, beginLevel = 0,
        rowName = "Лаки Блок",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "появится лаки блок, который",
            "принесёт случайную награду."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.00075 + (currentLevel / 100000000.0)

            if (Random.nextDouble() > chance) return

            FakeArmorStand(
                location.clone()
                    .subtract(0.0, 2.1, 0.0)
            ).apply {
                itemSlots[EnumWrappers.ItemSlot.HEAD] = ItemUtil.getSkull(
                    "ewogICJ0aW1lc3RhbXAiIDogMTY1ODUyODY5NjI3NywKICAicHJvZmlsZUlkIiA6ICJmZDYwZjM2ZjU4NjE0ZjEyYjNjZDQ3YzJkODU1Mjk5YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZWFkIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1YjBlMWI5NDNhZDllODA5M2VmNDJlNGJkMjdmYzMxNzcxMDVjM2RlNzM3MTYyOTNkNGU2YzdmNGIzNjMxYWYiCiAgICB9CiAgfQp9",
                    "ntx6FLw4K015kHo9VUkBWWQ/sT5B9A/eiVsumDi+1xqoK/BxAE8nQ5dPBIL9e/ijYjxBEY01QkJYvXCJUQr6/wka/M67ljl2B11VccOLs2ytn/DhA3QzuktAwhqci7L5OZeLNoi2oM3cu/jjlhnHoY1PRnQBB+Gx/qivFDfjbsI2oaIxd7i5q6ZMANe689fY7FRuQB+a+KX0No2NW7nP7xellCs9SCVU9DhXjXoWJFagh+YZj76Ess8j9wCrKwUEA7NkMZlidxSNTzPA5nQiv6cQSbTobBG6XUDWPu0XeWXFRtlPw8OSNBP0DbXdeAP417+f0Ju90ph2hQfjily4OSEom9FlSS3bOEBp7D6fNrnmvAKRrxN04k1kePflC6edGWsa+1mMj0P3mi+K9ms1jYamSmbmQfZsb12odNOWmPf23i4ZznAlwcqRI3n4yqnTRuATl4cc8rpm/2tDsCWgQHOKwChTwjpzzVnP20jXvm/b8h4zx+QmltRg+4zuh6llGeMToVdJFdhmjWERicD1WH+p4pssK4SV8L8ZUyfpQd/LV0Cx3GRoHWIK0uUQOR4WI5Hwg57qmaNC1V4/0VzMHOYbf3zri9VnY0YluGkvl5YdOZsMr4xgF3AuOgL9upvYsQdxzcsth7S9Djwu4QPcCLPpccg2pZyc+yOzd7NvGRI="
                )

                invisible = true

                val player = playerScope.player

                access = Predicate.isEqual(player)

                var rotate = 1f

                val task = Plugin.taskContext.everyAsync(0, 1) {
                    setHeadRotation(0f, rotate, 0f)
                    rotate += 8
                    teleport(location.add(.0, .05, .0))
                    player.spawnParticle(Particle.CLOUD, location.clone().add(.0, 1.1, .0), 1, .0, .105, .0, .0)
                }

                Plugin.taskContext.asyncAfter(100) {
                    fun generateReward() {
                        FakeDropItem(location.add(.0, 1.5, .0)).apply {
                            access = Predicate.isEqual(player)

                            item = when (Random.nextDouble()) {
                                in 0.0..0.35 -> {
                                    val percent = Random.nextInt(100, 125)

                                    val amount = Plugin.prestigeService.getPrestigePrice(
                                        playerScope.getAmount(Stats.PRESTIGE) + 1
                                    ) * percent / 100

                                    val stats = Stats.MONEY

                                    playerScope.addAmount(stats, amount)

                                    ChatUtil.sendMessage(
                                        player,
                                        stats.colored("§l+ ${References.format(amount)} ${stats.rowName} §7§o(${currentPickaxeUpgrades.rowName})")
                                    )

                                    glowColor = ChatColor.GOLD
                                    customName = stats.colored("§l+ $percent%", true)

                                    ItemUtil.build(stats.icon) {}
                                }

                                in 0.35..0.65 -> {
                                    val amount = 2e5
                                    val stats = Stats.TOKENS

                                    playerScope.addAmount(stats, amount)

                                    ChatUtil.sendMessage(
                                        player,
                                        stats.colored("§l+ ${References.format(amount)} ${stats.rowName} §7§o(${currentPickaxeUpgrades.rowName})")
                                    )

                                    glowColor = ChatColor.DARK_AQUA
                                    customName = stats.colored("§l+ ${References.format(amount)}", true)

                                    ItemUtil.build(stats.icon) {}
                                }

                                in 0.65..0.9 -> {
                                    val amount = 5e5
                                    val stats = Stats.TOKENS

                                    playerScope.addAmount(stats, amount)

                                    ChatUtil.sendMessage(
                                        player,
                                        stats.colored("§l+ ${References.format(amount)} ${stats.rowName} §7§o(${currentPickaxeUpgrades.rowName})")
                                    )

                                    glowColor = ChatColor.DARK_AQUA
                                    customName = stats.colored("§l+ ${References.format(amount)}", true)

                                    ItemUtil.build(stats.icon) {}
                                }

                                in 0.9..0.95 -> {
                                    val amount = Random.nextInt(1, 10).toDouble()
                                    val stats = Stats.EMERALDS

                                    playerScope.addAmount(stats, amount)

                                    ChatUtil.sendMessage(
                                        player,
                                        stats.colored("§l+ ${References.format(amount)} ${stats.rowName} §7§o(${currentPickaxeUpgrades.rowName})")
                                    )

                                    glowColor = ChatColor.GREEN
                                    customName = stats.colored("§l+ ${References.format(amount)}", true)

                                    ItemUtil.build(stats.icon) {}
                                }

                                in 0.95..0.975 -> Crates.PRECIOUS.apply {
                                    playerScope.addCrate(this)

                                    glowColor = chatColor
                                    customName = this.toString()
                                }.icon

                                in 0.975..0.9995 -> Crates.RELIC.apply {
                                    playerScope.addCrate(this)

                                    glowColor = chatColor
                                    customName = this.toString()
                                }.icon

                                in 0.9995..1.0 -> Crates.MYTHICAL.apply {
                                    playerScope.addCrate(this)

                                    glowColor = chatColor
                                    customName = this.toString()
                                }.icon

                                else -> {
                                    customName = "${NEUTRAL}Ничего ;C"

                                    ItemUtil.of(Material.COD).build()
                                }
                            }

                            customNameVisible = true

                            Plugin.taskContext.asyncAfter(60) { remove() }
                        }
                    }

                    generateReward()
                    player.spawnParticle(Particle.TOTEM, location.clone().add(.0, 1.7, .0), 20, .0, .0, .0, .5)

                    task.cancel()
                    remove()
                }
            }
        }
    },
    EXP_FINDER(
        19, Material.EXPERIENCE_BOTTLE, ChatColor.GREEN,
        maxLevel = 2000, currency = Stats.EMERALDS, price = .003,
        beginLevel = 0,
        rowName = "Перегрузка",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "вам добавится опыт кирки."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.01 + (currentLevel / 500000.0)

            if (Random.nextDouble() > chance) return

            val maximumValue = currentLevel / 100

            val pickaxe = playerScope.pickaxe

            val value = Random.nextInt(15, 30 + maximumValue)

            pickaxe.exp += value

            ChatUtil.sendMessage(
                playerScope.player,
                "$POSITIVE§l+ ${References.format(value.toDouble())} Опыта Кирки §7§o(${currentPickaxeUpgrades.rowName})"
            )
        }
    },
    GOLD_POUCH(
        20, Material.GOLD_INGOT, ChatColor.YELLOW,
        maxLevel = 7500, price = 5.0, beginLevel = 0,
        rowName = "Позолоченный Мешочек",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "выдаст 5-10% денег от",
            "стоимости следующего престижа."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.0005 + (currentLevel / 1000000.0)

            if (Random.nextDouble() > chance) return

            val multiplier = (Random.nextInt(5, 11)) / 100.0

            val amount = multiplier * Plugin.prestigeService.getPrestigePrice(
                playerScope.getAmount(Stats.PRESTIGE) + 1
            )

            playerScope.addAmount(Stats.MONEY, amount, true)
            ChatUtil.sendMessage(
                playerScope.player,
                Stats.MONEY.colored("§l+ ${References.format(amount)}", true).plus(" §7§o(${currentPickaxeUpgrades.rowName})")
            )
        }
    },
    COMBO(
        21, Material.SHIELD, ChatColor.LIGHT_PURPLE, canToggle = false,
        maxLevel = 10000, price = 3.0, beginLevel = 0,
        rowName = "Комбо",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "вы получите х2-х5 токены."
        )
    ),
    T1_FINDER(
        22, Material.GRAY_SHULKER_BOX, ChatColor.GRAY,
        maxLevel = 15000, price = 2.5, beginLevel = 0,
        rowName = "Нахождение T1 ключа",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "вы найдёте Шахтёрский T1 ключ."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.0025 + (currentLevel / 750000.0)

            if (Random.nextDouble() > chance) return

            (Items.MINE_T1.itemScope as CrateItem).addItem(playerScope.player)
        }
    },
    T2_FINDER(
        23, Material.LIGHT_BLUE_SHULKER_BOX, ChatColor.AQUA,
        maxLevel = 12500, price = 3.75, beginLevel = 0,
        rowName = "Нахождение T2 ключа",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "вы найдёте Шахтёрский T2 ключ."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.0015 + (currentLevel / 1000000.0)

            if (Random.nextDouble() > chance) return

            (Items.MINE_T2.itemScope as CrateItem).addItem(playerScope.player)
        }
    },
    T3_FINDER(
        24, Material.BLUE_SHULKER_BOX, ChatColor.DARK_BLUE,
        maxLevel = 10000, price = 5.0, beginLevel = 0,
        rowName = "Нахождение T3 ключа",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "вы найдёте Шахтёрский T3 ключ."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.001 + (currentLevel / 1250000.0)

            if (Random.nextDouble() > chance) return

            (Items.MINE_T3.itemScope as CrateItem).addItem(playerScope.player)
        }
    },
    VULCAN(
        25, Material.LAVA_BUCKET, ChatColor.DARK_RED,
        maxLevel = 15_000, price = 2.0, beginLevel = 0,
        rowName = "Мини Вулкан",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "произойдёт извержение, которое",
            "выдаёт Вулканический ключ."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.0025 + (currentLevel / 6e6)
            if (Random.nextDouble() > chance) return
            val crate = Crates.VULCAN

            val player = playerScope.player

            playerScope.addCrate(crate)

            player.spawnParticle(Particle.LAVA, location, 15, 0.1, 0.1, 0.1)
            player.playSound(player.location, Sound.BLOCK_LAVA_POP, 1f, 1f)

            FakeDropItem(location.add(0.0, 0.5, 0.0)).run {
                access = Predicate.isEqual(player)
                item = Crates.VULCAN.icon

                Plugin.taskContext.asyncAfter(40) { remove() }
            }
        }
    },
    GREED(
        29, Material.BLAZE_POWDER, ChatColor.GOLD, canToggle = false,
        maxLevel = 1000, currency = Stats.EMERALDS, price = .01, beginLevel = 0,
        rowName = "Жадность",
        describe = arrayOf("Повышает постоянный бустер денег.")
    ) {
        override fun onUpgrade(playerScope: PlayerScope, currentLevel: Int) {
            playerScope.addBooster(
                multiplier = currentLevel * .005,
                boosterCause = BoosterCause.GREED
            )
        }
    },
    METEOR(
        31, Material.FIRE_CHARGE, ChatColor.RED,
        maxLevel = 5000, currency = Stats.TOKENS, price = 10.0, beginLevel = 0,
        rowName = "Метеор",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "с неба прилетит метеор на шахту."
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.0025 + (currentLevel / 2e7)
            if (Random.nextDouble() > chance) return

            fun generateSphere(radius: Double) = apply {
                val blockX = location.blockX.toDouble()
                val blockY = location.blockY.toDouble()
                val blockZ = location.blockZ.toDouble()

                val airBlockData = WrappedBlockData.createData(Material.AIR)

                Cuboid.atCoordinates(
                    location.world ?: return@apply,
                    blockX - radius, blockY - radius, blockZ - radius,
                    blockX + radius, blockY + radius, blockZ + radius
                ).blockByChunks.map { (chunk, blocks) ->
                    chunk to blocks.filter {
                        val x = it.x.toDouble()
                        val y = it.y.toDouble()
                        val z = it.z.toDouble()

                        val distance = (blockX - x).pow(2) +
                                (blockY - y).pow(2) +
                                (blockZ - z).pow(2)

                        playerScope.mineScope.mineCuboid.contains(x, y, z) && distance < radius.pow(2)
                    }.map {
                        FORTUNE.handle(location, playerScope, currentPickaxeUpgrades, currentLevel)
                        playerScope.mineScope.decrementBlock(playerScope.player)

                        it.location.toVector() to airBlockData
                    }.toTypedArray()
                }.forEach { (chunk, blocks) ->
                    v1_16_R3PacketMultiBlockChange().apply {
                        sectionPosition = chunk
                        blocksData = blocks
                    }.send(playerScope.player)
                }
            }

            val player = playerScope.player

            FakeGiant(
                location.clone()
                    .add(.0, 8.0, .0)
                    .add(location.clone().direction.multiply(-3.0))
            ).apply {
                invisible = true
                access = Predicate.isEqual(player)

                itemSlots[EnumWrappers.ItemSlot.MAINHAND] =
                    ItemUtil.getSkull(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5NTM1NTgxNTE0OCwKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E5YTVhMWU2OWI0ZjgxMDU2MjU3NTJiY2VlMjUzNDA2NjRiMDg5ZmExYjJmNTI3ZmE5MTQzZDkwNjZhN2FhZDIiCiAgICB9CiAgfQp9",
                        "fnTZWjXU9HPgvoVXuor6xghmj3+KP+upSIvb3ngZsHB00jjeVAzvzyqJJtxXbUiwymxcOyU2MPu+lyunYBTcpfYvP0pNY9ekBphoyglG54h7vqSPQdCbsr5uiWobLNgUkrDaB0z68g2iq3vZy2nhBxt3HZ0I3gqk9penSm46RgEqaZwCdZkq2fSmSQQyvE5BODILimRlMO7zgHgc+tPE/YNd3ALsSyc0AMcZIoF7jtLjkZWWHwe5kd3U96tRLBC8/GXc7ZnqU2qMOco6p7fOZP1GzGy5Vu6hiB+D+zawBSTzT5kk9LxkNYqKD8av2z5ybm/P1zAaY8zCSr8dNyoY4HJW92wxMWVl6u7eX+wAOQ5WwISF7rFwN297m0xXIW0cuXnPQoqHLLbfSSrhT+7imcormVsVCG4Cmki1DYPnA6c07pKxlU52N0UOB6qDLq9OIft2uEb34jDxhHkk4EPMDsCL7xeD/byxQ2mpfF47pJ6ncnIeevllruJZPZIiXDsiJ9Da1QeXmK6hPFgRd4OyExEON2vsdYkMJzEfFG5dqZ5UM/YiQm9ut2SCwYIUYc4wdNNEEWs7wlY9W8foZ41bRB5pyT5WbIFwa4q2NXiNpXS96YryBSCS42M6hfpatYT5UM8YUpAijAOsjVgrtXYcsGEG/Ef12Q+ThKXswsfgjqQ="
                    )

                val newX = -1.5 * cos(Math.toRadians(location.yaw.toDouble() + 0))
                val newZ = -1.5 * sin(Math.toRadians(location.yaw.toDouble() + 0))

                val task = Plugin.taskContext.everyAsync(0, 2) {
                    player.spawnParticle(
                        Particle.LAVA,
                        this.location.clone()
                            .add(newX, 8.0, newZ)
                            .add(location.clone().direction.multiply(4.5))
                            .apply {
                                player.spawnParticle(
                                    Particle.EXPLOSION_LARGE,
                                    this.clone().add(.0, 5.0, .0),
                                    1, .0, .0, .0, 2.0
                                )
                            },
                        20, .5, .5, .5, 1.0
                    )

                    teleport(this.location.clone().subtract(.0, .7, .0))
                }

                Plugin.taskContext.asyncAfter(40) {
                    task.cancel()
                    remove()

                    player.spawnParticle(
                        Particle.EXPLOSION_HUGE,
                        location.add(.0, 1.0, .0),
                        2, .0, .0, .0, 2.0
                    )

                    it.context.asyncAfter(10) { generateSphere(2 + (floor(currentLevel / 1000.0) * 2)) }
                }
            }
        }
    },
    SCAVENGER(
        33, Material.REPEATER, ChatColor.DARK_GRAY,
        maxLevel = 12500, price = 3.0, beginLevel = 0,
        rowName = "Мусорщик",
        describe = arrayOf(
            "Шанс, что при вскапывании",
            "улучшится эффективность или",
            "удача на 1 уровень.",
            "(Не больше максимального)"
        )
    ) {
        override fun handle(
            location: Location,
            playerScope: PlayerScope,
            currentPickaxeUpgrades: PickaxeUpgrades,
            currentLevel: Int,
        ) {
            val chance = 0.004 + (currentLevel / 12e5)
            if (Random.nextDouble() > chance) return

            if (playerScope.addPickaxeUpgradeLevel(arrayOf(EFFICIENCY, FORTUNE).random(), 1))
                playerScope.pickaxe.addItem(playerScope)
        }
    },
    ;

    open fun handle(
        location: Location,
        playerScope: PlayerScope,
        currentPickaxeUpgrades: PickaxeUpgrades,
        currentLevel: Int,
    ) {
    }

    open fun initialize(playerScope: PlayerScope) {}

    open fun onUpgrade(playerScope: PlayerScope, currentLevel: Int) {}

    fun getClickItem(previousContainer: Container, currentLevel: Int, playerScope: PlayerScope): ClickItem {
        val lore = arrayListOf(
            "",
            "$chatColor§l| ${if (currentLevel >= maxLevel) "§e§lМАКСИМУМ!" else "${POSITIVE}$currentLevel${NEUTRAL}/${NEGATIVE}$maxLevel уровень"}",
            "$chatColor§l| §7Цена: §f${References.format(getPrice(currentLevel, 1), "###,###.###")} $currency",
            "",
            "§eНажмите, чтобы улучшить!"
        )

        if (isMaxLevel(currentLevel)) {
            lore[2] = ""
            for (i in 3 until lore.size) lore.removeLast()
        }

        val pickaxe = playerScope.pickaxe

        return ClickItem(
            this.slot,
            ItemUtil.of(material, data.toShort())
                .name(
                    "$chatColor§l$rowName ${
                        if (pickaxe.isToggledEnchantment(
                                playerScope,
                                this
                            )
                        ) "${NEUTRAL}(${NEGATIVE}ВЫКЛЮЧЕН${NEUTRAL})" else ""
                    }"
                )
                .lore(describe.map { ChatColor.DARK_GRAY.toString() + it }.toList())
                .addLore(lore)
                .addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_UNBREAKABLE
                )
                .build(),
        ) { container, event ->
            val player = event.whoClicked as Player

            if (canToggle && currentLevel > 0 && event.click == ClickType.DROP) {
                pickaxe.toggleEnchantment(playerScope, this)
                container.updateInventory(player)
                player.apply { playSound(location, Sound.BLOCK_ANVIL_BREAK, 1f, 1f) }
                return@ClickItem
            }

            if (currentLevel < this.maxLevel) {
                player.apply { playSound(location, Sound.BLOCK_CHEST_OPEN, 1f, 1f) }
                upgradeMenu(previousContainer, currentLevel, playerScope)
            }
        }
    }

    private fun isMaxLevel(currentLevel: Int): Boolean = currentLevel == maxLevel

    private fun upgradeMenu(previousContainer: Container, currentLevel: Int, playerScope: PlayerScope) {
        ApiManager.createInventoryContainer("${NEUTRAL}$rowName", 3) { _, container ->
            var tempCurrentLevel = currentLevel

            fun setUpgradeItems(setItemFunction: (Int, Int) -> Unit) {
                listOf(
                    1,
                    5,
                    10,
                    25,
                    50,
                    100,
                    250,
                    500,
                    1000
                ).forEachIndexed { index, it -> setItemFunction.invoke(index, it) }
            }

            fun setUpgradeItem(slot: Int, count: Int) {
                var levels = count
                if (tempCurrentLevel + levels >= this.maxLevel)
                    levels = this.maxLevel - tempCurrentLevel

                val price = getPrice(tempCurrentLevel, levels)

                container.addItem(
                    slot,
                    ItemUtil.of(Material.GREEN_STAINED_GLASS_PANE)
                        .name("§a§l+ $count уровней")
                        .lore(
                            "",
                            "${currency.chatColor}§l| §7Цена: §f${References.format(price, "###,###.###")} $currency",
                            "${currency.chatColor}§l| §7Новый уровень: ${POSITIVE}${tempCurrentLevel + levels}",
                            "",
                            "§eНажмите, чтобы улучшить!"
                        )
                        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
                ) click@{ _, _ ->
                    if (!upgrade(levels, tempCurrentLevel, playerScope)) return@click

                    tempCurrentLevel += levels
                    setUpgradeItems { index, count -> setUpgradeItem(index, count) }
                }
            }

            container.addItem(References.getReturnIcon(18, previousContainer))
            setUpgradeItems { index, count -> setUpgradeItem(index, count) }
        }.openInventory(playerScope.player)
    }

    private fun upgrade(count: Int, currentLevel: Int, playerScope: PlayerScope): Boolean {
        if (currentLevel >= this.maxLevel) return false

        val price = getPrice(currentLevel, count)
        if (price <= 0.0) return false

        val balance = playerScope.getAmount(currency)
        if (price > balance) return false

        playerScope.subtractAmount(currency, price)

        val pickaxe = playerScope.pickaxe
        pickaxe.upgrades.merge(this, count) { i1, i2 ->
            playerScope.player.apply { playSound(location, Sound.ENTITY_VILLAGER_YES, 1f, 1f) }

            (i1 + i2).apply { onUpgrade(playerScope, this) }
        }
        pickaxe.addItem(playerScope)
        return true
    }

    private fun getPrice(currentLevel: Int, count: Int): Double {
        var price = 0.0

        for (i in 1..count) price += (currentLevel + i) * this@PickaxeUpgrades.price

        return price
    }

    override fun toString(): String = "$chatColor$rowName"
}