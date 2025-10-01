package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.meta.ItemMeta

fun signCommand() = commandTree("sign") {
    withPermission(EssentialsPermissionRegistry.SIGN_COMMAND)
    greedyStringArgument("text") {
        playerExecutor { player, args ->
            val text: String by args
            val component = MiniMessage.miniMessage().deserialize(text)
            val item = player.inventory.itemInMainHand

            if (item.isEmpty) {
                player.sendText {
                    appendPrefix()
                    error("Du musst ein Item in der Hand halten.")
                }
                return@playerExecutor
            }

            item.editMeta(ItemMeta::class.java) {
                val lore = it.lore() ?: mutableListOf<Component>()

                lore.addLast(buildText {
                    text("Signiert von ".toSmallCaps()).decorate(TextDecoration.BOLD)
                    variableValue(player.name.toSmallCaps(), TextDecoration.BOLD)
                })
                lore.addLast(buildText {
                    append(component)
                })

                it.lore(lore)
            }

            player.sendText {
                appendPrefix()
                success("Das Item wurde signiert.")
            }
        }
    }
}