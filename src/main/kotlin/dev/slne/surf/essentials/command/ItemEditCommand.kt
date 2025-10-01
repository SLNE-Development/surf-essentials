package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.enchantments.Enchantment
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

                if (itemInHand.isEmpty) {
                    player.sendText {
                        appendPrefix()
                        error("Du musst ein Item in der Hand halten.")
                    }
                    return@playerExecutor
                }

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

                    if (itemInHand.isEmpty) {
                        player.sendText {
                            appendPrefix()
                            error("Du musst ein Item in der Hand halten.")
                        }
                        return@playerExecutor
                    }

                    itemInHand.editMeta(ItemMeta::class.java) {
                        val lore = it.lore()?.toMutableList() ?: mutableListOf()

                        val safeIndex = line.coerceIn(0, lore.size)
                        lore.add(safeIndex, displayLoreContent)

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

    literalArgument("enchant") {
        enchantmentArgument("enchant") {
            integerArgument("level") {
                withPermission(EssentialsPermissionRegistry.ITEM_EDIT_COMMAND_ENCHANT)
                playerExecutor { player, args ->
                    val enchant: Enchantment by args
                    val level: Int by args
                    val itemInHand = player.inventory.itemInMainHand
                    
                    if (itemInHand.isEmpty) {
                        player.sendText {
                            appendPrefix()
                            error("Du musst ein Item in der Hand halten.")
                        }
                        return@playerExecutor
                    }

                    itemInHand.editMeta(ItemMeta::class.java) {
                        it.addEnchant(enchant, level, true)
                    }

                    player.sendText {
                        appendPrefix()
                        success("Der Verzauberung ")
                        variableValue(enchant.key.key)
                        success(" mit der Stufe ")
                        variableValue(level.toString())
                        success(" wurde zum Item hinzugefügt.")
                    }
                }
            }
        }
    }
}