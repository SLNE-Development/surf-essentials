package dev.slne.surf.essentials

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.SurfApiPaper
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.api.paper.packet.SurfPaperPacketApi
import dev.slne.surf.essentials.handler.SignVisualHandler
import dev.slne.surf.essentials.listener.CanvasListener
import dev.slne.surf.essentials.service.SignService
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        super.onLoad()
    }

    override fun onEnable() {
        PaperCommandManager.registerAll()
        PaperListenerManager.registerAll()
        SurfPaperPacketApi.registerPacketLoreListener(
            plugin,
            SignService.keySignedBy,
            SignVisualHandler
        )

        if (SurfApiPaper.isCanvasMc) {
            CanvasListener.register()
        }
    }

    override fun onDisable() {
        super.onDisable()
    }

    fun isSurvivalServer() = pluginManager.isPluginEnabled("surf-freebuild-paper")
}