package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun clearCommand() = commandTree("clear") {
    withPermission(EssentialsPermissionRegistry.CLEAR_COMMAND)
    playerExecutor { player, _ ->
        if (player.inventory.isEmpty) {
            player.sendText {
                appendErrorPrefix()
                error("Dein Inventar ist bereits leer.")
            }
            return@playerExecutor
        }

        player.inventory.clear()

        player.sendText {
            appendSuccessPrefix()
            success("Dein Inventar wurde geleert.")
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
                        appendSuccessPrefix()
                        success("Dein Inventar wurde geleert.")
                    }
                }
            }

            if (clearedPlayers.isNotEmpty()) {
                executor.sendText {
                    appendSuccessPrefix()
                    success("Das Inventar von ")
                    variableValue(clearedPlayers.joinToString(", ") { it.name })
                    success(" wurde geleert.")
                }
            }

            if (alreadyEmptyPlayers.isNotEmpty()) {
                executor.sendText {
                    appendErrorPrefix()
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
                    val amount = player.inventory.contents
                        .filterNotNull()
                        .filter { it.type == type.type }
                        .sumOf { it.amount }


                    if (amount == 0) {
                        notFoundPlayers.add(player)
                    } else {
                        player.inventory.remove(type.type)
                        clearedPlayers[player] = amount
                        player.sendText {
                            appendSuccessPrefix()
                            success("Es wurden ")
                            variableValue(amount.toString())
                            success(" Items aus deinem Inventar entfernt.")
                        }
                    }
                }

                if (clearedPlayers.isNotEmpty()) {
                    executor.sendText {
                        appendSuccessPrefix()
                        success("Es wurden ${clearedPlayers.values.sum()} Items aus ${clearedPlayers.size} Inventaren entfernt.")
                    }
                }

                if (notFoundPlayers.isNotEmpty()) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("In den Inventaren von ")
                        variableValue(notFoundPlayers.joinToString(", ") { it.name })
                        error(" wurden keine  ")
                        append(
                            Component.translatable(type.type.translationKey())
                                .colorIfAbsent(Colors.VARIABLE_VALUE)
                        )
                        error(" gefunden.")
                    }
                }
            }
        }
    }
}