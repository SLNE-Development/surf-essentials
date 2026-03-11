package dev.slne.surf.essentials

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.essentials.listener.CanvasListener
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import dev.slne.surf.surfapi.bukkit.api.surfBukkitApi
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        super.onLoad()
    }

    override fun onEnable() {
        PaperCommandManager.registerAll()
        PaperListenerManager.registerAll()

        if (surfBukkitApi.isCanvasMc) {
            CanvasListener.register()
        }
    }

    override fun onDisable() {
        super.onDisable()
    }

    fun isSurvivalServer() = pluginManager.isPluginEnabled("surf-freebuild-bukkit")
}