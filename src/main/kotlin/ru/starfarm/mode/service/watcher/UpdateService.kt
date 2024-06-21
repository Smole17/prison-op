package ru.starfarm.mode.service.watcher

import org.bukkit.Bukkit
import ru.starfarm.api.service.BukkitService
import ru.starfarm.core.message.IMessageService
import ru.starfarm.core.realm.IRealmService
import ru.starfarm.mode.POSITIVE
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import kotlin.concurrent.thread

object UpdateService : BukkitService {

    override fun load() {
        val pluginsFolder = File("plugins")
        val pluginFolderPath = pluginsFolder.toPath()
        val watchService = FileSystems.getDefault().newWatchService()

        pluginFolderPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

        thread(name = "Plugin-folder-watcher", isDaemon = true) {
            while (!Thread.interrupted()) watchService.poll()?.apply {
                pollEvents().filter { it.kind() != StandardWatchEventKinds.OVERFLOW }.forEach {
                    val filePath = it.context() as Path
                    val fileName = filePath.toFile().name
                    if (fileName.endsWith(".jar")) Bukkit.shutdown()
                }
            }?.reset()
        }
    }

    override fun loadOnTowerConnect() {
        val messageService = IMessageService.Service
        val realmService = IRealmService.Service

        messageService.sendMessage(arrayOf("nesmole"), "§6${IRealmService.Service.realmId} ${POSITIVE}запущен.")
        realmService.transfer(realmService.realmId, arrayOf("nesmole"))
    }
}