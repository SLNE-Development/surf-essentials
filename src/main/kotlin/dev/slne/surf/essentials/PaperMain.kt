package dev.slne.surf.essentials

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        super.onLoad()
    }

    override fun onEnable() {
        PaperCommandManager.registerAll()
        PaperListenerManager.registerAll()
    }

    override fun onDisable() {
        super.onDisable()
    }
}