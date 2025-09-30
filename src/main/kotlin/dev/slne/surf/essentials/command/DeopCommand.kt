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

fun deopCommand() = commandTree("deop") {
    withPermission(EssentialsPermissionRegistry.DEOP_COMMAND)
    entitySelectorArgumentManyPlayers("players") {
        anyExecutor { executor, args ->
            val players: Collection<Player> by args
            val successfulPlayers = mutableObjectSetOf<Player>()

            players.forEach {
                if (it.isOp) {
                    it.isOp = false
                    successfulPlayers.add(it)
                }
            }

            if (successfulPlayers.isEmpty()) {
                executor.sendText {
                    appendPrefix()
                    error("Es wurde keinem Spieler der Operator-Status entzogen.")
                }
                return@anyExecutor
            }

            if (successfulPlayers.size < players.size) {
                executor.sendText {
                    appendPrefix()
                    error("Einigen Spielern konnte der Operator-Status nicht entzogen werden, da sie keinen Operator-Status besitzen.")
                    hoverEvent(buildText {
                        info("Erfolgreich degradierte Spieler")
                        info(":")
                        appendSpace()
                        variableValue(successfulPlayers.joinToString(", ") { it.name })
                    })
                }
                return@anyExecutor
            }

            executor.sendText {
                appendPrefix()
                success("Den ausgewählten Spielern wurde erfolgreich der Operator-Status entzogen.")
                hoverEvent(buildText {
                    info("Erfolgreich degradierte Spieler")
                    info(":")
                    appendSpace()
                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                })
            }
        }
    }
}