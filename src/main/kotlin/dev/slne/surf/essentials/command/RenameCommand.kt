package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import net.kyori.adventure.text.Component

fun renameCommand() = commandTree("rename") {
    withPermission(EssentialsPermissionRegistry.RENAME_COMMAND)

    greedyStringArgument("content") {
        playerExecutor { player, arguments ->
            val item = player.inventory.itemInMainHand
            val content: String by arguments

            if (item.isEmpty) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du musst ein Item in der Hand halten.")
                }
                return@playerExecutor
            }

            if (content.length > 256 && !player.hasPermission(EssentialsPermissionRegistry.RENAME_COMMAND_BYPASS)) {
                player.sendText {
                    appendErrorPrefix()
                    error("Der Name darf nicht länger als 256 Zeichen sein.")
                }
                return@playerExecutor
            }

            val displayName =
                if (player.hasPermission(EssentialsPermissionRegistry.RENAME_COMMAND_MINIMESSAGE)) miniMessage.deserialize(
                    content
                ) else Component.text(content)

            item.editMeta {
                it.displayName(displayName)
            }

            player.sendText {
                appendSuccessPrefix()
                success("Das Item wurde umbenannt.")
            }
        }
    }
}