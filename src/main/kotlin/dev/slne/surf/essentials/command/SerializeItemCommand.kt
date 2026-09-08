package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import net.kyori.adventure.text.format.TextDecoration
import kotlin.io.encoding.Base64

fun serializeItemCommand() = commandTree("serializeitem") {
    withPermission(EssentialsPermissionRegistry.SERIALIZE_ITEM_COMMAND)
    playerExecutor { player, _ ->
        val item = player.inventory.itemInMainHand
        val bytes = item.serializeAsBytes()
        val base64 = Base64.encode(bytes)

        player.sendText {
            appendSuccessPrefix()
            success("Das Item ")
            translatable(item.type.translationKey())
            success(" wurde in ")
            variableValue(bytes.size)
            success(" Bytes serialisiert und in Base64 kodiert. ")
            append {
                decorate(TextDecoration.ITALIC)
                spacer("kopieren...")
                clickCopiesToClipboard(base64)
            }
        }
    }
}