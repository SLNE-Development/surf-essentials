package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.World

fun seedCommand() = commandTree("seed") {
    withPermission(EssentialsPermissionRegistry.SEED_COMMAND)
    playerExecutor { player, _ ->
        val world = player.world
        player.sendText {
            appendPrefix()
            info("Der Ursprungswert der Welt ")
            variableValue(world.name)
            info(" ist ")
            variableValue(world.seed)
            info(".")
            clickCopiesToClipboard(world.seed.toString())
            hoverEvent(buildText {
                info("Klicke, um den Seed zu kopieren.".toSmallCaps())
            })
        }
    }
    worldArgument("world") {
        anyExecutor { executor, args ->
            val world: World by args

            executor.sendText {
                appendPrefix()
                info("Der Ursprungswert der Welt ")
                variableValue(world.name)
                info(" ist ")
                variableValue(world.seed)
                info(".")
                clickCopiesToClipboard(world.seed.toString())
                hoverEvent(buildText {
                    info("Klicke, um den Seed zu kopieren.".toSmallCaps())
                })
            }
        }
    }
}