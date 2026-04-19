package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.service.SignService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import net.luckperms.api.LuckPermsProvider

fun signCommand() = commandTree("sign") {
    withPermission(EssentialsPermissionRegistry.SIGN_COMMAND)
    greedyStringArgument("text", optional = true) {
        playerExecutor { player, args ->
            val text: String? by args
            val item = player.inventory.itemInMainHand

            if (item.isEmpty) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du musst ein Item in der Hand halten.")
                }
                return@playerExecutor
            }

            val prefix =
                LuckPermsProvider.get().userManager.getUser(player.uniqueId)?.cachedData?.metaData?.prefix
                    ?: ""

            SignService.sign(item, "$prefix${player.name}", text)

            player.sendText {
                appendSuccessPrefix()
                success("Das Item wurde signiert.")
            }
        }
    }
}