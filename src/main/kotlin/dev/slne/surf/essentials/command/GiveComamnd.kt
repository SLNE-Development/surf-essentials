package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun giveCommand() = commandTree("give") {
    withPermission(EssentialsPermissionRegistry.GIVE_COMMAND)
    itemStackArgument("item") {
        playerExecutor { player, args ->
            val itemStack: ItemStack by args

            player.inventory.addItem(itemStack)

            player.sendText {
                appendPrefix()
                success("Du hast ")
                variableValue(itemStack.amount.toString())
                success("x ")
                variableValue(itemStack.type.name)
                success(" erhalten.")
            }
        }
        entitySelectorArgumentManyPlayers("players") {
            withPermission(EssentialsPermissionRegistry.GIVE_COMMAND_OTHERS)
            anyExecutor { executor, args ->
                val itemStack: ItemStack by args
                val players: Collection<Player> by args

                players.forEach { it.inventory.addItem(itemStack) }

                executor.sendText {
                    appendPrefix()
                    success("Du hast ")
                    variableValue(itemStack.amount.toString())
                    success("x ")
                    variableValue(itemStack.type.name)
                    success(" an ")
                    variableValue(players.size.toString())
                    success(" Spieler vergeben.")
                }

                players.forEach { player ->
                    player.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(itemStack.amount.toString())
                        success("x ")
                        variableValue(itemStack.type.name)
                        success(" erhalten.")
                    }
                }
            }
        }
    }
}