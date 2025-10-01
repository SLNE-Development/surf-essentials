package dev.slne.surf.essentials

import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.essentials.command.*

object PaperCommandManager {
    fun registerAll() {
        CommandAPI.unregister("op")
        CommandAPI.unregister("deop")
        CommandAPI.unregister("advancement")
        CommandAPI.unregister("list")
        CommandAPI.unregister("clear")
        CommandAPI.unregister("gamemode", true)

        advancementCommand()
        opCommand()
        deopCommand()
        flyCommand()
        healCommand()
        hatCommand()
        listCommand()
        trashCommand()
        backCommand()
        spawnCommand()
        clearCommand()
        gameModeCommand()
        repairCommand()
        changeSlotCommand()
        strikeCommand()
        giveCommand()
    }
}