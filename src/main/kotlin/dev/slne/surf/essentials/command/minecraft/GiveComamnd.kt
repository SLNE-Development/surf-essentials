package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun giveCommand() = commandTree("give") {
    withPermission(EssentialsPermissionRegistry.GIVE_COMMAND)
    entitySelectorArgumentManyPlayers("players") {
        itemStackArgument("itemStack") {
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

            integerArgument("amount") {
                anyExecutor { executor, args ->
                    val amount: Int by args
                    val itemStack: ItemStack by args
                    val players: Collection<Player> by args

                    val itemToGive = itemStack.clone().apply { this.amount = amount }

                    players.forEach { it.inventory.addItem(itemToGive) }

                    executor.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(amount.toString())
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
                            variableValue(amount.toString())
                            success("x ")
                            variableValue(itemStack.type.name)
                            success(" erhalten.")
                        }
                    }
                }
            }
        }
    }
}