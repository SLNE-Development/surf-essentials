package dev.slne.surf.essentials

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.essentials.service.settingsService
import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        super.onLoad()
    }

    override suspend fun onEnableAsync() {
        PaperCommandManager.registerAll()
        PaperListenerManager.registerAll()

        if (hasSettingsApi()) {
            settingsService.register()
        }
    }

    override fun onDisable() {
        super.onDisable()
    }

    fun hasSettingsApi() = pluginManager.isPluginEnabled("surf-settings-paper")
}