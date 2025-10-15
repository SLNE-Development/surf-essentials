package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun trashCommand() = commandTree("trash") {
    withPermission(EssentialsPermissionRegistry.TRASH_COMMAND)
    playerExecutor { executor, _ ->
        executor.openInventory(Bukkit.createInventory(executor, 54, buildText {
            info("Mülleimer".toSmallCaps(), TextDecoration.BOLD)
        }))

        executor.sendText {
            appendPrefix()
            success("Du hast deinen Mülleimer geöffnet.")
        }

        executor.playSound(sound {
            source(Sound.Source.UI)
            type(org.bukkit.Sound.BLOCK_BARREL_OPEN)
        }, Sound.Emitter.self())
    }
    withPermission(EssentialsPermissionRegistry.TRASH_COMMAND_OTHERS)
    entitySelectorArgumentOnePlayer("player") {
        anyExecutor { executor, args ->
            val player: Player by args

            player.openInventory(Bukkit.createInventory(player, 54, buildText {
                info("Mülleimer".toSmallCaps(), TextDecoration.BOLD)
            }))

            player.sendText {
                appendPrefix()
                success("Dein Mülleimer wurde von ")
                variableValue(executor.name)
                success(" geöffnet.")
            }

            executor.sendText {
                appendPrefix()
                success("Du hast den Mülleimer für ")
                variableValue(player.name)
                success(" geöffnet.")
            }

            player.playSound(sound {
                source(Sound.Source.UI)
                type(org.bukkit.Sound.BLOCK_BARREL_OPEN)
            }, Sound.Emitter.self())
        }
    }
}