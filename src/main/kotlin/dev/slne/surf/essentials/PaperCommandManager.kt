package dev.slne.surf.essentials

import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.essentials.command.advancementCommand
import dev.slne.surf.essentials.command.deopCommand
import dev.slne.surf.essentials.command.opCommand

object PaperCommandManager {
    fun registerAll() {
        CommandAPI.unregister("op")
        CommandAPI.unregister("deop")
        CommandAPI.unregister("advancement")

        advancementCommand()
        opCommand()
        deopCommand()
    }
}