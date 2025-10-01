package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun clearCommand() = commandTree("clear") {
    withPermission(EssentialsPermissionRegistry.CLEAR_COMMAND)
    playerExecutor { player, _ ->
        if (player.inventory.isEmpty) {
            player.sendText {
                appendPrefix()
                error("Dein Inventar ist bereits leer.")
            }
            return@playerExecutor
        }

        player.inventory.clear()

        player.sendText {
            appendPrefix()
            success("Dein Inventar wurde geleert.")
        }

        itemStackArgument("type") {
            playerExecutor { player, args ->
                val type: ItemStack by args

                val amount = player.inventory.filter { it.type == type.type }.sumOf { it.amount }

                if (amount == 0) {
                    player.sendText {
                        appendPrefix()
                        error("In deinem Inventar wurden keine Items vom Typ ")
                        variableValue(type.type.name)
                        error(" gefunden.")
                    }
                    return@playerExecutor
                }

                player.inventory.remove(type.type)

                player.sendText {
                    appendPrefix()
                    success("Es wurden ")
                    variableValue(amount.toString())
                    success(" Items aus deinem Inventar entfernt.")
                }
            }
        }
    }

    entitySelectorArgumentManyPlayers("players") {
        withPermission(EssentialsPermissionRegistry.CLEAR_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val players: Collection<Player> by args
            val clearedPlayers = mutableObjectSetOf<Player>()
            val alreadyEmptyPlayers = mutableObjectSetOf<Player>()

            for (player in players) {
                if (player.inventory.isEmpty) {
                    alreadyEmptyPlayers.add(player)
                } else {
                    player.inventory.clear()
                    clearedPlayers.add(player)
                    player.sendText {
                        appendPrefix()
                        success("Dein Inventar wurde geleert.")
                    }
                }
            }

            if (clearedPlayers.isNotEmpty()) {
                executor.sendText {
                    appendPrefix()
                    success("Das Inventar von ")
                    variableValue(clearedPlayers.joinToString(", ") { it.name })
                    success(" wurde geleert.")
                }
            }

            if (alreadyEmptyPlayers.isNotEmpty()) {
                executor.sendText {
                    appendPrefix()
                    error("Das Inventar von ")
                    variableValue(alreadyEmptyPlayers.joinToString(", ") { it.name })
                    error(" war bereits leer.")
                }
            }
        }
        itemStackArgument("type") {
            anyExecutor { executor, args ->
                val players: Collection<Player> by args
                val type: ItemStack by args
                val clearedPlayers = mutableObject2ObjectMapOf<Player, Int>()
                val notFoundPlayers = mutableObjectSetOf<Player>()

                for (player in players) {
                    val amount =
                        player.inventory.filter { it.type == type.type }.sumOf { it.amount }

                    if (amount == 0) {
                        notFoundPlayers.add(player)
                    } else {
                        player.inventory.remove(type.type)
                        clearedPlayers[player] = amount
                        player.sendText {
                            appendPrefix()
                            success("Es wurden ")
                            variableValue(amount.toString())
                            success(" Items aus deinem Inventar entfernt.")
                        }
                    }
                }

                if (clearedPlayers.isNotEmpty()) {
                    executor.sendText {
                        appendPrefix()
                        success("Es wurden ${clearedPlayers.values.sum()} Items aus ${clearedPlayers.size} Inventaren entfernt.")
                    }
                }

                if (notFoundPlayers.isNotEmpty()) {
                    executor.sendText {
                        appendPrefix()
                        error("In den Inventaren von ")
                        variableValue(notFoundPlayers.joinToString(", ") { it.name })
                        error(" wurden keine Items vom Typ ")
                        variableValue(type.type.name)
                        error(" gefunden.")
                    }
                }
            }
        }
    }
}