package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.entity.Player

fun opCommand() = commandTree("op") {
    withPermission(EssentialsPermissionRegistry.OP_COMMAND)
    entitySelectorArgumentManyPlayers("players") {
        anyExecutor { executor, args ->
            val players: Collection<Player> by args
            val successfulPlayers = mutableObjectSetOf<Player>()

            players.forEach {
                if (!it.isOp) {
                    it.isOp = true
                    successfulPlayers.add(it)
                }
            }

            if (successfulPlayers.isEmpty()) {
                executor.sendText {
                    appendPrefix()
                    error("Es wurde kein Spieler zum Operator ernannt.")
                }
                return@anyExecutor
            }

            if (successfulPlayers.size < players.size) {
                executor.sendText {
                    appendPrefix()
                    error("Einige Spieler konnten nicht zum Operator ernannt werden, da sie bereits Operator sind.")
                    hoverEvent(buildText {
                        info("Erfolgreich zum Operator ernannte Spieler")
                        info(":")
                        appendSpace()
                        variableValue(successfulPlayers.joinToString(", ") { it.name })
                    })
                }
                return@anyExecutor
            }

            executor.sendText {
                appendPrefix()
                success("Die ausgewählten Spieler wurden erfolgreich zum Operator ernannt.")
                hoverEvent(buildText {
                    info("Erfolgreich zum Operator ernannte Spieler")
                    info(":")
                    appendSpace()
                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                })
            }
        }
    }
}