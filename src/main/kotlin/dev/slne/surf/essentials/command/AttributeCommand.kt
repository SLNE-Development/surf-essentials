package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers

fun attributeCommand() = commandTree("attribute") {
    entitySelectorArgumentManyPlayers("players") {
        //TODO: Implement attribute command
    }
}