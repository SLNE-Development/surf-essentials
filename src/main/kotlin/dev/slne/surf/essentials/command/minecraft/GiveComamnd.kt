package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
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
                    appendSuccessPrefix()
                    success("Du hast ")
                    translatable(itemStack.type.translationKey())
                    success(" an ")
                    variableValue(players.size.toString())
                    success(" Spieler vergeben.")
                }

                players.forEach { player ->
                    player.sendText {
                        appendSuccessPrefix()
                        success("Du hast ")
                        translatable(itemStack.type.translationKey())
                        success(" erhalten.")
                    }
                }
            }

            integerArgument("amount") {
                anyExecutor { executor, args ->
                    val itemStack: ItemStack by args
                    val amount: Int by args
                    val players: Collection<Player> by args

                    itemStack.amount = amount
                    players.forEach { it.inventory.addItem(itemStack) }

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Du hast ")
                        variableValue(amount.toString())
                        success("x ")
                        translatable(itemStack.type.translationKey())
                        success(" an ")
                        variableValue(players.size.toString())
                        success(" Spieler vergeben.")
                    }

                    players.forEach { player ->
                        player.sendText {
                            appendSuccessPrefix()
                            success("Du hast ")
                            variableValue(amount.toString())
                            success("x ")
                            translatable(itemStack.type.translationKey())
                            success(" erhalten.")
                        }
                    }
                }
            }
        }
    }
}