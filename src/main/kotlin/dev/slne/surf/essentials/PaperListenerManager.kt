package dev.slne.surf.essentials

import dev.slne.surf.api.paper.event.register
import dev.slne.surf.essentials.listener.*

object PaperListenerManager {
    fun registerAll() {
        AdvancementListener.register()
        FlyCorrectionListener.register()
        GameModeSwitcherCorrectionListener.register()
        TeleportationListener.register()
        UnknownCommandListener.register()
        WorldListener.register()
        SpecialItemListener.register()
        HungerListener.register()
    }
}