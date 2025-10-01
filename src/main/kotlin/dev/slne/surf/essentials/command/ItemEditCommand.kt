package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.meta.ItemMeta

fun itemEditCommand() = commandTree("itemedit") {
    withPermission(EssentialsPermissionRegistry.ITEM_EDIT_COMMAND)
    literalArgument("displayname") {
        greedyStringArgument("name") {
            withPermission(EssentialsPermissionRegistry.ITEM_EDIT_COMMAND_NAME)
            playerExecutor { player, args ->
                val name: String by args
                val displayName = MiniMessage.miniMessage().deserialize(name)
                val itemInHand = player.inventory.itemInMainHand

                itemInHand.editMeta(ItemMeta::class.java) {
                    it.displayName(displayName)
                }

                player.sendText {
                    appendPrefix()
                    success("Der Name des Items wurde zu ")
                    append(displayName)
                    success(" geändert.")
                }
            }
        }
    }

    literalArgument("lore") {
        integerArgument("line") {
            greedyStringArgument("loreContent") {
                withPermission(EssentialsPermissionRegistry.ITEM_EDIT_COMMAND_LORE)
                playerExecutor { player, args ->
                    val line: Int by args
                    val loreContent: String by args
                    val displayLoreContent = MiniMessage.miniMessage().deserialize(loreContent)
                    val itemInHand = player.inventory.itemInMainHand

                    itemInHand.editMeta(ItemMeta::class.java) {
                        val lore = it.lore() ?: mutableListOf()
                        lore[line] = displayLoreContent

                        it.lore(lore)
                    }

                    player.sendText {
                        appendPrefix()
                        success("Die Lore-Zeile ")
                        variableValue(line)
                        success(" des Items wurde zu ")
                        append(displayLoreContent)
                        success(" geändert.")
                    }
                }
            }
        }
    }
}