package dev.slne.surf.essentials

import dev.slne.surf.essentials.listener.*
import dev.slne.surf.surfapi.bukkit.api.event.register

object PaperListenerManager {
    fun registerAll() {
        AdvancementListener.register()
        FlyCorrectionListener.register()
        GameModeSwitcherCorrectionListener.register()
        TeleportationListener.register()
        UnknownCommandListener.register()
    }
}