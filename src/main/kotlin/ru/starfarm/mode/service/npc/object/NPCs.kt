package ru.starfarm.mode.service.npc.`object`

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.ApiManager
import ru.starfarm.core.entity.impl.FakePlayer
import ru.starfarm.core.entity.type.Interact
import ru.starfarm.core.inventory.item.BaseItem
import ru.starfarm.core.inventory.item.ClickItem
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.ItemUtil
import ru.starfarm.core.util.texture.skin.Skin
import ru.starfarm.core.util.texture.skin.SkinTexture
import ru.starfarm.core.util.texture.skin.SkinUtil
import ru.starfarm.mode.NEUTRAL
import ru.starfarm.mode.Plugin
import ru.starfarm.mode.References
import ru.starfarm.mode.hexed
import ru.starfarm.mode.service.player.playerScope
import ru.starfarm.mode.service.prestige.PrestigeService
import ru.starfarm.mode.service.shop.Shops
import ru.starfarm.mode.service.skill.Skills
import java.util.*
import java.util.function.BiConsumer

enum class NPCs(
    private val customName: String, private val skin: Skin?, private val x: Double,
    private val y: Double, private val z: Double, private val worldName: String? = null,
    private val click: (Player) -> Array<String>,
) {

    WIZARD_TRADER(
        "§dМагический Торговец",
        SkinUtil.getSkin("simplesimon18"),
        60.5, 102.0, .5,
         click = {
            Shops.EMERALD.openShop(Plugin.playerService, it)
            arrayOf(
                "Ццц...",
                "Как долго ты тут стоишь?",
                "Какой воздушных поток донёс тебя сюда?",
                "ફરીથી અને ફરીથી"
            )
        }
    ),
    MINER(
        "§eШахтёр",
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTU5NTQwNTU2MzczNiwKICAicHJvZmlsZUlkIiA6ICI0NzY2ZGEzOTNlOGQ0YTRmYWZhMjE2OWM2YWJhZDI0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJzYWx0ZWRDYXNoZXdzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk5MzEzZWZiZmUyNzkwYWQ1YjhhNmM2YWNhNzhhMzY0NzAwZGVlOTBkMzc1MGQxNTIzZjdhNTA5NTExMWMyMWIiCiAgICB9CiAgfQp9",
                "MfICMaiHxUpJ23qdMrNtx9ix7epxqDyWXBNQmz5GE3RvIJyLf0DdW34tFrl8eHFZG5WVZfaTqbDJxkzFoFtjnTfhw5qj++yQN3U1044TJ3PxADmOny3fbuRGYP6UJnoecjC/lff4fGhPrhJGgQM3/Dbln5ZtlFEsVu0cUFUS6Mfnwdk2rgrIqiwwv9LvqIySn46GOo/1wbx50Vqk8NXexiesAZIDmyfhAdFEIPsaWHHyZCXyHyuPentGzb98ruTvIep+OKbML9PoaFFnaGpb2BKK0i0wYi34bStQxKRTNWHdnFllDtV4/M+3rCY/Ddl6wDuJETrPdf0a1DNeGFW9DOoPekoQhJI1qhEEXFPvBm+2lO5prUkvZmrde0p+XEPYmS/snUdie/thOzftFlwyEwE6bDnvYNwucUBmnvO8XckG9wv1E6PfjiFSXtwc+2PHczOzhg+rp4YeE/7akAQdv67Jh7UbJem0xQVC9LqqF2aaRikBZ/oMEg5cYLiHqotEHdngP6ASxrFnLZi7zusM0bSxTwhNOU43tzIWEQAFObzVOUnjuL4B+uK0P6+rnT4JtHQOnOWQ0yHDdfrZr0cjtdu4Db4Be5gc+s/D6VRb1WxbrM8Df+FqeKxrPAI1YW8bqsHn9e6LVP0UISXrhoTdSDEw06n2kuaQSEnSXfCF3Q0="
            )
        ),
        32.5, 102.0, 3.5,
        click = {
            ApiManager.createInventoryContainer("${NEUTRAL}Шахтёр", InventoryType.HOPPER) { _, container ->
                container.addItem(
                    ClickItem(
                        2,
                        ItemUtil.of(Material.ENDER_PEARL)
                            .name("§eТелепортация на шахту")
                            .lore(
                                "${NEUTRAL}Телепорт происходит на",
                                "${NEUTRAL}приватную шахту.",
                                "",
                                "§7В нашей вселенной существуют",
                                "§7множество блоков шахты, которые будут",
                                "§7открываться по пути вашего прогресса.",
                                "",
                                "§7/mine",
                                "${NEUTRAL}телепортация на шахту.",
                                "",
                                "§eНажмите, чтобы телепортироваться!"
                            )
                            .build()
                    ) { _, _ ->
                        it.playerScope.teleportToMine()

                        it.closeInventory()
                    }
                )
            }.openInventory(it)

            arrayOf(
                "Я шахтёром лишь подрабатываю, на самом деле у меня алмазный бизнес.",
                "Какой чудесный день! Правда?"
            )
        }
    ),
    PRESTIGE_HELPER(
        "§fВербер",
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTYyNjU0NTg3NDIwNywKICAicHJvZmlsZUlkIiA6ICI5MGQ1NDY0OGEzNWE0YmExYTI2Yjg1YTg4NTU4OGJlOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFdW4wbWlhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0N2M5NDJmZDJlMjkxYzM0NTY1OThhNjRjNTUxM2JiYjM5NTJjOWRjYjA3NGMwZDM4MzgyMGVhNTc0NDU4NjgiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "lK4UgbEp8uXR/0V2GQi07kTBfU5yzkuaw6e6nkpgt9u0kwcywnPb0kHTgtEgvGqB+9glCe0Jj7cR6AaKxW4TApsYXaq3ExGMjOH3CS9X/nO3Gbmu1wDq0C9BqeoAwPbKB+X/XyfNeErWoRhrjObmOzBNh9PPVTvwj+I0xyvC9hbj5VlhBpegnOOrd7tWTPuVZB1GQge3/j5y7KoDS2c0n6xzzs4V7E3udnI7cms8xWQ+zGMrXFZ6iHIt5bQupNAQeBjUxvA2SMbkXwPnJ3TJfQmfcmBjJs7JrO2wuivZEbYexBusUI7dos1JrdSofPQpjjjwt/QcHcXw+wWlfMdPCwOpS28KSZzYhCLWlJG1mtVS4cmD41VBCaS60AkTUQPCIHyqbd+v+hclrAw0iJj18axuirmMh62O2p4sucXAaa6Pw+X31e5DSTK0l0wQ0fWgYway7KSLipJdULdB99H7y3TPQFksoVd0VVbg0PfkDg3c2pHMJT3uJhYJeG3OBdsJ621m9gjTOI2e8zyS308eYR8SAAJ/Pdy7yGywQgjikzopHlBb/segLgPYDYjr97wjKKh6g+DyJmc/edRPSFH/poEr0jY7X9gnnnWxJQtq7SPLc9Zu7XLfXOG5+Azw0neVdNAyfteb/XZrKRfC4josV3HilX/J3j7Bk7mXyhcmh68="
            )
        ),
        11.5, 102.0, 0.5,
        click = {
            ApiManager.createInventoryContainer("${NEUTRAL}Помощник", InventoryType.HOPPER) { _, container ->
                container.addItem(
                    BaseItem(
                        1,
                        ItemUtil.of(Material.PAPER)
                            .name("Престиж")
                            .lore(
                                "§7Престиж необходим для разблокировки",
                                "§7получения больше денег с разнообразных наград.",
                                "§7Максимальный престиж равен §n${References.format(PrestigeService.MAX_PRESTIGE)}§r§7.",
                                "",
                                "§7/prestige",
                                "${NEUTRAL}улучшить престиж на 1 единицу.",
                                "",
                                "§7/maxprestige",
                                "${NEUTRAL}улучшить престиж на все деньги.",
                                "",
                                "§7/autoprestige ${NEUTRAL}(от PREMIUM+ и выше)",
                                "${NEUTRAL}включить автоматическое улучшение",
                                "${NEUTRAL}престижа на все деньги."
                            )
                            .build()
                    )
                )

                fun getItemStack(): ItemStack =
                    listOf(
                        "§7Происходит для всех",
                        "§7игроков каждые 30 секунд,",
                        "§7улучшая престиж до максимально",
                        "§7доступного по деньгам игрока.",
                        "",
                        "§eНажмите, чтобы переключить."
                    ).run {
                        if (Plugin.prestigeService.isAutoPrestige(it))
                            ItemUtil.of(Material.ENDER_EYE)
                                .name("§aАвтоматическое улучшение престижа")
                                .lore(this)
                                .build()
                        else
                            ItemUtil.of(Material.ENDER_PEARL)
                                .name("§cАвтоматическое улучшение престижа")
                                .lore(this)
                                .build()
                    }

                container.addItem(
                    ClickItem(3, getItemStack()) { _, event ->
                        if (it.playerScope.profile?.donateGroup?.ordinal!! < 4) return@ClickItem

                        Plugin.prestigeService.toggleAutoPrestige(it)
                        event.currentItem = getItemStack()
                    }
                )
            }.openInventory(it)
            arrayOf(
                "Ты замечал надпись над своей панелью быстрого доступа?",
                "Ходил слух, что в этом мире нас награждают за каждые 500 престижей, давай проверим?"
            )
        }
    ),
    TOKEN_HELPER(
        "§3Эней",
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTY1NDUzMjAxMjIzMiwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMGM2NjdhYjNhZThiNTBhOTFkZmVkZGYxOGYwNmI3ZjVhOWJlMzNiOTYzMTc1ZjQ4MTgxNGJiNTYwOWZiZTE5IgogICAgfQogIH0KfQ==",
                "EA9Hp6AGf8l9u/vF2KEL83mmX6LL2/INIkEZPGmmgBTd9KFzO0VzJjH8SMR0lIPEjfb240WtlZNMlvK3hKJ3bChcPd78UFZ/HnzVOXDfrpdoP+zuJxgqc7p+GPj2h0+5SAq1/kDcpVrlMW8fd9jYAb4uDGUSTUCk1ORurXPyvWokYMBQCuHPIxfzbn+NT2zxA11YvQuVr0RdLA/U/L+eEvq1BVpgrG7KwI1lAoS18e6PQpfALE593kS+tFgeYzn1v1tb1mT39Hs1G9SZM6xFCQPJV0HvQCZSry1d19UuxcxHxGQa/XiaJB6y6d2OWtRkSnGA9TNB5lPL3ptqdaGfkHkUPXJEnKM9bUDUFdI1JHWqBOCElJjGLSTWAC2VB1Uthmx0RRgL2rYZKD1RDU44Nbi6cNOW3qoQ8bNElhMHYGCiRTbGVb9bVqCPlmOXKx56YyxcR98q6ag0vF4LasBpND818eKlvy3NKwWIQRcKmYKtjf0B6WSXaJGTQftY+44Uh1ZjX9yElfAgFnzgNueQ+vurvOgOdp90s2qu164GK2zSroTFLEaNyCYhz5Df89JiBFK8l94pOGLwxkVkdtXY85itwoVgwEppztNBmDIVz/jolZ5VDPjJjTcvKdWECJmFFsedgUOfgcAp0s96YLwoVK5CPLfJrBlePOCYUAahWQE="
            )
        ),
        55.5, 102.0, 13.5,
        click = {
            arrayOf(
                "Ты слышал о токенах?! Нет?!! Это прекрасная вещь для обмена с игроками и улучшения своего инструмента!",
                "Пр.. прив... привет! Не мог бы ты одолжить пар.. парочку токенов при помощи \"§3/tokenswithdraw <количество>§f\"?"
            )
        }
    ),
    EMERALD_HELPER(
        "§aБориль",
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTY1ODc0Mzg1NDA5NywKICAicHJvZmlsZUlkIiA6ICI5MThhMDI5NTU5ZGQ0Y2U2YjE2ZjdhNWQ1M2VmYjQxMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWV2ZWxvcGVyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdhYjQ1YTg2ZGY2Y2FmNmE4MWZhMTRmNTUyMjlkYzNiZDU2MDE5ZDhhM2NhOTU2YWRiMjllMjZhMjc1M2Y2YTMiCiAgICB9CiAgfQp9",
                "gCOyE+hXZkVG3YE+2fPmQ7WmvAAFwVMOCUwPiwheVGqOtkxeHNjvA6B4QS6i0FnWVZ0qbuZ5AnmvdEyBfyzLdMu1UnN8SAWR70/hftjwv4wjOC+rBYPGtOiy+a7dxnBsJ1341DjkfqIhhD4tGToCkchz9x3gQn0pTmXFjdT47V25TN3cQ/RRZqZgvTG54yv5QuBMsokswJzxAomP0NMqNgnh/NFVq2+vqD0/ME0whiP9GQAz7oMNhRfkjGilZroe/TKjk2Z/VLznHe9i/Wl4uMY5yvQeBhZo1/voywZPqMe4ZPBzhk1x/HM35w0qEQDjm8WaxiN2crg3kQaQvRgfj3U+31qhtscEHLb+p2tQI6jo13VxOMpnUS90r63kB+EdSQ3e8gsgZ7udbuq0Pbv229ajM5Nm2DUucH9VvzDtftoo27Q6FgdOyUP6BQG/Yg6eaSJzHjulPJFzju8xcKFngn2c72+2ApJTQ7guNCQ+EWX2T1GtNEMxF9CfPTV73oZ0bS0vMXF7EEkKTdTalTwfITiv2xWbheUWeTVA6ELJ14rE/T4BfRoJcFsxPzAZKl4mEdtdz155DAL3jnLbx/epTNe0y3Tpj1RFDokMWWoSLyxw4oKwe80Mpz1/vefXKbwnaYEFFfqpfXIZOfD0OM4PauH5Uc3AdsXt7kADPBwmtpA="
            )
        ),
        84.5, 102.0, -16.5,
        click = {
            arrayOf(
                "Ты замечал на этом острове странного торговца?",
                "Изумруды, чудесные изумруды! Такие блестящие! Так ещё и подходят для торговли.",
                "А сколько у тебя зелёных самоцветов? У меня вот целых 7 штук! Покажи-ка и ты при помощи \"§a/emeraldwithdraw <количество>§f\"."
            )
        }
    ),
    PICKAXE_HELPER(
        "§bЛусиус",
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTYzOTUyOTkxMTkwNSwKICAicHJvZmlsZUlkIiA6ICIxN2Q0ODA1ZDRmMTA0YTA5OWRiYzJmNzYzMDNjYmRkZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJnaWZ0bWV0b25uZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThiYmM2Mjc1NmVlZDAwYjMwOTMyM2I3NTVjNTM3YjIxYzViYjUxOGQyNTlkZmQyMmIwYTllZmUwYTlkYzVhNSIKICAgIH0KICB9Cn0=",
                "KEGG/Rf2URQJ88RzG0/fgiLkQ2NJ0/1ms9IpryHd+5m32e98neyxpx0OEPSPlR6/owskyqhN6NBH6Svg7FuD/jZ5ozTl37FKrsWNztcjTM4X9tnUDpCigRG7x7CEKJsmDzPrkrvHFeCDej7T+NHoYz91An9EcnQ2bslZiPrWYK4y6xfZU/vXnccwRCHl8tv/CKarg7ZS1rdOV9M3Y0g7ZneLm1v9wG2yfx8eJV70BJesH784w2MR/2ZX2oxoSrFRCfA0QLrT+Qtm+6wbYj48a2kpvju+r90OyFDP+4+8jAdRNOiXuIbzccTsyalMHH8sH+ubpqCkf7aQq/Dl65iaUHcs7luk0EVJOiFpQfFqLdkwfPYPNdlKuk6Vaso0lbiCte+4ID6X2sJjJ/g5KtuR6ty+eJ3BBrCo8v9gD9QM9MYDGS3gCHm4RQUpcRKpnW460woYPctwfgI8FRh8aMMh7s4OfIzNL4NZqE6/Vo4tsIp7VLIJOEdRiPHf0kN36qFugNOB/FLgzqAU89puUha6vyoUO3VPVXKjTdpqQkv32sdeTRJqS3+MBwoYt9Np7JeWIP7OwmY3BeivZKny9GfMiJeyTUjrsbZkk651nOOLeh8BUVZnRTYpdaTGVwrPyfllyG7W+80/ctgfNPMwq9cfRkBbS77XXY0O7ZKpn1H6TH0="
            )
        ),
        96.5, 102.0, -1.5,
        click = {
            arrayOf(
                "У тебя тоже есть этот прелестный инструмент для добычи разнообраных валют?",
                "Эх! Чуть-чуть не хватает, чтобы улучшить скорость на своей кирке.",
                "Я заметил одну приятную вещь: за каждые 5 уровней кирки выдают по одному реликвийному ключу!"
            )
        }
    ),
    MINE_HELPER(
        "&#d4fbd5Ф&#ddfdd6р&#c7fccdо&#b7fcc9н&#d0fdd8н".hexed(),
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTYxMDk3MDAzNzA4OCwKICAicHJvZmlsZUlkIiA6ICJmNWQwYjFhZTQxNmU0YTE5ODEyMTRmZGQzMWU3MzA1YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYXRjaFRoZVdhdmUxMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81ODdhNTc5NWQ2NDliZTIzZGM2MDdiYjY5YmU5ZmE3MmYwYmQxM2JhMWE3MWJjNDE4YzI0YzQzYWEyMGFlYmM5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "tBb7oAzWRsdIIEd+i+de4oR5YaPOGve7n79y1dWnkGwYf6LlJdo3EZO4tbrnEP776xYDodwxgeXxZD1LchOOyha1Z9f4QGPN9BV6oDNgJBN1FTci3EK5IpuIU1QuaFPPdq7BUxu6GIcsxKlQDyhcES/iXbUqygS1FQ/cdip682AAXP8ucjKSAbKRZTGXIabNvuZxDi5FUl0JrDTPKIre0ta3Mrv4c7vJJo+1YKTgX+S/+Q83etAvzTbXZf20yduXTwHjivgVqAKFkqK5phZ/zc3PJ4C+xlUkp/yLZSVvhiUk6s3qC8cua4Ly3syDfRhlqR6Mto5IDc8I9dUKibkVIXKNLoVjw+oUaFQpuKEfEi6YKfdaWqkkd2LSCzGRNOG+SFrChoWwS16NcpaROf2ByECI7xPMRSmM/ebOuUsayONKSxDFGr9RD89I+t9qlQaUG8YNWO4+wjX3QI+snVk9ncBDpNCeniVoPOnDxDEp9GiMVRXHuYkW1ZBnKV9DRo2M7bd+P5x6pFJM9RD89ddJerjo6o4V6KB7eEygjRR4/GYJ3pdRLKNc5wSjyusdFGKWgR+ZsNIkmeDNOXIDHMnvOC60IieZ4OWLjWvsEdw6WS9leeTZG9xucrJII4DQUxbnUpu/LebAynt9ad4tHkWtv7MN7mLkU7ACnuWSD0Lc3as="
            )
        ),
        3.5, 70.0, 22.5, "mine",
        click = {
            it.playerScope.mineScope.openInventory(it)

            arrayOf(
                "Привет! Не хочешь ли обновить свою шахту?",
                "А ты знал, что новые блоки можно выбить из ящиков?",
                "Жалко, что шахта не увеличивается, но я обещаю, что скоро это исправлю!"
            )
        }
    ),
    SKILL_HELPER(
        "§9Сайфер",
        SkinUtil.of(
            texture = SkinTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTY2MTc1MTcxNjU1NywKICAicHJvZmlsZUlkIiA6ICIyNDEzZGZhMzk0NGQ0NDZhYTFiZjY3N2YwOThiNzhlZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBenp6dXJhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NjZTMwNTJkYThiNDQ0NjAyZTY1OGE2YWJjNzhmZGM4ZjU2ZGFhOGIxYzMyNjg3YmEyNDIyNGI1ZjEzZDkwY2IiCiAgICB9CiAgfQp9",
                "XxiBoGGOKE9XB6RE5TywtUZb08RBAf/ctgwrkO/60rhkHoYh6wReGRoNSzVfc5EDnz+vNBvQob0SPazf0mDHx92uxdl4RWGH5GQvxyJbwYjYN/+KysJZJpNJ9KpQ3FUvMcqXKElwBlWXgaiCLNxeq9eLcGEaapvgRv2u7cPCei4ApBKZb9HTYXI7DDsU0uCJCl+rJqyEBSQ9OVMm4dX2NZGuXy2YyWTuaJyYeqT+DDO8duPABy18YE3rNWqKqGdB3HQ4esukA/+BMzp0jqAQ+hVHmxa9284DNZZZuXLlFBK6FVBoQ9zA9c6XDNtQnUN72rf9X7B4t2VocyyCSib5xWFd1s7o9vsvc0ARBASz8EsrfUcruvMKHp83yTsKxwiECFSHRrhsE/GTO8dvBhUA1VWf1Jfvy0Fu6AHXCVJS47Rl+dK+pj70PPDmZ9w7JeyO1hZJyVGcg9dADjsqmowJ2UJwpfGZV3fKnleTAlFyfK0ybt+aM5k2UWc3g9+Ip10OeZ+vpp0Q8Djvg98qEdpyTLUNAPfFaF8iOCWE/AF+x2BWTZE9fsr9WUx5chhn2OHcoIg85L9PJxki/6HMPkPMDnhBNmuddtnDHoqq+YN/Xt2n7lb1j0ZBUbYim/xsZxHYQXByntEru3cO4BfnxX4/q4wOxYMRKg1iSjDEks4jm6A="
            )
        ),
        42.5, 102.0, -21.5,
        click = {
            Skills.openSkillsGui(it.playerScope)

            arrayOf(
                "А ты уже получил свой гарантированный мистический ящик?",
                "\"Копай больше и получай больше\" - это проверенная победная стезя!",
            )
        }
    ),
    ;

    companion object {
        private val playerCooldowns = arrayListOf<UUID>()
    }

    val fakePlayer get() =
        FakePlayer(
            skin ?: SkinUtil.NULL,
            Location(worldName?.let { Bukkit.getWorld(it) } ?: Bukkit.getWorlds()[0], x, y, z)
        ).apply {
            hologram.textLine("§e§lКЛИК")
            hologram.textLine(this@NPCs.customName)

            click = BiConsumer { player, interact ->
                val uuid = player.uniqueId
                if (interact != Interact.CLICK || playerCooldowns.contains(uuid)) return@BiConsumer

                playerCooldowns.add(uuid)

                Plugin.taskContext.asyncAfter(20) { playerCooldowns.remove(uuid) }

                sendMessage(player)
            }
        }

    fun sendMessage(player: Player) =
        ChatUtil.sendMessage(player, "§e[NPC] ${ChatColor.stripColor(customName)}§f: ${this@NPCs.click.invoke(player).random()}")
}