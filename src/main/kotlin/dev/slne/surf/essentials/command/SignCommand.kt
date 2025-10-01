package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.meta.ItemMeta

fun signCommand() = commandTree("sign") {
    withPermission(EssentialsPermissionRegistry.SIGN_COMMAND)
    greedyStringArgument("text", optional = true) {
        playerExecutor { player, args ->
            val text: String? by args
            val component = text?.let { MiniMessage.miniMessage().deserialize(it) }
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
                    text("Signiert von ".toSmallCaps(), Colors.WHITE)
                    variableValue(player.name.toSmallCaps(), TextDecoration.BOLD)
                }.decoration(TextDecoration.ITALIC, false))

                component?.let { comp ->
                    lore.addLast(buildText {
                        append(comp).colorIfAbsent(Colors.WHITE)
                    }.decoration(TextDecoration.ITALIC, false))
                }

                it.lore(lore)
            }

            player.sendText {
                appendPrefix()
                success("Das Item wurde signiert.")
            }
        }
    }
}