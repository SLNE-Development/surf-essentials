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
        CommandAPI.unregister("give")
        CommandAPI.unregister("teleport")
        CommandAPI.unregister("enchant")
        CommandAPI.unregister("whitelist")
        CommandAPI.unregister("summon")

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
        gameModeOfflineCommand()
        repairCommand()
        changeSlotCommand()
        strikeCommand()
        giveCommand()
        infoCommand()
        itemEditCommand()
        signCommand()
        teleportCommand()
        teleportOfflineCommand()
        teleportRandomCommand()
        teleportToTopCommand()
        enchantmentCommand()
        whitelistCommand()
        hurtCommand()
        summonCommand()
        fillStackCommand()
    }
}