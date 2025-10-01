package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.appendLinkButton
import dev.slne.surf.essentials.util.appendPrefixedKeyArrowLine
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun infoCommand() = commandTree("pinfo") {
    withPermission(EssentialsPermissionRegistry.INFO_COMMAND)
    entitySelectorArgumentOnePlayer("player") {
        anyExecutor { executor, args ->
            val player: Player by args

            val name = player.name
            val uuid = player.uniqueId.toString()
            val ip = "${player.address.hostName}:${player.address.port}"
            val client = player.clientBrandName
            val labyProfile = "https://laby.net/${player.name}"
            val nameMcProfile = "https://de.namemc.com/profile/${player.name}"
            val health =
                "${player.health}/${player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0}"
            val food = "${player.foodLevel}/20"
            val location =
                "Welt: ${player.world.name}, X: ${player.location.blockX}, Y: ${player.location.blockY}, Z: ${player.location.blockZ}"

            val ping = "${player.ping}ms"

            executor.sendText {
                appendPrefix()
                info("Spielerinformationen für ")
                variableValue(player.name.toSmallCaps())

                appendPrefixedKeyArrowLine("Name", name)
                appendPrefixedKeyArrowLine("UUID", uuid)
                appendPrefixedKeyArrowLine("IP", ip)
                appendPrefixedKeyArrowLine("Client", client ?: "Unbekannt")
                appendPrefixedKeyArrowLine("Leben", health)
                appendPrefixedKeyArrowLine("Hunger", food)
                appendPrefixedKeyArrowLine("Ping", ping)
                appendPrefixedKeyArrowLine("Standort", location)

                appendNewPrefixedLine {
                    appendLinkButton("Laby.net Profil", labyProfile)
                    appendSpace()
                    appendLinkButton("NameMC Profil", nameMcProfile)
                }
            }
        }
    }
}

